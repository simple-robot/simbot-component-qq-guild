/*
 *  Copyright (c) 2021-2022 ForteScarlet <ForteScarlet@163.com>
 *
 *  本文件是 simbot-component-tencent-guild 的一部分。
 *
 *  simbot-component-tencent-guild 是自由软件：你可以再分发之和/或依照由自由软件基金会发布的 GNU 通用公共许可证修改之，无论是版本 3 许可证，还是（按你的决定）任何以后版都可以。
 *
 *  发布 simbot-component-tencent-guild 是希望它能有用，但是并无保障;甚至连可销售和符合某个特定的目的都不保证。请参看 GNU 通用公共许可证，了解详情。
 *
 *  你应该随程序获得一份 GNU 通用公共许可证的复本。如果没有，请看:
 *  https://www.gnu.org/licenses
 *  https://www.gnu.org/licenses/gpl-3.0-standalone.html
 *  https://www.gnu.org/licenses/lgpl-3.0-standalone.html
 *
 *
 */

package love.forte.simbot.component.tencentguild.internal

import kotlinx.coroutines.*
import love.forte.simbot.BotAlreadyRegisteredException
import love.forte.simbot.Component
import love.forte.simbot.ID
import love.forte.simbot.LoggerFactory
import love.forte.simbot.component.tencentguild.ComponentTencentGuild
import love.forte.simbot.component.tencentguild.TencentGuildBot
import love.forte.simbot.component.tencentguild.TencentGuildBotManager
import love.forte.simbot.component.tencentguild.TencentGuildBotManagerConfiguration
import love.forte.simbot.tencentguild.TencentBotConfiguration
import love.forte.simbot.tencentguild.tencentBot
import org.slf4j.Logger
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write
import kotlin.coroutines.CoroutineContext

/**
 *
 * @author ForteScarlet
 */
internal class TencentGuildBotManagerImpl(
    override val configuration: TencentGuildBotManagerConfiguration
) : TencentGuildBotManager() {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(TencentGuildBotManagerImpl::class)
    }

    private val completableJob: CompletableJob
    override val coroutineContext: CoroutineContext

    init {
        val parentContext = configuration.parentCoroutineContext
        val parentJob = parentContext[Job]
        completableJob = SupervisorJob(parentJob)
        coroutineContext = parentContext.minusKey(Job) + completableJob
    }

    override val logger: Logger
        get() = LOGGER


    // private val isCanceled = AtomicBoolean(false)

    private val lock = ReentrantReadWriteLock()
    private val eventProcessor = configuration.eventProcessor

    override val isActive: Boolean
        get() = completableJob.isActive

    override val isCancelled: Boolean
        get() = completableJob.isCancelled

    override val isStarted: Boolean
        get() = completableJob.isActive

    override fun invokeOnCompletion(handler: CompletionHandler) {
        completableJob.invokeOnCompletion(handler)
    }

    override suspend fun join() {
        completableJob.join()
    }

    // nothing to start
    override suspend fun start(): Boolean {
        return !completableJob.isCompleted
    }

    private var botMap = ConcurrentHashMap<String, TencentGuildBotImpl>()

    override val component: Component
        get() = ComponentTencentGuild.component


    override suspend fun doCancel(reason: Throwable?): Boolean {
        lock.write {
            val cancelled = completableJob.isCancelled
            for (bot in botMap.values.toList()) {
                bot.cancel(reason)
            }
            if (cancelled) {
                return false
            }

            if (reason != null) {
                completableJob.cancel(reason.localizedMessage, reason)
            } else {
                completableJob.cancel()
            }
            completableJob.join()
            return true
        }
    }

    override fun get(id: ID): TencentGuildBot? {
        lock.read {
            if (completableJob.isCancelled) throw IllegalStateException("This manager has already cancelled.")

            return botMap[id.toString()]
        }
    }

    override fun all(): Sequence<TencentGuildBot> {
        return botMap.values.asSequence()
    }


    override fun register(
        appId: String,
        appKey: String,
        token: String,
        block: TencentBotConfiguration.() -> Unit
    ): TencentGuildBot {
        val configure = configuration.botConfigure
        lock.write {
            val sourceBot = tencentBot(appId, appKey, token) {
                configure(appId, appKey, token)
                block()
                if (coroutineContext[Job] == null) {
                    coroutineContext += completableJob
                }
            }
            // check botInfo
            logger.info("Registered bot info: {}", sourceBot.botInfo)
            return botMap.compute(appId) { key, old ->
                if (old != null) throw BotAlreadyRegisteredException(key)

                TencentGuildBotImpl(sourceBot, this, eventProcessor).apply {
                    coroutineContext[Job]!!.invokeOnCompletion {
                        // remove self on completion
                        botMap.remove(key)
                    }
                }
            }!!
            // return guildBot
        }
    }
}