/*
 * Copyright (c) 2022-2024. ForteScarlet.
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

package love.forte.simbot.component.qguild.internal.guild

import kotlinx.coroutines.CompletableJob
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import love.forte.simbot.common.collectable.Collectable
import love.forte.simbot.common.collectable.asCollectable
import love.forte.simbot.common.id.ID
import love.forte.simbot.common.id.literal
import love.forte.simbot.component.qguild.ExperimentalQGApi
import love.forte.simbot.component.qguild.channel.QGCategoryChannel
import love.forte.simbot.component.qguild.channel.QGChannel
import love.forte.simbot.component.qguild.channel.QGForumChannel
import love.forte.simbot.component.qguild.channel.QGTextChannel
import love.forte.simbot.component.qguild.guild.QGGuild
import love.forte.simbot.component.qguild.internal.bot.QGBotImpl
import love.forte.simbot.component.qguild.internal.channel.QGCategoryChannelImpl
import love.forte.simbot.component.qguild.role.QGGuildRole
import love.forte.simbot.component.qguild.role.QGRoleCreator
import love.forte.simbot.definition.Member
import love.forte.simbot.qguild.api.apipermission.ApiPermissions
import love.forte.simbot.qguild.api.apipermission.GetApiPermissionListApi
import love.forte.simbot.qguild.api.member.GetGuildMemberListApi
import love.forte.simbot.qguild.api.member.createFlow
import love.forte.simbot.qguild.model.ChannelType
import love.forte.simbot.qguild.model.Guild
import love.forte.simbot.qguild.model.isCategory
import love.forte.simbot.qguild.stdlib.requestDataBy
import kotlin.coroutines.CoroutineContext


/**
 *
 * @author ForteScarlet
 */
internal class QGGuildImpl private constructor(
    internal val bot: QGBotImpl,
    override val source: Guild,
    override val coroutineContext: CoroutineContext
) : QGGuild {
    override suspend fun permissions(): ApiPermissions =
        GetApiPermissionListApi.create(source.id).requestDataBy(bot.source)

    override suspend fun botAsMember(): Member {
        TODO("Not yet implemented")
    }

    override val members: Collectable<QGMemberImpl>
        get() = queryMembers()

    // TODO Spec Collectable type?
    private fun queryMembers(): Collectable<QGMemberImpl> = flow { // prop ->
        // 批次
//        val batchLimit = prop.batch.takeIf { it > 0 } ?: GetGuildMemberListApi.MAX_LIMIT
        val batchLimit = GetGuildMemberListApi.MAX_LIMIT
        val flow =
            GetGuildMemberListApi.createFlow(guildId = source.id, batch = batchLimit) { requestDataBy(bot.source) }
//                .let(prop::effectOn)

        emitAll(flow.map { m ->
            QGMemberImpl(
                bot = bot,
                source = m,
                guildId = this@QGGuildImpl.id,
                sourceGuild = bot.checkIfTransmitCacheable(this@QGGuildImpl)
            )
        })
    }.asCollectable()

    override suspend fun member(id: ID): QGMemberImpl? = member(id.literal)

    internal suspend fun member(id: String): QGMemberImpl? = bot.member(source.id, id, this)

    // TODO
    @ExperimentalQGApi
    override val roles: Collectable<QGGuildRole>
        get() = TODO()
//        get() = bot.effectedFlowItems {
//            GetGuildRoleListApi.create(source.id).requestBy(bot).roles.forEach { info ->
//                val roleImpl = QGGuildRoleImpl(
//                    bot = bot,
//                    guildId = id,
//                    source = info,
//                    sourceGuild = bot.checkIfTransmitCacheable(this@QGGuildImpl)
//                )
//
//                emit(roleImpl)
//            }
//        }


    @ExperimentalQGApi
    override fun roleCreator(): QGRoleCreator = TODO() //QGRoleCreatorImpl(this)

    //
    override val channels: Collectable<QGChannel>
        get() = bot.queryChannels(source.id, bot.checkIfTransmitCacheable(this))
            .asCollectable()

    override suspend fun channel(id: ID): QGChannel? =
        bot.channel(id.literal, this)

    override val chatChannels: Collectable<QGTextChannel>
        get() = TODO("Not yet implemented")

    override suspend fun chatChannel(id: ID): QGTextChannel? {
        TODO("Not yet implemented")
    }

    override val forumChannels: Collectable<QGForumChannel>
        get() = TODO("Not yet implemented")

    override suspend fun forumChannel(id: ID): QGForumChannel? {
        TODO("Not yet implemented")
    }

    override val categories: Collectable<QGCategoryChannelImpl>
        get() = queryCategories()

    private fun queryCategories(): Collectable<QGCategoryChannelImpl> = bot.channelFlow(source.id)
        .filter { it.type.isCategory }
        .map { info ->
            QGCategoryChannelImpl(
                bot = bot,
                source = info,
                sourceGuild = this,
            )
        }.asCollectable()

    override suspend fun category(id: ID): QGCategoryChannel? {
        val channel = channel(id) ?: return null
//
        return channel as? QGCategoryChannel
            ?: throw IllegalStateException("The type of channel(id=${channel.source.id}, name=${channel.source.name}) in guild(id=${source.id}, name=$name) is not category (${ChannelType.CATEGORY}), but ${channel.source.type}")
    }


    override fun toString(): String {
        return "QGGuild(id=$id, name=$name)"
    }

    companion object {
        internal fun qgGuild(bot: QGBotImpl, guild: Guild): QGGuildImpl {
            val job: CompletableJob = SupervisorJob(bot.coroutineContext[Job])
            val coroutineContext: CoroutineContext = bot.coroutineContext + job

            return QGGuildImpl(bot, guild, coroutineContext)
        }
    }
}
