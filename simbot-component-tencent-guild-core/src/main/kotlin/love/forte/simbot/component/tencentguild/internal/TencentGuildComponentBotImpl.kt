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

import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import love.forte.simbot.ID
import love.forte.simbot.component.tencentguild.TencentGuild
import love.forte.simbot.component.tencentguild.TencentGuildBotManager
import love.forte.simbot.component.tencentguild.TencentGuildComponent
import love.forte.simbot.component.tencentguild.TencentGuildComponentBot
import love.forte.simbot.component.tencentguild.event.TcgBotStartedEvent
import love.forte.simbot.component.tencentguild.internal.TencentGuildImpl.Companion.tencentGuildImpl
import love.forte.simbot.component.tencentguild.internal.event.TcgBotStartedEventImpl
import love.forte.simbot.event.EventProcessor
import love.forte.simbot.event.pushIfProcessable
import love.forte.simbot.literal
import love.forte.simbot.logger.LoggerFactory
import love.forte.simbot.qguild.Bot
import love.forte.simbot.qguild.api.user.GetBotGuildListApi
import love.forte.simbot.qguild.model.SimpleGuild
import love.forte.simbot.qguild.requestBy
import love.forte.simbot.utils.item.Items
import love.forte.simbot.utils.item.Items.Companion.asItems
import java.util.concurrent.ConcurrentHashMap
import kotlin.collections.set
import kotlin.coroutines.CoroutineContext
import love.forte.simbot.qguild.model.User as QGUser

/**
 *
 * @author ForteScarlet
 */
internal class TencentGuildComponentBotImpl(
    override val source: Bot,
    override val manager: TencentGuildBotManager,
    override val eventProcessor: EventProcessor,
    override val component: TencentGuildComponent,
) : TencentGuildComponentBot {

    private val job = SupervisorJob(source.coroutineContext[Job])
    override val coroutineContext: CoroutineContext = source.coroutineContext + job

    override val logger =
        LoggerFactory.getLogger("love.forte.simbot.component.tencentguild.bot.${source.ticket.secret}")

    @Volatile
    private lateinit var botSelf: QGUser

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

    internal val internalGuilds = ConcurrentHashMap<String, TencentGuildImpl>()

    internal fun getInternalGuild(id: ID): TencentGuildImpl? = internalGuilds[id.literal]

    override val guilds: Items<TencentGuildImpl>
        get() = internalGuilds.values.asItems()

    override suspend fun guild(id: ID): TencentGuild? {
        return internalGuilds[id.literal]
    }

    /**
     * 启动当前bot。
     */
    override suspend fun start(): Boolean = source.start().also {
        // just set everytime.
        botSelf = source.me().also { me ->
            logger.debug("bot own information: {}", me)
        }

        suspend fun pushStartedEvent() {
            eventProcessor.pushIfProcessable(TcgBotStartedEvent) {
                TcgBotStartedEventImpl(this)
            }
        }

        if (!it) {
            pushStartedEvent()
            return@also
        }

        initData()
        registerEventProcessor()
        pushStartedEvent()
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

}

private suspend fun TencentGuildComponentBotImpl.initGuildListData() {
    var lastId: String? = null
    var times = 1
    val guildInfoList = mutableListOf<SimpleGuild>()
    while (true) {
        val list = GetBotGuildListApi.create(after = lastId).requestBy(source)
        if (list.isEmpty()) break

        logger.debug(
            "Sync batch {} of the guild list, {} pieces of synchronized data, after id: {}",
            times++,
            list.size,
            lastId
        )

        guildInfoList.addAll(list)

        lastId = list.lastOrNull()?.id

    }

    logger.info("{} pieces of guild information are synchronized", guildInfoList.size)
    logger.info("Begin to initialize guild information asynchronously...")

    val initDataJob = SupervisorJob(this.coroutineContext[Job])

    for (info in guildInfoList) {
        launch(initDataJob) {
            val guildImpl = tencentGuildImpl(this@initGuildListData, info)
            // 基本不会出现重复，初始化阶段直接覆盖。
            internalGuilds[info.id] = guildImpl
        }
    }

    // wait for sync jobs...
    initDataJob.children.forEach {
        it.join()
    }
    initDataJob.cancel()

    logger.info("{} pieces of guild are initialized.", internalGuilds.size)
}
