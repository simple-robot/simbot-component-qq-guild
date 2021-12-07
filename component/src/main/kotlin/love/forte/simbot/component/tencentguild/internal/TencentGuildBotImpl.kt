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

import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import love.forte.simbot.*
import love.forte.simbot.component.tencentguild.TencentGuildBot
import love.forte.simbot.component.tencentguild.TencentGuildBotManager
import love.forte.simbot.component.tencentguild.internal.event.eventSignalParsers
import love.forte.simbot.definition.Friend
import love.forte.simbot.definition.Group
import love.forte.simbot.definition.Guild
import love.forte.simbot.event.EventProcessor
import love.forte.simbot.event.pushIfProcessable
import love.forte.simbot.tencentguild.EventSignals
import love.forte.simbot.tencentguild.TencentBot
import love.forte.simbot.tencentguild.TencentGuildInfo
import love.forte.simbot.tencentguild.api.guild.GetBotGuildListApi
import love.forte.simbot.tencentguild.request
import java.util.concurrent.atomic.AtomicInteger
import java.util.stream.Stream
import kotlin.streams.asStream

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

    private val job get() = sourceBot.coroutineContext[Job]!!

    private val logger =
        LoggerFactory.getLogger("love.forte.simbot.component.tencentguild.bot.${sourceBot.ticket.appKey}")

    /**
     * grouping是无效的.
     */
    override suspend fun guilds(grouping: Grouping, limiter: Limiter): Flow<TencentGuildImpl> {
        val batch = if (limiter.batchSize > 0) {
            if (limiter.batchSize > 100) 100 else limiter.batchSize
        } else {
            100
        }

        return getGuilds(limiter.offset, batch, limiter.limit).map { info ->
            TencentGuildImpl(bot = this, guildInfo = info)
        }
    }

    @Api4J
    override fun getGuilds(grouping: Grouping, limiter: Limiter): Stream<out Guild> {
        val batch = if (limiter.batchSize > 0) {
            if (limiter.batchSize > 100) 100 else limiter.batchSize
        } else {
            100
        }

        return getGuildSequence(limiter.offset, batch, limiter.limit).map { info ->
            TencentGuildImpl(bot = this, guildInfo = info)
        }.asStream()
    }


    private fun getGuilds(skip: Int, batch: Int, limit: Int): Flow<TencentGuildInfo> {
        return flow {
            var lastId: ID? = null

            while (true) {
                val list = GetBotGuildListApi(after = lastId, limit = batch).request(sourceBot)
                if (list.isEmpty()) break

                lastId = list.lastOrNull()?.id

                for (tencentGuildInfo in list) {
                    emit(tencentGuildInfo)
                }
            }
        }.let {
            var f = it
            if (skip > 0) {
                f = f.drop(skip)
            }
            if (limit > 0) {
                f = f.take(limit)
            }

            f
        }
    }

    private fun getGuildSequence(skip: Int, batch: Int, limit: Int): Sequence<TencentGuildInfo> {
        return sequence {
            var lastId: ID? = null
            while (true) {
                val list = runBlocking {
                    GetBotGuildListApi(after = lastId, limit = batch).request(sourceBot)
                }
                if (list.isEmpty()) break

                lastId = list.lastOrNull()?.id

                yieldAll(list)
            }
        }.let {
            var f = it
            if (skip > 0) {
                f = f.drop(skip)
            }
            if (limit > 0) {
                f = f.take(limit)
            }

            f
        }

    }

    override suspend fun start(): Boolean = sourceBot.start().also {
        activeStatus.compareAndSet(0, 1)
        // process event.
        sourceBot.processor { json ->
            // event processor
            logger.trace("EventSignals.events[{}]: {}", type, EventSignals.events[type])
            EventSignals.events[this.type]?.let {
                logger.trace("eventSignalParsers[{}]: {}", it, eventSignalParsers[it])

                eventSignalParsers[it]?.let { parser ->

                    logger.trace(
                        "eventProcessor.isProcessable({}): {}",
                        parser.key,
                        eventProcessor.isProcessable(parser.key)
                    )
                    eventProcessor.pushIfProcessable(parser.key) {
                        parser(
                            bot = this@TencentGuildBotImpl,
                            decoder = json,
                            dispatch = this
                        )
                    }
                }
            }
        }
    }


    override suspend fun join() {
        sourceBot.join()
    }

    @Api4J
    override fun joinBlocking() {
        super<TencentGuildBot>.joinBlocking()
    }

    @Api4J
    override fun startBlocking(): Boolean {
        return super<TencentGuildBot>.startBlocking()
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


    override suspend fun groups(grouping: Grouping, limiter: Limiter): Flow<Group> {
        return emptyFlow()
    }

    @Api4J
    override fun getGroups(grouping: Grouping, limiter: Limiter): Stream<out Group> {
        return Stream.empty()
    }

    override suspend fun friends(grouping: Grouping, limiter: Limiter): Flow<Friend> {
        return emptyFlow()
    }

    @Api4J
    override fun getFriends(grouping: Grouping, limiter: Limiter): Stream<out Friend> {
        return Stream.empty()
    }

}