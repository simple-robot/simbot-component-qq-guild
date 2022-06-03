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

import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import love.forte.simbot.*
import love.forte.simbot.component.tencentguild.TencentGuild
import love.forte.simbot.component.tencentguild.TencentGuildBotManager
import love.forte.simbot.component.tencentguild.TencentGuildComponent
import love.forte.simbot.component.tencentguild.TencentGuildComponentBot
import love.forte.simbot.component.tencentguild.event.TcgBotStartedEvent
import love.forte.simbot.component.tencentguild.internal.event.TcgBotStartedEventImpl
import love.forte.simbot.component.tencentguild.internal.event.eventSignalParsers
import love.forte.simbot.event.EventProcessor
import love.forte.simbot.event.pushIfProcessable
import love.forte.simbot.tencentguild.*
import love.forte.simbot.tencentguild.api.guild.GetBotGuildListApi
import love.forte.simbot.tencentguild.api.guild.GetGuildApi
import love.forte.simbot.utils.item.Items
import love.forte.simbot.utils.item.itemsByFlow
import love.forte.simbot.utils.runInBlocking
import kotlin.coroutines.CoroutineContext

/**
 *
 * @author ForteScarlet
 */
internal class TencentGuildComponentBotImpl(
    override val sourceBot: TencentGuildBot,
    override val manager: TencentGuildBotManager,
    override val eventProcessor: EventProcessor,
    override val component: TencentGuildComponent,
) : TencentGuildComponentBot {
    
    override val coroutineContext: CoroutineContext
        get() = sourceBot.coroutineContext
    
    private val job
        get() = sourceBot.coroutineContext[Job]!!
    
    override val logger =
        LoggerFactory.getLogger("love.forte.simbot.component.tencentguild.bot.${sourceBot.ticket.appKey}")
    
    @Volatile
    private lateinit var meId: ID
    
    override fun isMe(id: ID): Boolean {
        if (id == this.id) return true
        if (::meId.isInitialized && meId == id) return true
        return false
    }
    
    override val guilds: Items<TencentGuildImpl>
        get() = bot.itemsByFlow { prop ->
            getGuildFlow(limiter(prop.offset, prop.limit, prop.batch)).map { info ->
                TencentGuildImpl(baseBot = this, guildInfo = info)
            }
        }
    
    
    private fun getGuildFlow(limiter: Limiter): Flow<TencentGuildInfo> {
        return limiter.toFlow { batchSize ->
            val batch = if (batchSize in 1..100) batchSize else 100
            var lastId: ID? = null
            
            while (true) {
                val list = GetBotGuildListApi(after = lastId, limit = batch).requestBy(sourceBot)
                if (list.isEmpty()) break
                
                lastId = list.lastOrNull()?.id
                
                for (tencentGuildInfo in list) {
                    emit(tencentGuildInfo)
                }
            }
        }
    }
    
    override suspend fun guild(id: ID): TencentGuild? {
        return try {
            val guild = GetGuildApi(id).requestBy(sourceBot)
            TencentGuildImpl(this, guild)
        } catch (apiException: TencentApiException) {
            if (apiException.value == 404) {
                null
            } else {
                throw apiException
            }
        }
    }
    
    @Api4J
    override fun getGuild(id: ID): TencentGuild? = runInBlocking { guild(id) }
    
    /**
     * 启动当前bot。
     */
    override suspend fun start(): Boolean = sourceBot.start().also {
        // just set everytime.
        sourceBot.botInfo
        meId = sourceBot.me().id
        
        suspend fun pushEvent() {
            eventProcessor.pushIfProcessable(TcgBotStartedEvent) {
                TcgBotStartedEventImpl(this)
            }
        }
        if (!it) {
            pushEvent()
            return@also
        }
        
        // activeStatus.compareAndSet(0, 1)
        // process event.
        sourceBot.processor { json, decoded ->
            // event processor
            logger.trace("EventSignals.events[{}]: {}", type, EventSignals.events[type])
            EventSignals.events[this.type]?.let { signals ->
                logger.trace("eventSignalParsers[{}]: {}", it, eventSignalParsers[signals])
                
                eventSignalParsers[signals]?.let { parser ->
                    
                    logger.trace(
                        "eventProcessor.isProcessable({}): {}",
                        parser.key,
                        eventProcessor.isProcessable(parser.key)
                    )
                    eventProcessor.pushIfProcessable(parser.key) {
                        parser(
                            bot = this@TencentGuildComponentBotImpl,
                            decoder = json,
                            decoded = decoded,
                            dispatch = this
                        )
                    }
                }
            }
        }
        pushEvent()
    }
    
    
    override suspend fun join() {
        sourceBot.join()
    }
    
    
    override suspend fun cancel(reason: Throwable?): Boolean = sourceBot.cancel(reason)
    
    
    @Api4J
    override fun cancelBlocking(reason: Throwable?): Boolean {
        return runBlocking { cancel(reason) }
    }
    
    override val isStarted: Boolean
        get() = job.isCompleted || job.isActive
    
    
    override val isCancelled: Boolean
        get() = job.isCancelled
    
    
}