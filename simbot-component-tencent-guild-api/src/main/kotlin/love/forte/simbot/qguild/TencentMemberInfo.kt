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

package love.forte.simbot.qguild

import kotlinx.serialization.KSerializer
import love.forte.simbot.ID
import love.forte.simbot.Timestamp
import love.forte.simbot.definition.MemberInfo
import love.forte.simbot.qguild.internal.TencentMemberInfoImpl

/**
 * [成员](https://bot.q.qq.com/wiki/develop/api/openapi/member/model.html)
 *
 * @author ForteScarlet
 */
public interface TencentMemberInfo : MemberInfo, TencentGuildObjective {
    /**
     * 频道id
     */
    public val guildId: ID?

    /**
     * 用户基础信息
     */
    public val user: TencentUserInfo

    /**
     * 用户在频道内的昵称
     */
    public val nick: String

    /**
     * 用户在频道内的身份
     */
    public val roleIds: List<ID>

    /**
     * iISO8601 timestamp	用户加入频道的时间
     */
    public val joinedAt: Timestamp

    override val avatar: String
        get() = user.avatar
    override val id: ID
        get() = user.id
    override val username: String
        get() = user.username
    override val joinTime: Timestamp
        get() = joinedAt
    override val nickname: String
        get() = nick

    public companion object {
        internal val serializer: KSerializer<out TencentMemberInfo> = TencentMemberInfoImpl.serializer()
    }
}
