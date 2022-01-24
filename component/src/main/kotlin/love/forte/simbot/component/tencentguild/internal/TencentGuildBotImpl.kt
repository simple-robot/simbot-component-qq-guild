/*
 *  Copyright (c) 2021-2022 ForteScarlet <ForteScarlet@163.com>
 *
 *  根据 GNU LESSER GENERAL PUBLIC LICENSE 3 获得许可；
 *  除非遵守许可，否则您不得使用此文件。
 *  您可以在以下网址获取许可证副本：
 *
 *       https://www.gnu.org/licenses/lgpl-3.0-standalone.html
 *
 *   有关许可证下的权限和限制的具体语言，请参见许可证。
 */

package love.forte.simbot.component.tencentguild.internal

import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import love.forte.simbot.*
import love.forte.simbot.component.tencentguild.TencentGuild
import love.forte.simbot.component.tencentguild.TencentGuildBot
import love.forte.simbot.component.tencentguild.TencentGuildBotManager
import love.forte.simbot.component.tencentguild.internal.event.eventSignalParsers
import love.forte.simbot.event.EventProcessor
import love.forte.simbot.event.pushIfProcessable
import love.forte.simbot.tencentguild.*
import love.forte.simbot.tencentguild.api.guild.GetBotGuildListApi
import love.forte.simbot.tencentguild.api.guild.GetGuildApi
import love.forte.simbot.utils.runInBlocking
import java.util.stream.Stream
import kotlin.coroutines.CoroutineContext
import kotlin.streams.asStream

/**
 *
 * @author ForteScarlet
 */
internal class TencentGuildBotImpl(
    override val sourceBot: TencentBot,
    override val manager: TencentGuildBotManager,
    override val eventProcessor: EventProcessor,
) : TencentGuildBot() {

    override val coroutineContext: CoroutineContext
        get() = sourceBot.coroutineContext

    private val job
        get() = sourceBot.coroutineContext[Job]!!

    override val logger =
        LoggerFactory.getLogger("love.forte.simbot.component.tencentguild.bot.${sourceBot.ticket.appKey}")

    /**
     * grouping是无效的.
     */
    override suspend fun guilds(grouping: Grouping, limiter: Limiter): Flow<TencentGuildImpl> {
        return getGuildFlow(limiter).map { info ->
            TencentGuildImpl(bot = this, guildInfo = info)
        }
    }

    @Api4J
    override fun getGuilds(grouping: Grouping, limiter: Limiter): Stream<out TencentGuild> {

        return getGuildSequence(limiter).map { info ->
            TencentGuildImpl(bot = this, guildInfo = info)
        }.asStream()
    }


    private fun getGuildFlow(limiter: Limiter): Flow<TencentGuildInfo> {
        return limiter.toFlow { batchSize ->
            val batch = if (batchSize in 1..100) batchSize else 100
            var lastId: ID? = null

            while (true) {
                val list = GetBotGuildListApi(after = lastId, limit = batch).request(sourceBot)
                if (list.isEmpty()) break

                lastId = list.lastOrNull()?.id

                for (tencentGuildInfo in list) {
                    emit(tencentGuildInfo)
                }
            }
        }
    }

    private fun getGuildSequence(limiter: Limiter): Sequence<TencentGuildInfo> {
        return limiter.toSequence { batchSize ->
            val batch = if (batchSize in 1..100) batchSize else 100
            var lastId: ID? = null
            while (true) {
                val list = runBlocking {
                    GetBotGuildListApi(after = lastId, limit = batch).request(sourceBot)
                }
                if (list.isEmpty()) break

                lastId = list.lastOrNull()?.id

                yieldAll(list)
            }
        }
    }

    override suspend fun guild(id: ID): TencentGuild? {
        return try {
            val guild = GetGuildApi(id).request(sourceBot)
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

    override suspend fun start(): Boolean = sourceBot.start().also {
        if (!it) return@also

        //activeStatus.compareAndSet(0, 1)
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
                            bot = this@TencentGuildBotImpl,
                            decoder = json,
                            decoded = decoded,
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