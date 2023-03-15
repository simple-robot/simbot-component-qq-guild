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
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import love.forte.simbot.ID
import love.forte.simbot.component.qguild.*
import love.forte.simbot.component.qguild.config.QGBotComponentConfiguration
import love.forte.simbot.component.qguild.event.QGBotStartedEvent
import love.forte.simbot.component.qguild.internal.QGGuildImpl.Companion.qgGuild
import love.forte.simbot.component.qguild.internal.QGGuildImpl.Companion.qgGuildWithoutInit
import love.forte.simbot.component.qguild.internal.event.QGBotStartedEventImpl
import love.forte.simbot.component.qguild.util.requestBy
import love.forte.simbot.event.EventProcessor
import love.forte.simbot.literal
import love.forte.simbot.logger.LoggerFactory
import love.forte.simbot.qguild.Bot
import love.forte.simbot.qguild.QQGuildApiException
import love.forte.simbot.qguild.api.guild.GetGuildApi
import love.forte.simbot.qguild.api.user.GetBotGuildListApi
import love.forte.simbot.qguild.event.EventIntents
import love.forte.simbot.qguild.event.GuildCreate
import love.forte.simbot.qguild.event.GuildDelete
import love.forte.simbot.qguild.event.GuildUpdate
import love.forte.simbot.qguild.ifNotFoundThenNull
import love.forte.simbot.qguild.model.Guild
import love.forte.simbot.qguild.model.SimpleGuild
import love.forte.simbot.qguild.model.User
import love.forte.simbot.qguild.requestBy
import love.forte.simbot.utils.item.Items
import love.forte.simbot.utils.item.Items.Companion.asItems
import love.forte.simbot.utils.item.effectOn
import love.forte.simbot.utils.item.itemsByFlow
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
    private val configuration: QGBotComponentConfiguration
) : QGBot {
    override val logger =
        LoggerFactory.getLogger("love.forte.simbot.component.qguild.bot.${source.ticket.secret}")

    internal val job: CompletableJob
    override val coroutineContext: CoroutineContext
    private val intents = source.configuration.intents


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



    // TODO check permissions (event signs)
    // TODO 内建缓存的ID hash 需要根据 shard 来
    //  https://bot.q.qq.com/wiki/develop/api/gateway/shard.html
    //  分片是按照频道id进行哈希的，同一个频道的信息会固定从同一个链接推送。具体哈希计算规则如下：
    //  shard_id = (guild_id >> 22) % num_shards
    //  ...你妈的

    /*
        如果为null，则说明没有订阅guild相关的事件
     */
    @Volatile
    private var internalGuilds: ConcurrentHashMap<String, QGGuildImpl>? = null

    internal fun getInternalGuild(id: String): QGGuildImpl? = internalGuilds?.get(id)

    private suspend fun queryGuild(id: String): QGGuildImpl? {
        return try {
            GetGuildApi.create(id).requestBy(bot)
        } catch (apiEx: QQGuildApiException) {
            apiEx.ifNotFoundThenNull()
        }?.let { guild -> qgGuild(this, guild) }
    }

    override suspend fun guild(id: ID): QGGuildImpl? {
        return internalGuilds?.get(id.literal) ?: queryGuild(id.literal)
    }

    @OptIn(FlowPreview::class)
    override val guilds: Items<QGGuildImpl>
        get() = internalGuilds?.values?.asItems() ?: itemsByFlow { props ->
            props.effectOn(queryGuildList(props.batch).flatMapConcat { it.asFlow() }).map { qgGuild(this, it) }
        }

    private val startLock = Mutex()

    override suspend fun me(): User = source.me()

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

            initData()

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


    /**
     * 初始化bot基本信息.
     */
    private suspend fun initData() {
        if (EventIntents.Guilds.intents in intents) {
            initGuildListData()
        } else {
            logger.warn("Bot(appId={}) is not subscribed to guild events(intents={}) and will not have a guild cache built in.", id, EventIntents.Guilds.intents)
        }

    }

    private fun queryGuildList(batch: Int = GetBotGuildListApi.DEFAULT_LIMIT): Flow<List<SimpleGuild>> = flow {
        var lastId: String? = null
        while (true) {
            val list = GetBotGuildListApi.create(after = lastId).requestBy(source)
            if (list.isEmpty()) break
            lastId = list.last().id
            emit(list)
        }
    }

    private suspend fun initGuildListData() {
        val guildParallel = configuration.initConfig.parallel.guild
        require(guildParallel >= 0) {
            "Guild parallel must >= 0, but $guildParallel"
        }

        val internalGuilds = ConcurrentHashMap<String, QGGuildImpl>()
        var times = 1
        val guilds = mutableMapOf<String, SimpleGuild>()

        TreeSet<SimpleGuild>(Comparator.comparing { it.id })

        queryGuildList().collect { list ->
            logger.debug(
                "Sync batch {} of the guild list, {} pieces of synchronized data.",
                times++,
                list.size
            )
            list.associateByTo(guilds) { it.id }
        }

        logger.info("{} pieces of guild information are synchronized", guilds.size)
        logger.info("Begin to initialize guild information asynchronously...")


        suspend fun syncGuild(info: SimpleGuild) {
            try {
                val guildImpl = qgGuild(this@QGBotImpl, info)
                internalGuilds[info.id] = guildImpl
            } catch (ex: RuntimeException) {
                if (ex is QQGuildApiException || ex is QQGuildInitException) {
                    logger.warn(
                        "Guild(id={}, name={}) cannot be initialized because '{}'. Guild(id={}, name={}) will be skipped",
                        info.id,
                        info.name,
                        ex.localizedMessage,
                        info.id,
                        info.name,
                    )
                    logger.debug(
                        "Guild(id={}, name={}) cannot be initialized because '{}'. Guild(id={}, name={}) will be skipped",
                        info.id,
                        info.name,
                        ex.localizedMessage,
                        info.id,
                        info.name,
                        ex
                    )
                } else {
                    throw ex
                }
            }
        }

        when (guildParallel) {
            0 -> coroutineScope {
                for (info in guilds.values) {
                    launch {
                        syncGuild(info)
                    }
                }
            }

            1 -> {
                for (info in guilds.values) {
                    syncGuild(info)
                }
            }

            else -> {
                @OptIn(ExperimentalStdlibApi::class, ExperimentalCoroutinesApi::class)
                val dispatcher =
                    (coroutineContext[CoroutineDispatcher] ?: Dispatchers.Default).limitedParallelism(guildParallel)
                withContext(dispatcher) {
                    coroutineScope {
                        for (info in guilds.values) {
                            launch {
                                syncGuild(info)
                            }
                        }
                    }
                }
            }
        }

//        // try init all guilds.
//        coroutineScope {
//            for (info in guilds.values) {
//                launch {
//                    val guildImpl = qgGuild(this@QGBotImpl, info)
//                    internalGuilds[info.id] = guildImpl
//                }
//            }
//        }

        // cancel all old guilds
        this.internalGuilds?.also { old ->
            old.values.forEach {
                it.cancel()
            }
            old.clear()
        }

        logger.info("{} pieces of guild are initialized.", internalGuilds.size)
        this.internalGuilds = internalGuilds
    }


    //region internal events
    internal suspend fun updateGuild(guild: Guild): QGGuildImpl {
        return internalGuilds?.let { cache ->
            cache.compute(guild.id) { _, old ->
                old?.update(guild) ?: qgGuildWithoutInit(this, guild)
            }!!.also {
                it.initData(true)
            }
        } ?: qgGuild(this, guild)
        // 如果不支持缓存，直接初始化

    }

    internal suspend fun emitGuildCreate(event: GuildCreate): QGGuildImpl {
        return updateGuild(event.data)
    }

    internal suspend fun emitGuildUpdate(event: GuildUpdate): QGGuildImpl {
        return updateGuild(event.data)
    }

    internal fun emitGuildDelete(event: GuildDelete): QGGuildImpl? {
        return internalGuilds?.remove(event.data.id)
    }

    //endregion




    override fun toString(): String {
        val uid = if (::botSelf.isInitialized) botSelf.id else "(NOT INIT)"
        return "QGBotImpl(appId=$id, userId=$uid, isActive=$isActive)"
    }
}

