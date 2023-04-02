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
import love.forte.simbot.component.qguild.QGGuild
import love.forte.simbot.component.qguild.QGMember
import love.forte.simbot.component.qguild.internal.QGBotImpl
import love.forte.simbot.component.qguild.internal.QGMemberImpl
import love.forte.simbot.component.qguild.internal.newSupervisorCoroutineContext
import love.forte.simbot.component.qguild.role.QGMemberRole
import love.forte.simbot.component.qguild.role.QGRoleUpdater
import love.forte.simbot.component.qguild.util.requestBy
import love.forte.simbot.literal
import love.forte.simbot.qguild.api.member.GetMemberApi
import love.forte.simbot.qguild.api.role.RemoveMemberRoleApi
import love.forte.simbot.qguild.model.Role
import kotlin.coroutines.CoroutineContext


/**
 *
 * @author ForteScarlet
 */
@OptIn(ExperimentalSimbotApi::class)
internal class QGMemberRoleImpl(
    override val guildRole: QGGuildRoleImpl, override val memberId: ID, private val sourceMember: QGMember?
) : BaseQGRole(), QGMemberRole {
    override val coroutineContext: CoroutineContext = guildRole.bot.newSupervisorCoroutineContext()

    override val id: ID get() = guildRole.id

    override val guildId: ID
        get() = guildRole.guildId

    override val bot: QGBotImpl
        get() = guildRole.bot

    override var source: Role
        get() = guildRole.source
        set(value) {
            guildRole.source = value
        }

    override suspend fun guild(): QGGuild = guildRole.guild()
    override fun updater(): QGRoleUpdater = guildRole.updater()
    override suspend fun member(): QGMember {
        return sourceMember ?: GetMemberApi.create(guildId.literal, memberId.literal).requestBy(bot).let { QGMemberImpl(bot, it, guildId) }
    }

    override suspend fun delete(): Boolean = delete0(null)
    override suspend fun delete(channelId: ID): Boolean = delete0(channelId.literal)

    private suspend fun delete0(channelId: String?): Boolean {
        RemoveMemberRoleApi.create(guildId.literal, memberId.literal, id.literal, channelId).requestBy(bot)
        return true
    }
}
