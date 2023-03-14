/*
 * Copyright (c) 2021-2023. ForteScarlet.
 *
 * This file is part of simbot-component-tencent-guild.
 *
 * simbot-component-tencent-guild is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * simbot-component-tencent-guild is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with simbot-component-tencent-guild. If not, see <https://www.gnu.org/licenses/>.
 */

package love.forte.simbot.component.qguild.internal

import kotlinx.coroutines.*
import love.forte.simbot.ID
import love.forte.simbot.bot.BotAlreadyRegisteredException
import love.forte.simbot.component.qguild.QGBot
import love.forte.simbot.component.qguild.QGBotManager
import love.forte.simbot.component.qguild.QGBotManagerConfiguration
import love.forte.simbot.component.qguild.QQGuildComponent
import love.forte.simbot.component.qguild.event.QGBotRegisteredEvent
import love.forte.simbot.component.qguild.internal.event.QGBotRegisteredEventImpl
import love.forte.simbot.event.EventProcessor
import love.forte.simbot.event.pushIfProcessable
import love.forte.simbot.logger.LoggerFactory
import love.forte.simbot.qguild.BotFactory
import love.forte.simbot.qguild.ConfigurableBotConfiguration
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
internal class QGBotManagerImpl(
    override val eventProcessor: EventProcessor,
    override val configuration: QGBotManagerConfiguration,
    override val component: QQGuildComponent,
) : QGBotManager() {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(QGBotManagerImpl::class)
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

    private var botMap = ConcurrentHashMap<String, QGBotImpl>()


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

    override fun get(id: ID): QGBot? {
        lock.read {
            if (completableJob.isCancelled) throw IllegalStateException("This manager has already cancelled.")

            return botMap[id.toString()]
        }
    }

    override fun all(): List<QGBot> {
        return botMap.values.toList()
    }


    override fun register(
        appId: String,
        appKey: String,
        token: String,
        block: ConfigurableBotConfiguration.() -> Unit,
    ): QGBot {
        val configure = configuration.botConfigure
        lock.write {
            val sourceBot = BotFactory.create(appId, appKey, token) {
                configure(appId, appKey, token)
                block()
                if (coroutineContext[Job] == null) {
                    coroutineContext += completableJob
                }
            }
            // check botInfo
            logger.info("Registered bot appId: {}", sourceBot.ticket.appId)
            return botMap.compute(appId) { key, old ->
                if (old != null) throw BotAlreadyRegisteredException(key)

                QGBotImpl(sourceBot, this, eventProcessor, component).apply {
                    coroutineContext[Job]!!.invokeOnCompletion {
                        // remove self on completion
                        botMap.remove(key)
                    }
                }
            }!!.also { bot ->
                bot.launch {
                    eventProcessor.pushIfProcessable(QGBotRegisteredEvent) {
                        QGBotRegisteredEventImpl(bot)
                    }
                }
            }
            // return guildBot
        }
    }
}
