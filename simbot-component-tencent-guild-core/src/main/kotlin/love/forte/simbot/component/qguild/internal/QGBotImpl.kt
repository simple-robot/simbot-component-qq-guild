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
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import love.forte.simbot.ID
import love.forte.simbot.component.qguild.QGBot
import love.forte.simbot.component.qguild.QGBotManager
import love.forte.simbot.component.qguild.QGGuild
import love.forte.simbot.component.qguild.QQGuildComponent
import love.forte.simbot.component.qguild.event.QGBotStartedEvent
import love.forte.simbot.component.qguild.internal.QGGuildImpl.Companion.qgGuild
import love.forte.simbot.component.qguild.internal.QGGuildImpl.Companion.qgGuildWithoutInit
import love.forte.simbot.component.qguild.internal.event.QGBotStartedEventImpl
import love.forte.simbot.event.EventProcessor
import love.forte.simbot.literal
import love.forte.simbot.logger.LoggerFactory
import love.forte.simbot.qguild.Bot
import love.forte.simbot.qguild.api.user.GetBotGuildListApi
import love.forte.simbot.qguild.event.GuildCreate
import love.forte.simbot.qguild.event.GuildDelete
import love.forte.simbot.qguild.event.GuildUpdate
import love.forte.simbot.qguild.model.Guild
import love.forte.simbot.qguild.model.SimpleGuild
import love.forte.simbot.qguild.requestBy
import love.forte.simbot.utils.item.Items
import love.forte.simbot.utils.item.Items.Companion.asItems
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.collections.set
import kotlin.coroutines.CoroutineContext
import love.forte.simbot.qguild.model.User as QGUser

/**
 *
 * @author ForteScarlet
 */
internal class QGBotImpl(
    override val source: Bot,
    override val manager: QGBotManager,
    override val eventProcessor: EventProcessor,
    override val component: QQGuildComponent,
) : QGBot {

    override val logger =
        LoggerFactory.getLogger("love.forte.simbot.component.tencentguild.bot.${source.ticket.secret}")

    private val job: CompletableJob
    override val coroutineContext: CoroutineContext
//    private val statusFlowCoroutineContext: CoroutineContext

    @Volatile
    private var internalGuilds = ConcurrentHashMap<String, QGGuildImpl>()

    internal fun getInternalGuild(id: String): QGGuildImpl? = internalGuilds[id]

//    /**
//     * 从缓存中获取指定 Guild，如果不存在，根据ID查询并记录。
//     *
//     */
//    internal suspend fun computeIfAbsentGuild(id: String): QGGuildImpl {
//        internalGuilds.computeIfAbsent(id) {
//            QGGuildImpl.qgGuildWithoutInit(this, )
//        }
//    }

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
        if (::botSelf.isInitialized && botSelf.id == id.literal) return true
        return false
    }

    override val username: String
        get() = if (!::botSelf.isInitialized) {
            throw UninitializedPropertyAccessException("information of bot has not been initialized. Please execute the `start()` method at least once first")
        } else botSelf.username

    override val avatar: String
        get() = if (!::botSelf.isInitialized) "" else botSelf.avatar

    internal fun getInternalGuild(id: ID): QGGuildImpl? = internalGuilds[id.literal]

    override val guilds: Items<QGGuildImpl>
        get() = internalGuilds.values.asItems()

    override suspend fun guild(id: ID): QGGuild? {
        return internalGuilds[id.literal]
    }

    private val startLock = Mutex()

    /**
     * 启动当前bot。
     */
    override suspend fun start(): Boolean = startLock.withLock {
        source.start().also {
            // just set everytime.
            botSelf = source.me().also { me ->
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

            initData()
            // TODO restart clear?
            registerEventProcessor()
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


    /**
     * 初始化bot基本信息.
     */
    private suspend fun initData() {
        initGuildListData()
    }


    private suspend fun initGuildListData() {
        val internalGuilds = ConcurrentHashMap<String, QGGuildImpl>()
        var lastId: String? = null
        var times = 1
        val guilds = mutableMapOf<String, SimpleGuild>()
//        val guildInfoList = mutableSetOf<SimpleGuild>()

        TreeSet<SimpleGuild>(Comparator.comparing { it.id })

        while (true) {
            val list = GetBotGuildListApi.create(after = lastId).requestBy(source)
            if (list.isEmpty()) break

            logger.debug(
                "Sync batch {} of the guild list, {} pieces of synchronized data, after id: {}",
                times++,
                list.size,
                lastId
            )

            list.associateByTo(guilds) { it.id }

            lastId = list.lastOrNull()?.id

        }

        logger.info("{} pieces of guild information are synchronized", guilds.size)
        logger.info("Begin to initialize guild information asynchronously...")

        // try init all guilds.
        coroutineScope {
            for (info in guilds.values) {
                launch {
                    val guildImpl = qgGuild(this@QGBotImpl, info)
                    internalGuilds[info.id] = guildImpl
                }
            }
        }

        // cancel all old guilds
        this.internalGuilds.values.forEach {
            it.cancel()
        }

        logger.info("{} pieces of guild are initialized.", internalGuilds.size)
        this.internalGuilds = internalGuilds
    }


    //region internal events
    internal suspend fun updateGuild(guild: Guild): QGGuildImpl {
        return internalGuilds.compute(guild.id) { _, old ->
            old?.update(guild) ?: qgGuildWithoutInit(this, guild)
        }!!.also {
            it.initData(true)
        }
    }

    internal suspend fun emitGuildCreate(event: GuildCreate): QGGuildImpl {
        return updateGuild(event.data)
    }

    internal suspend fun emitGuildUpdate(event: GuildUpdate): QGGuildImpl {
        return updateGuild(event.data)
    }

    internal fun emitGuildDelete(event: GuildDelete): QGGuildImpl? {
        return internalGuilds.remove(event.data.id)
    }

    //endregion

}

