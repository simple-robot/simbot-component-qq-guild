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
import kotlinx.coroutines.flow.*
import love.forte.simbot.*
import love.forte.simbot.component.tencentguild.*
import love.forte.simbot.component.tencentguild.event.*
import love.forte.simbot.component.tencentguild.internal.event.*
import love.forte.simbot.event.*
import love.forte.simbot.tencentguild.*
import love.forte.simbot.tencentguild.api.guild.*
import love.forte.simbot.utils.*
import java.util.stream.*
import kotlin.coroutines.*
import kotlin.streams.*

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
        suspend fun pushEvent() {
            eventProcessor.pushIfProcessable(TcgBotStartedEvent) {
                TcgBotStartedEventImpl(this)
            }
        }
        if (!it) {
            pushEvent()
            return@also
        }

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