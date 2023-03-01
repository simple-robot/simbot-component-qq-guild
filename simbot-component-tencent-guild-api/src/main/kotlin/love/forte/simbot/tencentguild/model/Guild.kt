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

/**
 *
 * [频道对象(Guild)](https://bot.q.qq.com/wiki/develop/api/openapi/guild/model.html)
 *
 * 频道对象中所涉及的 ID 类数据，都仅在机器人场景流通，与真实的 ID 无关。请不要理解为真实的 ID -私信场景下的 guild_id 为私信临时频道的 ID，获取私信来源频道信息请使用 `src_guild_id`
 */
@ApiModel
@Serializable
public data class Guild(
    /** 频道ID */
    val id: String,
    /** 频道名称 */
    val name: String,
    /** 频道头像地址 */
    val icon: String,
    /** 创建人用户ID */
    @SerialName("owner_id") val ownerId: String,
    /** 当前人是否是创建人 */
    @SerialName("owner") val isOwner: Boolean,
    /** 成员数 */
    @SerialName("member_count") val memberCount: Int,
    /** 最大成员数 */
    @SerialName("max_members") val maxMembers: Int,
    /** 描述 */
    val description: String,

    /** 加入时间 */
    @SerialName("joined_at") @Serializable(InstantISO8601Serializer::class) val joinedAt: Instant
)

/*
id	string	频道ID
name	string	频道名称
icon	string	频道头像地址
owner_id	string	创建人用户ID
owner	bool	当前人是否是创建人
member_count	int	成员数
max_members	int	最大成员数
description	string	描述
joined_at	string	加入时间
 */
