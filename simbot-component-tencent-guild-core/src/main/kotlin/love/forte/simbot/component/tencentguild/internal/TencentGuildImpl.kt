/*
 * Copyright (c) 2022-2023. ForteScarlet.
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
import love.forte.simbot.ID
import love.forte.simbot.Timestamp
import love.forte.simbot.component.tencentguild.TencentChannel
import love.forte.simbot.component.tencentguild.TencentGuild
import love.forte.simbot.component.tencentguild.TencentGuildComponentGuildBot
import love.forte.simbot.component.tencentguild.TencentMember
import love.forte.simbot.component.tencentguild.internal.info.toInternal
import love.forte.simbot.component.tencentguild.util.requestBy
import love.forte.simbot.literal
import love.forte.simbot.logger.LoggerFactory
import love.forte.simbot.qguild.TencentApiException
import love.forte.simbot.qguild.api.apipermission.ApiPermissions
import love.forte.simbot.qguild.api.apipermission.GetApiPermissionListApi
import love.forte.simbot.qguild.api.channel.GetGuildChannelListApi
import love.forte.simbot.qguild.api.member.GetMemberApi
import love.forte.simbot.qguild.api.role.GetGuildRoleListApi
import love.forte.simbot.qguild.isGrouping
import love.forte.simbot.qguild.model.Guild
import love.forte.simbot.toTimestamp
import love.forte.simbot.utils.item.Items
import love.forte.simbot.utils.item.Items.Companion.asItems
import love.forte.simbot.utils.item.effectedFlowItems
import java.util.concurrent.ConcurrentHashMap
import kotlin.coroutines.CoroutineContext
import kotlin.time.Duration


/**
 *
 * @author ForteScarlet
 */
internal class TencentGuildImpl private constructor(
    private val baseBot: TencentGuildComponentBotImpl,
    override val source: Guild,
    override val permissions: ApiPermissions
) : TencentGuild {
    private val logger =
        LoggerFactory.getLogger("love.forte.simbot.component.tencentguild.internal.TencentGuildImpl[${source.id}]")

    private val job = SupervisorJob(baseBot.coroutineContext[Job])
    override val coroutineContext: CoroutineContext = baseBot.coroutineContext + job

    override val id: ID = source.id.ID
    override val maximumChannel: Int get() = -1
    override val createTime: Timestamp get() = source.joinedAt.toTimestamp()
    override val currentMember: Int get() = source.memberCount
    override val description: String get() = source.description
    override val icon: String get() = source.icon
    override val maximumMember: Int get() = source.maxMembers
    override val name: String get() = source.name
    override val ownerId: ID get() = source.ownerId.ID

    internal val internalChannels = ConcurrentHashMap<String, TencentChannelImpl>()
    internal val internalChannelCategories = ConcurrentHashMap<String, TencentChannelCategoryImpl>()

    internal fun getInternalChannel(id: ID): TencentChannelImpl? = internalChannels[id.literal]

    override lateinit var bot: TencentGuildComponentGuildBot
        internal set

    internal lateinit var ownerInternal: TencentMemberImpl

    override fun toString(): String {
        return "TencentGuildImpl(bot=$baseBot, guildInfo=$source)"
    }

    override suspend fun owner(): TencentMember = ownerInternal

    /**
     * 同步数据，包括成员信息和频道列表信息, 以及 [bot] 和 [ownerInternal] 的初始化。
     * 必须在对象实例后执行一次进行初始化，否则内部属性信息将会为空。
     *
     * *Note: 成员列表的获取暂时不支持（只支持私域）*
     */
    private suspend fun initData() {
        // TODO 如果不支持，变更策略(查询时)
        syncData()
    }

    /**
     * 同步数据，包括成员信息和频道列表信息, 以及 [bot] 和 [ownerInternal] 的初始化。
     * 必须在对象实例后执行一次进行初始化，否则内部属性信息将会为空。
     *
     * *Note: 成员列表的获取暂时不支持（只支持私域）*
     */
    internal suspend fun syncData() {
        kotlin.runCatching { syncMembers() }.onFailure { e ->
            logger.error("Sync members data for guild $this failed.", e)
        }
        // TODO 如果不支持，变更策略(查询时)
        kotlin.runCatching { syncChannels() }.onFailure { e ->
            logger.error("Sync channels data for guild $this failed.", e)
        }
    }

    override val members: Items<TencentMember>
        get() = TODO("Not yet implemented")

    override suspend fun member(id: ID): TencentMemberImpl? {
        val member = kotlin.runCatching { GetMemberApi.create(source.id, id).requestBy(baseBot) }.getOrElse { e ->
            if (e is TencentApiException && e.value == 404) return@getOrElse null

            throw e
        }

        return member?.let { info -> TencentMemberImpl(baseBot, info.toInternal(), this) }
    }

    override val roles: Items<TencentRoleImpl>
        get() = bot.effectedFlowItems {
            GetGuildRoleListApi.create(source.id).requestBy(baseBot).roles.forEach { info ->
                val roleImpl = TencentRoleImpl(baseBot, info)
                emit(roleImpl)
            }
        }


    override val currentChannel: Int get() = internalChannels.size

    override val channels: Items<TencentChannelImpl>
        get() = internalChannels.values.asItems()

    override suspend fun channel(id: ID): TencentChannel? {
        return internalChannels[id.literal]
    }

    override val channelList: List<TencentChannel>
        get() = internalChannels.values.toList()

    override suspend fun mute(duration: Duration): Boolean = false

    /**
     * 同步成员列表、owner、bot的信息。
     */
    private suspend fun syncMembers() {
        // emm... 暂时不支持成员列表的获取. 反正挺神奇的

        syncBot()
        syncOwner()
    }

    private suspend fun syncOwner() {
        val ownerId = source.ownerId
        val ownerInfo = GetMemberApi.create(source.id, ownerId).requestBy(baseBot)

        ownerInternal = TencentMemberImpl(baseBot, ownerInfo, this)
        logger.debug("Sync guild owner: {}", ownerInfo)

    }

    private suspend fun syncBot() {
        val member = member(bot.id)!!
        bot = baseBot.asMember(member)
        logger.debug("Sync guild bot: {}", bot)
    }


    private suspend fun syncChannels() {
        val channelInfoList = GetGuildChannelListApi.create(source.id).requestBy(baseBot).sortedBy {
            if (it.channelType.isGrouping) 0 else 1
        }

        logger.debug(
            "Sync the channel list for guild(id={}, name={}), {} pieces of synchronized data",
            id,
            name,
            channelInfoList.size
        )
        for (info in channelInfoList) {
            if (info.channelType.isGrouping) {
                internalChannelCategories.compute(info.id.literal) { _, current ->
                    current?.also {
                        it.channel = info
                    } ?: TencentChannelCategoryImpl(baseBot, this, info)
                }
            } else {
                // find category
                val categoryId = info.parentId
                val category = internalChannelCategories[categoryId]

                if (category == null) {
                    logger.warn(
                        "Cannot find category(id={}) for sync channel({}). \nThis is an expected problem and please report this log to issues: https://github.com/simple-robot/simbot-component-tencent-guild/issues",
                        categoryId,
                        info
                    )
                    continue
                }

                internalChannels.compute(info.id.literal) { _, current ->
                    current?.also {
                        it.source = info
                    } ?: TencentChannelImpl(baseBot, info, this, category)
                }


            }
        }
    }

    companion object {

        suspend fun tencentGuildImpl(
            bot: TencentGuildComponentBotImpl,
            guild: Guild,
        ): TencentGuildImpl {
            // permissions
            val permissions = GetApiPermissionListApi.create(guild.id).requestBy(bot)

            return TencentGuildImpl(bot, guild, permissions)
                .also { it.initData() }
        }
    }

}



