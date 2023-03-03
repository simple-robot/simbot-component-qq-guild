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

package love.forte.simbot.component.tencentguild.internal.info

import love.forte.simbot.ID
import love.forte.simbot.Timestamp

/**
 *
 * @author ForteScarlet
 */
internal data class InternalTencentMemberInfo(
    override var guildId: ID?,
    override var user: TencentUserInfo,
    override var nick: String,
    override var roleIds: List<ID>,
    override var joinedAt: Timestamp,
) : TencentMemberInfo


internal fun TencentMemberInfo.toInternal(
    copy: Boolean = true, copyUser: Boolean = true, copyRoleIds: Boolean = true,
): InternalTencentMemberInfo {
    if (this is InternalTencentMemberInfo) {
        return if (copy) {
            val user = user
            val roleIds = roleIds
            copy(
                user = if (copyUser) user.toInternal(true) else user,
                roleIds = if (copyRoleIds) roleIds.toList() else roleIds
            )
        } else {
            this
        }
    }
    
    return InternalTencentMemberInfo(
        guildId = guildId,
        user = user.toInternal(copyUser),
        nick = nick,
        roleIds = if (copyRoleIds) roleIds.toList() else roleIds,
        joinedAt = joinedAt
    )
}
