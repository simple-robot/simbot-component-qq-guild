/*
 * Copyright (c) 2022-2023. ForteScarlet.
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

import kotlinx.coroutines.CompletableJob
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.*
import love.forte.simbot.ExperimentalSimbotApi
import love.forte.simbot.ID
import love.forte.simbot.Timestamp
import love.forte.simbot.component.qguild.QGChannel
import love.forte.simbot.component.qguild.QGChannelCategory
import love.forte.simbot.component.qguild.QGChannelCategoryId
import love.forte.simbot.component.qguild.QGGuild
import love.forte.simbot.component.qguild.forum.QGForums
import love.forte.simbot.component.qguild.internal.forum.QGForumChannelImpl
import love.forte.simbot.component.qguild.internal.forum.QGForumsImpl
import love.forte.simbot.component.qguild.internal.role.QGGuildRoleImpl
import love.forte.simbot.component.qguild.internal.role.QGRoleCreatorImpl
import love.forte.simbot.component.qguild.role.QGRoleCreator
import love.forte.simbot.component.qguild.util.requestBy
import love.forte.simbot.literal
import love.forte.simbot.qguild.InternalApi
import love.forte.simbot.qguild.QQGuildApiException
import love.forte.simbot.qguild.api.apipermission.ApiPermissions
import love.forte.simbot.qguild.api.apipermission.GetApiPermissionListApi
import love.forte.simbot.qguild.api.channel.GetChannelApi
import love.forte.simbot.qguild.api.channel.GetGuildChannelListApi
import love.forte.simbot.qguild.api.member.GetGuildMemberListApi
import love.forte.simbot.qguild.api.member.GetMemberApi
import love.forte.simbot.qguild.api.member.createFlow
import love.forte.simbot.qguild.api.role.GetGuildRoleListApi
import love.forte.simbot.qguild.ifNotFoundThenNull
import love.forte.simbot.qguild.model.ChannelType
import love.forte.simbot.qguild.model.Guild
import love.forte.simbot.qguild.model.SimpleChannel
import love.forte.simbot.qguild.model.isCategory
import love.forte.simbot.utils.item.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.coroutines.CoroutineContext


/**
 *
 * @author ForteScarlet
 */
internal class QGGuildImpl private constructor(
    internal val baseBot: QGBotImpl,
    override val source: Guild,
    override val coroutineContext: CoroutineContext
) : QGGuild {

    override val id: ID = source.id.ID
    override val ownerId: ID = source.ownerId.ID

    @OptIn(ExperimentalSimbotApi::class)
    override val joinTime: Timestamp get() = source.joinedAt.toTimestamp()
    override val currentMember: Int get() = source.memberCount
    override val description: String get() = source.description
    override val icon: String get() = source.icon
    override val maximumMember: Int get() = source.maxMembers
    override val name: String get() = source.name

    override fun toString(): String {
        return "QGGuildImpl(id=$id, name=$name)"
    }

    override suspend fun permissions(): ApiPermissions = GetApiPermissionListApi.create(source.id).requestBy(baseBot)

    override val bot: QGGuildBotImpl = QGGuildBotImpl(
        bot = baseBot,
        guildId = source.id,
        sourceGuild = baseBot.checkIfTransmitCacheable(this)
    )

    override suspend fun owner(): QGMemberImpl {
        return member(ownerId) ?: throw NoSuchElementException("owner(id=$ownerId)")
    }

    override val members: Items<QGMemberImpl>
        get() = queryMembers()

    private fun queryMembers(): Items<QGMemberImpl> = flowItems { prop ->
        // 批次
        val batchLimit = prop.batch.takeIf { it > 0 } ?: GetGuildMemberListApi.MAX_LIMIT
        val flow =
            GetGuildMemberListApi.createFlow(guildId = source.id, batch = batchLimit) { requestBy(baseBot) }
                .let(prop::effectOn)

        emitAll(flow.map { m ->
            QGMemberImpl(
                bot = baseBot,
                source = m,
                guildId = this@QGGuildImpl.id,
                sourceGuild = baseBot.checkIfTransmitCacheable(this@QGGuildImpl)
            )
        })
    }

    override suspend fun member(id: ID): QGMemberImpl? = member(id.literal)

    internal suspend fun member(id: String): QGMemberImpl? = queryMember(id)

    private suspend fun queryMember(id: String): QGMemberImpl? {
        val member = try {
            GetMemberApi.create(source.id, id).requestBy(baseBot)
        } catch (apiEx: QQGuildApiException) {
            apiEx.ifNotFoundThenNull()
        }

        return member?.let { info ->
            QGMemberImpl(
                bot = baseBot,
                source = info,
                guildId = this@QGGuildImpl.id,
                sourceGuild = baseBot.checkIfTransmitCacheable(this)
            )
        }
    }

    @ExperimentalSimbotApi
    override val roles: Items<QGGuildRoleImpl>
        get() = bot.effectedFlowItems {
            GetGuildRoleListApi.create(source.id).requestBy(baseBot).roles.forEach { info ->
                val roleImpl = QGGuildRoleImpl(
                    bot = baseBot,
                    guildId = id,
                    source = info,
                    sourceGuild = baseBot.checkIfTransmitCacheable(this@QGGuildImpl)
                )

                emit(roleImpl)
            }
        }


    @ExperimentalSimbotApi
    override fun roleCreator(): QGRoleCreator = QGRoleCreatorImpl(this)

    override val channels: Items<QGChannel>
        get() = queryChannels()

    private fun channelFlow(): Flow<SimpleChannel> {
        return flow {
            GetGuildChannelListApi.create(source.id)
                .requestBy(baseBot)
                .forEach {
                    emit(it)
                }
        }
    }

    internal data class ChannelInfoWithCategory(val info: SimpleChannel, val categoryId: QGChannelCategoryId)

    internal fun channelFlowWithCategoryId(): Flow<ChannelInfoWithCategory> {
        val categoryMap = ConcurrentHashMap<String, QGChannelCategoryId>()
        return channelFlow().filter { info ->
            if (info.type.isCategory) {
                categoryMap.computeIfAbsent(info.parentId) {
                    QGChannelCategoryImpl(
                        bot = bot,
                        source = info,
                        sourceGuild = baseBot.checkIfTransmitCacheable(this@QGGuildImpl)
                    )
                }

                false
            } else {
                true
            }
        }.map { info ->
            val category = categoryMap.computeIfAbsent(info.parentId) { cid ->
                QGChannelCategoryIdImpl(
                    bot = bot,
                    guildId = info.guildId.ID,
                    id = cid.ID,
                    sourceGuild = baseBot.checkIfTransmitCacheable(this@QGGuildImpl)
                )
            }

            ChannelInfoWithCategory(info, category)
        }
    }

    /**
     * 通过API实时查询channels列表
     */
    @OptIn(InternalApi::class)
    private fun queryChannels(): Items<QGChannel> = effectedItemsByFlow {
        channelFlowWithCategoryId().map { (info, category) ->
            when (info.type) {
                ChannelType.TEXT -> QGTextChannelImpl(
                    bot = bot,
                    source = info,
                    sourceGuild = baseBot.checkIfTransmitCacheable(this@QGGuildImpl),
                    category = category,
                )

                ChannelType.FORUM -> QGForumChannelImpl(
                    bot = bot,
                    source = info,
                    sourceGuild = baseBot.checkIfTransmitCacheable(this@QGGuildImpl),
                    category = category,
                )

                else -> QGNonTextChannelImpl(
                    bot = bot,
                    source = info,
                    sourceGuild = baseBot.checkIfTransmitCacheable(this@QGGuildImpl),
                    category = category
                )
            }

        }
    }

    /**
     * @throws QQGuildApiException
     */
    private suspend fun queryChannel(id: ID): SimpleChannel? {
        return try {
            GetChannelApi.create(id.literal).requestBy(baseBot)
        } catch (apiEx: QQGuildApiException) {
            apiEx.ifNotFoundThenNull()
        }
    }

    @OptIn(InternalApi::class)
    override suspend fun channel(id: ID): QGChannel? {
        val channelInfo = queryChannel(id) ?: return null

        return when (channelInfo.type) {
            ChannelType.CATEGORY -> QGChannelCategoryImpl(
                bot = bot,
                source = channelInfo,
                sourceGuild = baseBot.checkIfTransmitCacheable(this@QGGuildImpl),
            ) // throw IllegalStateException("The type of channel(id=${channelInfo.id}, name=${channelInfo.name}) is CATEGORY. Maybe you should use [guild.category(id)]?")
            ChannelType.TEXT -> QGTextChannelImpl(
                bot = bot,
                source = channelInfo,
                sourceGuild = baseBot.checkIfTransmitCacheable(this@QGGuildImpl),
            )

            ChannelType.FORUM -> QGForumChannelImpl(
                bot = bot,
                source = channelInfo,
                sourceGuild = baseBot.checkIfTransmitCacheable(this@QGGuildImpl),
            )

            else -> QGNonTextChannelImpl(
                bot = bot,
                source = channelInfo,
                sourceGuild = baseBot.checkIfTransmitCacheable(this@QGGuildImpl),
            )
        }
    }

    override val categories: Items<QGChannelCategoryImpl>
        get() = queryCategories()

    private fun queryCategories(): Items<QGChannelCategoryImpl> = effectedItemsByFlow {
        channelFlow()
            .filter { it.type.isCategory }
            .map { info ->
                QGChannelCategoryImpl(
                    bot = bot,
                    source = info,
                    sourceGuild = baseBot.checkIfTransmitCacheable(this@QGGuildImpl)
                )
            }
    }

    override suspend fun category(id: ID): QGChannelCategory? {
        val channel = channel(id) ?: return null

        return channel as? QGChannelCategory
            ?: throw IllegalStateException("The type of channel(id=${channel.source.id}, name=${channel.source.name}) in guild(id=${source.id}, name=$name) is not category (${ChannelType.CATEGORY}), but ${channel.source.type}")
    }

    @Volatile
    private lateinit var _forums: QGForums

    override val forums: QGForums
        get() {
            if (::_forums.isInitialized) {
                return _forums
            }

            synchronized(this) {
                if (::_forums.isInitialized) {
                    return _forums
                }

                return initForums().also {
                    _forums = it
                }
            }

        }

    private fun initForums(): QGForums = QGForumsImpl(this)

    companion object {
        internal fun qgGuild(bot: QGBotImpl, guild: Guild): QGGuildImpl {
            val job: CompletableJob = SupervisorJob(bot.coroutineContext[Job])
            val coroutineContext: CoroutineContext = bot.coroutineContext + job

            return QGGuildImpl(bot, guild, coroutineContext)
        }
    }
}
