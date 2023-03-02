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
import love.forte.simbot.ID
import love.forte.simbot.Timestamp
import love.forte.simbot.qguild.TencentGuildInfo
import love.forte.simbot.qguild.TimestampISO8601Serializer


/**
 *
 *  [频道对象](https://bot.q.qq.com/wiki/develop/api/openapi/guild/model.html)
 *
 * @author ForteScarlet
 */
@Serializable
internal data class TencentGuildInfoImpl(
    override val id: CharSequenceID,
    override val name: String,
    override val icon: String = "", // TODO 缺失
    @SerialName("owner_id")
    override val ownerId: CharSequenceID = "".ID, // TODO 缺失
    @SerialName("owner")
    override val isBotOwner: Boolean,
    @SerialName("member_count")
    override val memberCount: Int = -1,     // TODO 缺失
    @SerialName("max_members")
    override val maxMembers: Int = -1,      // TODO 缺失
    override val description: String = "",  // TODO 缺失
    @SerialName("joined_at")
    @Serializable(TimestampISO8601Serializer::class)
    override val joinedAt: Timestamp = Timestamp.NotSupport, // TODO 缺失
    @SerialName("union_world_id")
    override val unionWorldId: String? = null,
    @SerialName("union_org_id")
    override val unionOrgId: String? = null,
) : TencentGuildInfo
