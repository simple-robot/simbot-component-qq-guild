/*
 * Copyright (c) 2021-2023. ForteScarlet.
 *
 * This file is part of simbot-component-qq-guild.
 *
 * simbot-component-qq-guild is free software: you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * simbot-component-qq-guild is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with simbot-component-qq-guild.
 * If not, see <https://www.gnu.org/licenses/>.
 */

package love.forte.simbot.component.qguild.internal

import kotlinx.coroutines.*
import love.forte.simbot.ID
import love.forte.simbot.bot.BotAlreadyRegisteredException
import love.forte.simbot.component.qguild.QGBot
import love.forte.simbot.component.qguild.QQGuildBotManager
import love.forte.simbot.component.qguild.QQGuildBotManagerConfiguration
import love.forte.simbot.component.qguild.QQGuildComponent
import love.forte.simbot.component.qguild.config.QGBotComponentConfiguration
import love.forte.simbot.component.qguild.event.QGBotRegisteredEvent
import love.forte.simbot.component.qguild.internal.event.QGBotRegisteredEventImpl
import love.forte.simbot.component.qguild.util.registerRootJob
import love.forte.simbot.event.EventProcessor
import love.forte.simbot.event.pushIfProcessable
import love.forte.simbot.logger.LoggerFactory
import love.forte.simbot.qguild.BotFactory
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
internal class QQGuildBotManagerImpl(
    override val eventProcessor: EventProcessor,
    override val configuration: QQGuildBotManagerConfiguration,
    override val component: QQGuildComponent,
) : QQGuildBotManager() {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(QQGuildBotManagerImpl::class)
    }

    private val job: CompletableJob
    override val coroutineContext: CoroutineContext

    init {
        val parentContext = configuration.parentCoroutineContext
        val parentJob = parentContext[Job]
        job = SupervisorJob(parentJob)
        coroutineContext = parentContext + job
    }

    override val logger: Logger
        get() = LOGGER

    private val lock = ReentrantReadWriteLock()

    override val isActive: Boolean
        get() = job.isActive

    override val isCancelled: Boolean
        get() = job.isCompleted

    override val isStarted: Boolean
        get() = job.isActive || job.isCompleted

    override fun invokeOnCompletion(handler: CompletionHandler) {
        job.invokeOnCompletion(handler)
    }

    override suspend fun join() {
        job.join()
    }

    // nothing to start
    override suspend fun start(): Boolean {
        return !job.isCompleted
    }

    private var botMap = ConcurrentHashMap<String, QGBotImpl>()


    override suspend fun doCancel(reason: Throwable?): Boolean {
        lock.write {
            val cancelled = job.isCancelled
            for (bot in botMap.values.toList()) {
                bot.cancel(reason)
            }
            if (cancelled) {
                return false
            }

            if (reason != null) {
                job.cancel(reason.localizedMessage, reason)
            } else {
                job.cancel()
            }
            job.join()
            return true
        }
    }

    override fun get(id: ID): QGBot? {
        lock.read {
            if (job.isCancelled) throw IllegalStateException("This manager has already cancelled.")

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
        block: QGBotComponentConfiguration.() -> Unit,
    ): QGBot {
        val configure = configuration.botConfigure
        lock.write {
            val config = QGBotComponentConfiguration().also(block)
            val sourceBot = BotFactory.create(appId, appKey, token) {
                configure(appId, appKey, token)
                config.botConfigure.invoke(this)

                val customJob = coroutineContext[Job]
                if (customJob == null) {
                    coroutineContext += job
                } else {
                    customJob.registerRootJob(job)
                }
            }
            // check botInfo
            logger.info("Registered bot appId: {}", sourceBot.ticket.appId)
            return botMap.compute(appId) { key, old ->
                if (old != null) throw BotAlreadyRegisteredException(key)

                QGBotImpl(sourceBot, this, eventProcessor, component, config).apply {
                    job.invokeOnCompletion {
                        // remove self on completion
                        botMap.remove(key, this)
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