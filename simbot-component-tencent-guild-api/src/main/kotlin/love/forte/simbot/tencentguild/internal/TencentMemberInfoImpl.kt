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

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import love.forte.simbot.CharSequenceID
import love.forte.simbot.Timestamp
import love.forte.simbot.tencentguild.TencentMemberInfo
import love.forte.simbot.tencentguild.TimestampISO8601Serializer

/**
 *
 * @author ForteScarlet
 */
@Serializable
internal data class TencentMemberInfoImpl(
    @SerialName("guild_id")
    override val guildId: CharSequenceID? = null,
    override val user: TencentUserInfoImpl,
    override val nick: String = "",
    @SerialName("roles")
    override val roleIds: List<CharSequenceID> = emptyList(),
    @SerialName("joined_at")
    @Serializable(TimestampISO8601Serializer::class)
    override val joinedAt: Timestamp = Timestamp.NotSupport
) : TencentMemberInfo


@Serializable
internal data class TencentMemberInfoImplForMessage(
    @SerialName("guild_id")
    override var guildId: CharSequenceID? = null,
    override val nick: String = "",
    @SerialName("roles")
    override val roleIds: List<CharSequenceID> = emptyList(),
    @SerialName("joined_at")
    @Serializable(TimestampISO8601Serializer::class)
    override val joinedAt: Timestamp = Timestamp.NotSupport
) : TencentMemberInfo {
    @Transient
    override lateinit var user: TencentUserInfoImpl
}
