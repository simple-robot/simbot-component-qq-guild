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
import love.forte.simbot.component.qguild.internal.QGGuildImpl
import love.forte.simbot.component.qguild.role.QGGuildRole
import love.forte.simbot.component.qguild.role.QGRoleCreator
import love.forte.simbot.component.qguild.util.requestBy
import love.forte.simbot.literal
import love.forte.simbot.qguild.api.role.CreateGuildRoleApi


/**
 *
 * @author ForteScarlet
 */
@OptIn(ExperimentalSimbotApi::class)
internal class QGRoleCreatorImpl(private val guild: QGGuildImpl) : QGRoleCreator {
    override var name: String? = null
    override var color: Int? = null
    override var isHoist: Boolean? = null

    override suspend fun create(): QGGuildRole {
        if (name == null && color == null && isHoist == null) {
            throw IllegalArgumentException("No Parameters are set")
        }

        val created = CreateGuildRoleApi.create(guild.id.literal, name, color, isHoist?.let { if (it) 1 else 0 })
            .requestBy(guild.baseBot)

        return QGGuildRoleImpl(guild.baseBot, guild.id, created.role, guild)
    }
}
