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
import love.forte.simbot.tencentguild.ChannelSubType
import love.forte.simbot.tencentguild.ChannelType
import love.forte.simbot.tencentguild.TencentChannelInfo

internal data class InternalTencentChannelInfo(
    override var id: ID,
    override var guildId: ID,
    override var name: String,
    override var channelType: ChannelType,
    override var channelSubType: ChannelSubType,
    override var position: Int,
    override var parentId: String,
    override var ownerId: ID,
) : TencentChannelInfo


internal fun TencentChannelInfo.toInternal(copy: Boolean = true): InternalTencentChannelInfo {
    if (this is InternalTencentChannelInfo) {
        return if (copy) copy() else this
    }
    
    return InternalTencentChannelInfo(
        id,
        guildId,
        name,
        channelType,
        channelSubType,
        position,
        parentId,
        ownerId,
    )
}
