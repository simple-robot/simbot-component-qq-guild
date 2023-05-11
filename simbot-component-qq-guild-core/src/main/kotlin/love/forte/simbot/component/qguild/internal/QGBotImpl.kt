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
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import love.forte.simbot.ID
import love.forte.simbot.component.qguild.QGBot
import love.forte.simbot.component.qguild.QQGuildBotManager
import love.forte.simbot.component.qguild.QQGuildComponent
import love.forte.simbot.component.qguild.config.QGBotComponentConfiguration
import love.forte.simbot.component.qguild.event.QGBotStartedEvent
import love.forte.simbot.component.qguild.internal.QGGuildImpl.Companion.qgGuild
import love.forte.simbot.component.qguild.internal.event.QGBotStartedEventImpl
import love.forte.simbot.component.qguild.util.requestBy
import love.forte.simbot.event.EventProcessor
import love.forte.simbot.literal
import love.forte.simbot.logger.LoggerFactory
import love.forte.simbot.qguild.*
import love.forte.simbot.qguild.DisposableHandle
import love.forte.simbot.qguild.api.guild.GetGuildApi
import love.forte.simbot.qguild.api.user.GetBotGuildListApi
import love.forte.simbot.qguild.model.SimpleGuild
import love.forte.simbot.qguild.model.User
import love.forte.simbot.utils.item.Items
import love.forte.simbot.utils.item.effectOn
import love.forte.simbot.utils.item.itemsByFlow
import kotlin.coroutines.CoroutineContext
import love.forte.simbot.qguild.model.User as QGUser

/**
 *
 * @author ForteScarlet
 */
internal class QGBotImpl(
    override val source: Bot,
    override val manager: QQGuildBotManager,
    override val eventProcessor: EventProcessor,
    override val component: QQGuildComponent,
    configuration: QGBotComponentConfiguration
) : QGBot {
    override val logger =
        LoggerFactory.getLogger("love.forte.simbot.component.qguild.bot.${source.ticket.secret}")

    internal val job: CompletableJob
    override val coroutineContext: CoroutineContext

    private val cacheConfig = configuration.cacheConfig
    private val cacheable = cacheConfig?.enable == true

    init {
        val context = source.coroutineContext
        val job = SupervisorJob(context[Job])
        this.job = job
        this.coroutineContext = context + job
    }

    @Volatile
    private lateinit var botSelf: QGUser

    override val userId: ID
        get() {
            if (!::botSelf.isInitialized) {
                throw UninitializedPropertyAccessException("information of bot has not been initialized. Please execute the `start()` method at least once first")
            }

            return botSelf.id.ID
        }


    override fun isMe(id: ID): Boolean {
        if (id == this.id) return true
        return ::botSelf.isInitialized && botSelf.id == id.literal
    }

    override val username: String
        get() = if (!::botSelf.isInitialized) {
            throw UninitializedPropertyAccessException("information of bot has not been initialized. Please execute the `start()` method at least once first")
        } else botSelf.username

    override val avatar: String
        get() = if (!::botSelf.isInitialized) "" else botSelf.avatar


    private suspend fun queryGuild(id: String): QGGuildImpl? {
        return try {
            GetGuildApi.create(id).requestBy(bot)
        } catch (apiEx: QQGuildApiException) {
            apiEx.ifNotFoundThenNull()
        }?.let { guild -> qgGuild(this, guild) }
    }

    override suspend fun guild(id: ID): QGGuildImpl? = queryGuild(id.literal)

    @OptIn(ExperimentalCoroutinesApi::class)
    override val guilds: Items<QGGuildImpl>
        get() = itemsByFlow { props ->
            props.effectOn(queryGuildList(props.batch).flatMapConcat { it.asFlow() }).map { qgGuild(this, it) }
        }

    private val startLock = Mutex()

    override suspend fun me(): User = source.me().also { botSelf = it }

    @Volatile
    private var sourceListenerDisposableHandle: DisposableHandle? = null

    /**
     * 启动当前bot。
     */
    override suspend fun start(): Boolean = startLock.withLock {
        source.start().also {
            // just set everytime.
            botSelf = me().also { me ->
                logger.debug("bot own information: {}", me)
            }

            suspend fun pushStartedEvent() {
                if (eventProcessor.isProcessable(QGBotStartedEvent)) {
                    launch {
                        eventProcessor.push(QGBotStartedEventImpl(this@QGBotImpl))
                    }
                }
            }

            if (!it) {
                pushStartedEvent()
                return@also
            }

            sourceListenerDisposableHandle?.also { handle ->
                handle.dispose()
                sourceListenerDisposableHandle = null
            }
            sourceListenerDisposableHandle = registerEventProcessor()
            pushStartedEvent()
        }
    }


    override suspend fun join() {
        source.join()
    }


    override suspend fun cancel(reason: Throwable?): Boolean = source.cancel(reason)


    override val isStarted: Boolean
        get() = job.isCompleted || job.isActive


    override val isCancelled: Boolean
        get() = job.isCancelled


    private fun queryGuildList(batch: Int): Flow<List<SimpleGuild>> = flow {
        val limit = batch.takeIf { it > 0 } ?: GetBotGuildListApi.DEFAULT_LIMIT
        var lastId: String? = null
        while (true) {
            val list = GetBotGuildListApi.create(after = lastId, limit = limit).requestBy(source)
            if (list.isEmpty()) break
            lastId = list.last().id
            emit(list)
        }
    }

    internal fun inGuild(guildId: String, currentMsgId: String? = null): QGGuildBotImpl =
        QGGuildBotImpl(bot = this, guildId = guildId, currentMsgId = currentMsgId)

    internal fun <T> checkIfTransmitCacheable(target: T): T? = target.takeIf { cacheable && cacheConfig?.transmitCacheConfig?.enable == true }

    override fun toString(): String {
        val uid = if (::botSelf.isInitialized) botSelf.id else "(NOT INIT)"
        return "QGBotImpl(appId=$id, userId=$uid, isActive=$isActive)"
    }
}


/**
 * 从 [QGBot] 中分配一个拥有新的 [SupervisorJob] 的 [CoroutineContext].
 */
internal fun CoroutineScope.newSupervisorCoroutineContext(): CoroutineContext =
    coroutineContext + SupervisorJob(coroutineContext[Job])
