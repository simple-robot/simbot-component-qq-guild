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
import love.forte.simbot.tencentguild.TencentGuildInfo

/**
 *
 * @author ForteScarlet
 */
internal data class InternalTencentGuildInfo(
    override var id: ID,
    override var name: String,
    override var icon: String,
    override var ownerId: ID,
    override var isBotOwner: Boolean,
    override var memberCount: Int,
    override var maxMembers: Int,
    override var description: String,
    override var joinedAt: Timestamp,
    override var unionWorldId: String?,
    override var unionOrgId: String?,
) : TencentGuildInfo


internal fun TencentGuildInfo.toInternal(copy: Boolean = true): InternalTencentGuildInfo {
    if (this is InternalTencentGuildInfo) {
        return if (copy) copy() else this
    }
    
    return InternalTencentGuildInfo(
        id,
        name,
        icon,
        ownerId,
        isBotOwner,
        memberCount,
        maxMembers,
        description,
        joinedAt,
        unionWorldId,
        unionOrgId,
    )
}
