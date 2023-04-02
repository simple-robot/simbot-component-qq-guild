/*
 * Copyright (c) 2023. ForteScarlet.
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

package love.forte.simbot.component.qguild.internal.role

import love.forte.simbot.ExperimentalSimbotApi
import love.forte.simbot.ID
import love.forte.simbot.component.qguild.QGBot
import love.forte.simbot.component.qguild.QGGuild
import love.forte.simbot.component.qguild.QGMember
import love.forte.simbot.component.qguild.internal.QGGuildImpl
import love.forte.simbot.component.qguild.internal.newSupervisorCoroutineContext
import love.forte.simbot.component.qguild.role.QGGuildRole
import love.forte.simbot.component.qguild.role.QGRoleUpdater
import love.forte.simbot.component.qguild.util.requestBy
import love.forte.simbot.literal
import love.forte.simbot.qguild.api.role.AddMemberRoleApi
import love.forte.simbot.qguild.api.role.DeleteGuildRoleApi
import love.forte.simbot.qguild.model.Role
import kotlin.coroutines.CoroutineContext


/**
 *
 * @author ForteScarlet
 */
@OptIn(ExperimentalSimbotApi::class)
internal class QGGuildRoleImpl(
    override val bot: QGBot,
    override val guildId: ID,
    @Volatile override var source: Role,
    private val sourceGuild: QGGuildImpl
) : BaseQGRole(), QGGuildRole {
    override val coroutineContext: CoroutineContext = bot.newSupervisorCoroutineContext()
    override val id: ID = source.id.ID

    override suspend fun guild(): QGGuild =
        sourceGuild // ?: bot.guild(guildId) ?: throw NoSuchElementException("Guild($guildId)")

    override fun updater(): QGRoleUpdater = QGRoleUpdaterImpl(this)

    override suspend fun grantTo(memberId: ID): QGMemberRoleImpl = grantTo0(memberId, null)

    override suspend fun grantTo(member: QGMember): QGMemberRoleImpl = grantTo0(member, null)

    override suspend fun grantTo(memberId: ID, channelId: ID): QGMemberRoleImpl = grantTo0(memberId, channelId.literal)

    override suspend fun grantTo(member: QGMember, channelId: ID): QGMemberRoleImpl =
        grantTo0(member, channelId.literal)

    private suspend fun grantTo0(memberId: ID, channelId: String?): QGMemberRoleImpl {
        AddMemberRoleApi.create(guildId.literal, memberId.literal, id.literal, channelId).requestBy(bot)
        return QGMemberRoleImpl(this, memberId, null)
    }

    private suspend fun grantTo0(member: QGMember, channelId: String?): QGMemberRoleImpl {
        AddMemberRoleApi.create(guildId.literal, member.id.literal, id.literal, channelId).requestBy(bot)
        return QGMemberRoleImpl(this, member.id, member)
    }

    override suspend fun delete(): Boolean {
        DeleteGuildRoleApi.create(guildId.literal, source.id).requestBy(bot)
        return true
    }
}
