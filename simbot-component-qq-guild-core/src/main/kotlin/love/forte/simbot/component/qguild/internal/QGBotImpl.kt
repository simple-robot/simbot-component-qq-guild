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
import love.forte.simbot.component.qguild.*
import love.forte.simbot.component.qguild.config.QGBotComponentConfiguration
import love.forte.simbot.component.qguild.event.QGBotStartedEvent
import love.forte.simbot.component.qguild.internal.QGGuildImpl.Companion.qgGuild
import love.forte.simbot.component.qguild.internal.event.QGBotStartedEventImpl
import love.forte.simbot.component.qguild.internal.forum.QGForumChannelImpl
import love.forte.simbot.component.qguild.util.requestBy
import love.forte.simbot.event.EventProcessor
import love.forte.simbot.literal
import love.forte.simbot.logger.LoggerFactory
import love.forte.simbot.qguild.*
import love.forte.simbot.qguild.DisposableHandle
import love.forte.simbot.qguild.api.channel.GetChannelApi
import love.forte.simbot.qguild.api.channel.GetGuildChannelListApi
import love.forte.simbot.qguild.api.guild.GetGuildApi
import love.forte.simbot.qguild.api.member.GetMemberApi
import love.forte.simbot.qguild.api.user.GetBotGuildListApi
import love.forte.simbot.qguild.model.ChannelType
import love.forte.simbot.qguild.model.SimpleChannel
import love.forte.simbot.qguild.model.SimpleGuild
import love.forte.simbot.qguild.model.isCategory
import love.forte.simbot.utils.item.Items
import love.forte.simbot.utils.item.effectOn
import love.forte.simbot.utils.item.effectedItemsByFlow
import love.forte.simbot.utils.item.itemsByFlow
import java.util.concurrent.ConcurrentHashMap
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


    internal suspend fun queryGuild(id: String): QGGuildImpl? {
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


    internal fun channelFlow(guildId: String): Flow<SimpleChannel> {
        return flow {
            GetGuildChannelListApi.create(guildId)
                .requestBy(this@QGBotImpl)
                .forEach {
                    emit(it)
                }
        }
    }

    /**
     * @throws QQGuildApiException
     */
    internal suspend fun queryChannel(id: String): SimpleChannel? {
        return try {
            GetChannelApi.create(id).requestBy(this)
        } catch (apiEx: QQGuildApiException) {
            apiEx.ifNotFoundThenNull()
        }
    }

    @OptIn(InternalApi::class)
    internal suspend fun channel(id: String, sourceGuild: QGGuildImpl?): QGChannel? {
        val channelInfo = queryChannel(id) ?: return null

        return when (channelInfo.type) {
            ChannelType.CATEGORY -> QGChannelCategoryImpl(
                bot = sourceGuild?.bot ?: this.inGuild(channelInfo.guildId),
                source = channelInfo,
                sourceGuild = checkIfTransmitCacheable(sourceGuild),
            ) // throw IllegalStateException("The type of channel(id=${channelInfo.id}, name=${channelInfo.name}) is CATEGORY. Maybe you should use [guild.category(id)]?")
            ChannelType.TEXT -> QGTextChannelImpl(
                bot = sourceGuild?.bot ?: this.inGuild(channelInfo.guildId),
                source = channelInfo,
                sourceGuild = checkIfTransmitCacheable(sourceGuild),
            )

            ChannelType.FORUM -> QGForumChannelImpl(
                bot = sourceGuild?.bot ?: this.inGuild(channelInfo.guildId),
                source = channelInfo,
                sourceGuild = checkIfTransmitCacheable(sourceGuild),
            )

            else -> QGNonTextChannelImpl(
                bot = sourceGuild?.bot ?: this.inGuild(channelInfo.guildId),
                source = channelInfo,
                sourceGuild = checkIfTransmitCacheable(sourceGuild),
            )
        }
    }

    internal data class ChannelInfoWithCategory(val info: SimpleChannel, val categoryId: QGChannelCategoryId)

    internal fun channelFlowWithCategoryId(guildId: String, sourceGuild: QGGuildImpl?): Flow<ChannelInfoWithCategory> {
        val categoryMap = ConcurrentHashMap<String, QGChannelCategoryId>()
        fun bot(info: SimpleChannel): QGGuildBotImpl = sourceGuild?.bot ?: inGuild(info.guildId)

        return channelFlow(guildId).filter { info ->
            if (info.type.isCategory) {
                categoryMap.computeIfAbsent(info.id) {
                    QGChannelCategoryImpl(
                        bot = bot(info),
                        source = info,
                        sourceGuild = checkIfTransmitCacheable(sourceGuild)
                    )
                }

                false
            } else {
                true
            }
        }.map { info ->
            val category = categoryMap.computeIfAbsent(info.parentId) { cid ->
                QGChannelCategoryIdImpl(
                    bot = bot(info),
                    guildId = info.guildId.ID,
                    id = cid.ID,
                    sourceGuild = sourceGuild
                )
            }

            ChannelInfoWithCategory(info, category)
        }
    }


    /**
     * 通过API实时查询channels列表
     */
    @OptIn(InternalApi::class)
    internal fun queryChannels(guildId: String, sourceGuild: QGGuildImpl?): Items<QGChannel> = effectedItemsByFlow {
        fun bot(info: SimpleChannel): QGGuildBotImpl = sourceGuild?.bot ?: inGuild(info.guildId)
        channelFlowWithCategoryId(guildId, sourceGuild).map { (info, category) ->
            when (info.type) {
                ChannelType.TEXT -> QGTextChannelImpl(
                    bot = bot(info),
                    source = info,
                    sourceGuild = checkIfTransmitCacheable(sourceGuild),
                    category = category,
                )

                ChannelType.FORUM -> QGForumChannelImpl(
                    bot = bot(info),
                    source = info,
                    sourceGuild = checkIfTransmitCacheable(sourceGuild),
                    category = category,
                )

                else -> QGNonTextChannelImpl(
                    bot = bot(info),
                    source = info,
                    sourceGuild = checkIfTransmitCacheable(sourceGuild),
                    category = category
                )
            }

        }
    }

    internal suspend fun member(guildId: String, userId: String, sourceGuild: QGGuildImpl?): QGMemberImpl? {
        val member = try {
            GetMemberApi.create(guildId, userId).requestBy(this)
        } catch (apiEx: QQGuildApiException) {
            apiEx.ifNotFoundThenNull()
        }

        return member?.let { info ->
            QGMemberImpl(
                bot = this,
                source = info,
                guildId = guildId.ID,
                sourceGuild = checkIfTransmitCacheable(sourceGuild)
            )
        }
    }

    private val startLock = Mutex()

    override suspend fun me(): QGUser = source.me().also { botSelf = it }

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

    internal fun inGuild(guildId: String, sourceGuild: QGGuildImpl? = null): QGGuildBotImpl =
        QGGuildBotImpl(bot = this, guildId = guildId, sourceGuild = checkIfTransmitCacheable(sourceGuild))

    private val isTransmitCacheable = cacheable && cacheConfig?.transmitCacheConfig?.enable == true

    internal fun <T> checkIfTransmitCacheable(target: T): T? = target.takeIf { isTransmitCacheable }

    override fun toString(): String {
        // 还未初始化
        val uid = if (::botSelf.isInitialized) botSelf.id else "(Not initialized yet)"
        return "QGBotImpl(appId=$id, userId=$uid, isActive=$isActive)"
    }
}


/**
 * 从 [QGBot] 中分配一个拥有新的 [SupervisorJob] 的 [CoroutineContext].
 */
internal fun CoroutineScope.newSupervisorCoroutineContext(): CoroutineContext =
    coroutineContext + SupervisorJob(coroutineContext[Job])
