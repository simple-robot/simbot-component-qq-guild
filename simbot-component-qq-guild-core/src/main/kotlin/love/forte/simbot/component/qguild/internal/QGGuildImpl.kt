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
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import love.forte.simbot.ExperimentalSimbotApi
import love.forte.simbot.ID
import love.forte.simbot.Timestamp
import love.forte.simbot.component.qguild.QGChannel
import love.forte.simbot.component.qguild.QGChannelCategoryId
import love.forte.simbot.component.qguild.QGGuild
import love.forte.simbot.component.qguild.internal.role.QGGuildRoleImpl
import love.forte.simbot.component.qguild.internal.role.QGRoleCreatorImpl
import love.forte.simbot.component.qguild.role.QGRoleCreator
import love.forte.simbot.component.qguild.util.requestBy
import love.forte.simbot.literal
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
import love.forte.simbot.qguild.model.isCategory
import love.forte.simbot.utils.item.Items
import love.forte.simbot.utils.item.effectOn
import love.forte.simbot.utils.item.effectedFlowItems
import love.forte.simbot.utils.item.flowItems
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

    override val maximumChannel: Int get() = -1

    @OptIn(ExperimentalSimbotApi::class)
    override val joinTime: Timestamp get() = source.joinedAt.toTimestamp()
    override val currentMember: Int get() = source.memberCount
    override val description: String get() = source.description
    override val icon: String get() = source.icon
    override val maximumMember: Int get() = source.maxMembers
    override val name: String get() = source.name

    override fun toString(): String {
        return "QGGuildImpl(id=$id, name=$name, bot=$baseBot, guild=$source)"
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

    override val currentChannel: Int get() = -1

    override val channels: Items<QGChannelImpl>
        get() = queryChannels()

    /**
     * 通过API实时查询channels列表
     */
    private fun queryChannels(): Items<QGChannelImpl> = effectedFlowItems {
        val categoryMap = ConcurrentHashMap<String, QGChannelCategoryId>()
        val flow = GetGuildChannelListApi
            .create(source.id)
            .requestBy(baseBot)
            .asFlow()
            .filter { info ->
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
            }
            .map { info ->
                val category = categoryMap.computeIfAbsent(info.parentId) { cid ->
                    QGChannelCategoryIdImpl(
                        bot = bot,
                        guildId = info.guildId.ID,
                        id = cid.ID,
                        sourceGuild = baseBot.checkIfTransmitCacheable(this@QGGuildImpl)
                    )
                }

                QGChannelImpl(
                    bot = bot,
                    source = info,
                    sourceGuild = baseBot.checkIfTransmitCacheable(this@QGGuildImpl),
                    category = category,
                )
            }


        emitAll(flow)
    }

    override suspend fun channel(id: ID): QGChannel? {
        // by api
        val channelInfo = try {
            GetChannelApi.create(id.literal).requestBy(baseBot)
        } catch (apiEx: QQGuildApiException) {
            apiEx.ifNotFoundThenNull()
        } ?: return null

        return QGChannelImpl(
            bot = bot,
            source = channelInfo,
            sourceGuild = baseBot.checkIfTransmitCacheable(this)
        )
    }

    override val categories: Items<QGChannelCategoryImpl>
        get() = queryCategories()

    private fun queryCategories(): Items<QGChannelCategoryImpl> = effectedFlowItems {
        val flow = GetGuildChannelListApi
            .create(source.id)
            .requestBy(baseBot)
            .asFlow()
            .filter { it.type.isCategory }
            .map { info ->
                QGChannelCategoryImpl(
                    bot = bot,
                    source = info,
                    sourceGuild = baseBot.checkIfTransmitCacheable(this@QGGuildImpl)
                )
            }

        emitAll(flow)
    }

    override suspend fun category(id: ID): QGChannelCategoryImpl? {
        val info = try {
            GetChannelApi.create(id.literal).requestBy(baseBot)
        } catch (apiEx: QQGuildApiException) {
            apiEx.ifNotFoundThenNull()
        }

        if (info != null && info.type != ChannelType.CATEGORY) {
            throw IllegalStateException("The type of channel(id=${info.id}, name=${info.name}) in guild(id=${info.guildId}, name=$name) is not category, but ${info.type}(${info.type.value})")
        }

        return info?.let {
            QGChannelCategoryImpl(
                bot = bot,
                source = it,
                sourceGuild = baseBot.checkIfTransmitCacheable(this@QGGuildImpl)
            )
        }
    }


    companion object {
        internal fun qgGuild(bot: QGBotImpl, guild: Guild): QGGuildImpl {
            val job: CompletableJob = SupervisorJob(bot.coroutineContext[Job])
            val coroutineContext: CoroutineContext = bot.coroutineContext + job

            return QGGuildImpl(bot, guild, coroutineContext)
        }
    }
}

