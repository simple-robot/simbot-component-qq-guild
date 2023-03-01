/*
 * Copyright (c) 2023. ForteScarlet.
 *
 * This file is part of simbot-component-tencent-guild.
 *
 * simbot-component-tencent-guild is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * simbot-component-tencent-guild is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with simbot-component-tencent-guild. If not, see <https://www.gnu.org/licenses/>.
 */

package love.forte.simbot.tencentguild.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.tencentguild.ApiModel
import love.forte.simbot.tencentguild.utils.InstantISO8601Serializer
import java.time.Instant

@Serializable
public abstract class SimpleMember {

    /**
     * 用户的昵称
     */
    public abstract val nick: String

    /**
     * 用户在频道内的身份组ID, 默认值可参考 [DefaultRoles](https://bot.q.qq.com/wiki/develop/api/openapi/guild/role_model.html#DefaultRoles)
     */
    public abstract val roles: List<String>

    /**
     * 用户加入频道的时间
     */
    @Serializable(InstantISO8601Serializer::class)
    @SerialName("join_at")
    public abstract val joinedAt: Instant
}

/**
 * [成员对象(Member)](https://bot.q.qq.com/wiki/develop/api/openapi/member/model.html)
 */
@ApiModel
@Serializable
public data class Member(
    /**
     * 用户的频道基础信息，只有成员相关接口中会填充此信息
     */
    val user: User,
    /**
     * 用户的昵称
     */
    val nick: String,
    /**
     * 用户在频道内的身份组ID, 默认值可参考 [DefaultRoles](https://bot.q.qq.com/wiki/develop/api/openapi/guild/role_model.html#DefaultRoles)
     */
    val roles: List<String>,
    /**
     * 用户加入频道的时间
     */
    @Serializable(InstantISO8601Serializer::class) @SerialName("join_at") val joinedAt: Instant
)

/**
 * [MemberWithGuildID](https://bot.q.qq.com/wiki/develop/api/openapi/member/model.html#memberwithguildid)
 *
 */
@ApiModel
@Serializable
public data class MemberWithGuildID(

    /**
     * 频道id
     */
    @SerialName("guild_id") val guildId: String,
    /**
     * 用户的频道基础信息，只有成员相关接口中会填充此信息
     */
    val user: User,
    /**
     * 用户的昵称
     */
    val nick: String,
    /**
     * 用户在频道内的身份组ID, 默认值可参考 [DefaultRoles](https://bot.q.qq.com/wiki/develop/api/openapi/guild/role_model.html#DefaultRoles)
     */
    val roles: List<String>,
    /**
     * 用户加入频道的时间
     */
    @Serializable(InstantISO8601Serializer::class) @SerialName("join_at") val joinedAt: Instant
)
