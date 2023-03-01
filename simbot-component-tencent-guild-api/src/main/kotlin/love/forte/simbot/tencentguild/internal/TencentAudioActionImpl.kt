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

package love.forte.simbot.tencentguild.internal

import kotlinx.serialization.*
import love.forte.simbot.*
import love.forte.simbot.tencentguild.*

@Serializable
public data class TencentAudioActionImpl(
    @SerialName("guild_id")
    override val guildId: ID,
    @SerialName("channel_id")
    override val channelId: ID,
    @SerialName("audio_url")
    override val audioUrl: String,
    override val text: String
) : TencentAudioAction
