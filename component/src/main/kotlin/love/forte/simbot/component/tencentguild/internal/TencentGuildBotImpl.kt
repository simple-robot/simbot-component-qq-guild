/*
 *  Copyright (c) 2021-2021 ForteScarlet <https://github.com/ForteScarlet>
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

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import love.forte.simbot.*
import love.forte.simbot.component.tencentguild.TencentGuildBot
import love.forte.simbot.component.tencentguild.TencentGuildBotManager
import love.forte.simbot.component.tencentguild.internal.event.eventSignalParsers
import love.forte.simbot.definition.Friend
import love.forte.simbot.definition.Group
import love.forte.simbot.event.EventProcessor
import love.forte.simbot.event.pushIfProcessable
import love.forte.simbot.tencentguild.EventSignals
import love.forte.simbot.tencentguild.TencentBot
import love.forte.simbot.tencentguild.TencentGuildInfo
import love.forte.simbot.tencentguild.api.guild.GetBotGuildListApi
import love.forte.simbot.tencentguild.request
import java.util.concurrent.atomic.AtomicInteger

/**
 *
 * @author ForteScarlet
 */
internal class TencentGuildBotImpl(
    override val sourceBot: TencentBot,
    override val manager: TencentGuildBotManager,
    override val eventProcessor: EventProcessor,
) : TencentGuildBot(), TencentBot by sourceBot {
    // 0 init 1 start 2 cancel
    private val activeStatus = AtomicInteger(0)

    private val logger = LoggerFactory.getLogger("love.forte.simbot.component.tencentguild.bot.${sourceBot.ticket.appKey}")

    /**
     * grouping是无效的.
     */
    override suspend fun guilds(grouping: Grouping, limiter: Limiter): Flow<TencentGuildImpl> {
        val batch = if (limiter.batchSize > 0) {
            if (limiter.batchSize > 100) 100 else limiter.batchSize
        } else {
            100
        }

        val limit = if (limiter.limit <= 0) Int.MAX_VALUE else limiter.limit
        val skip = if (limiter.offset <= 0) 0 else limiter.offset
        return getGuilds(skip, batch, limit).map { info ->
            TencentGuildImpl(bot = this, guildInfo = info)
        }
    }


    private fun getGuilds(skip: Int, batch: Int, limit: Int): Flow<TencentGuildInfo> = flow {
        var lastId: ID? = null
        flowForLimiter(skip, limit) {
            GetBotGuildListApi(after = lastId, limit = batch).request(sourceBot).also {
                lastId = it.lastOrNull()?.id
            }
        }
    }


    override suspend fun start(): Boolean = sourceBot.start().also {
        activeStatus.compareAndSet(0, 1)
        // process event.
        sourceBot.processor { json ->
            // event processor
            logger.trace("EventSignals.events[{}]: {}", type, EventSignals.events[type])
            EventSignals.events[this.type]?.let {
                println(eventSignalParsers[it])
                logger.trace("eventSignalParsers[{}]: {}", it, eventSignalParsers[it])

                eventSignalParsers[it]?.let { parser ->

                    println(eventProcessor.isProcessable(parser.key))
                    logger.trace("eventProcessor.isProcessable({}): {}", parser.key, eventProcessor.isProcessable(parser.key))
                    eventProcessor.pushIfProcessable(parser.key) { parser(
                        bot = this@TencentGuildBotImpl,
                        decoder = json,
                        dispatch = this
                    ) }
                }
            }
        }
    }


    override suspend fun join() {
        sourceBot.join()
    }

    override suspend fun cancel(): Boolean = sourceBot.cancel().also {
        activeStatus.set(2)
    }

    override val isStarted: Boolean
        get() = activeStatus.get() >= 1



    override val isCancelled: Boolean
        get() = activeStatus.get() == 2


    override suspend fun groups(grouping: Grouping, limiter: Limiter): Flow<Group> {
        return emptyFlow()
    }

    @Api4J
    override fun getGroups(grouping: Grouping, limiter: Limiter): List<Group> {
        return emptyList()
    }

    override suspend fun friends(grouping: Grouping, limiter: Limiter): Flow<Friend> {
        return emptyFlow()
    }

    @Api4J
    override fun getFriends(grouping: Grouping, limiter: Limiter): List<Friend> {
        return emptyList()
    }
}