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

package love.forte.simbot.component.tencentguild.internal

import kotlinx.coroutines.*
import love.forte.simbot.ID
import love.forte.simbot.bot.BotAlreadyRegisteredException
import love.forte.simbot.component.tencentguild.TencentGuildBotManager
import love.forte.simbot.component.tencentguild.TencentGuildBotManagerConfiguration
import love.forte.simbot.component.tencentguild.TencentGuildComponent
import love.forte.simbot.component.tencentguild.TencentGuildComponentBot
import love.forte.simbot.component.tencentguild.event.TcgBotRegisteredEvent
import love.forte.simbot.component.tencentguild.internal.event.TcgBotRegisteredEventImpl
import love.forte.simbot.event.EventProcessor
import love.forte.simbot.event.pushIfProcessable
import love.forte.simbot.logger.LoggerFactory
import love.forte.simbot.qguild.BotConfiguration
import love.forte.simbot.qguild.tencentGuildBot
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
    override val eventProcessor: EventProcessor,
    override val configuration: TencentGuildBotManagerConfiguration,
    override val component: TencentGuildComponent,
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
    
    private var botMap = ConcurrentHashMap<String, TencentGuildComponentBotImpl>()
    
    
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
    
    override fun get(id: ID): TencentGuildComponentBot? {
        lock.read {
            if (completableJob.isCancelled) throw IllegalStateException("This manager has already cancelled.")
            
            return botMap[id.toString()]
        }
    }
    
    override fun all(): List<TencentGuildComponentBot> {
        return botMap.values.toList()
    }
    
    
    override fun register(
        appId: String,
        appKey: String,
        token: String,
        block: BotConfiguration.() -> Unit,
    ): TencentGuildComponentBot {
        val configure = configuration.botConfigure
        lock.write {
            val sourceBot = tencentGuildBot(appId, appKey, token) {
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
                
                TencentGuildComponentBotImpl(sourceBot, this, eventProcessor, component).apply {
                    coroutineContext[Job]!!.invokeOnCompletion {
                        // remove self on completion
                        botMap.remove(key)
                    }
                }
            }!!.also { bot ->
                bot.launch {
                    eventProcessor.pushIfProcessable(TcgBotRegisteredEvent) {
                        TcgBotRegisteredEventImpl(bot)
                    }
                }
            }
            // return guildBot
        }
    }
}
