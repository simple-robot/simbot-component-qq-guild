/*
 *  Copyright (c) 2021 ForteScarlet <https://github.com/ForteScarlet>
 *
 *  根据 Apache License 2.0 获得许可；
 *  除非遵守许可，否则您不得使用此文件。
 *  您可以在以下网址获取许可证副本：
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *   有关许可证下的权限和限制的具体语言，请参见许可证。
 */

package love.forte.simbot.component.tencentguild.internal

import kotlinx.coroutines.CompletionHandler
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import love.forte.simbot.Component
import love.forte.simbot.ID
import love.forte.simbot.LoggerFactory
import love.forte.simbot.component.tencentguild.ComponentTencentGuild
import love.forte.simbot.component.tencentguild.TencentGuildBot
import love.forte.simbot.component.tencentguild.TencentGuildBotManager
import love.forte.simbot.component.tencentguild.TencentGuildBotManagerConfiguration
import love.forte.simbot.tencentguild.TencentBotConfiguration
import love.forte.simbot.tencentguild.tencentBot
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write

/**
 *
 * @author ForteScarlet
 */
internal class TencentGuildBotManagerImpl(
    override val configuration: TencentGuildBotManagerConfiguration
) : TencentGuildBotManager() {
    companion object {
        private val logger = LoggerFactory.getLogger(TencentGuildBotManagerImpl::class)
    }

    private val completableJob = Job()

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

    private var botMap = mutableMapOf<String, TencentGuildBotImpl>()

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
            }
            // check botInfo
            logger.info("Registered bot info: {}", sourceBot.botInfo)
            val guildBot = TencentGuildBotImpl(sourceBot, this, eventProcessor)
            botMap.compute(appId) { key, old ->
                if (old != null) throw IllegalStateException("Bot appId '$key' already registered.")
                guildBot.apply {
                    coroutineContext[Job]!!.invokeOnCompletion {
                        // remove self on completion
                        botMap.remove(key)
                    }
                }
            }
            return guildBot
        }
    }
}