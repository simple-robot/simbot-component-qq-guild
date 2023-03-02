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

package love.forte.simbot.qguild.internal

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.CharSequenceID
import love.forte.simbot.qguild.ChannelSubType
import love.forte.simbot.qguild.ChannelType
import love.forte.simbot.qguild.TencentChannelInfo


@Serializable
internal data class TencentChannelInfoImpl(
    override val id: CharSequenceID,
    @SerialName("guild_id")
    override val guildId: CharSequenceID,
    
    override val name: String,
    
    @SerialName("type")
    @get:JvmSynthetic
    override val channelType: ChannelType,
    
    @SerialName("sub_type")
    override val channelSubType: ChannelSubType,
    
    override val position: Int,
    @SerialName("parent_id")
    override val parentId: String,
    
    @SerialName("owner_id")
    override val ownerId: CharSequenceID,
) : TencentChannelInfo
