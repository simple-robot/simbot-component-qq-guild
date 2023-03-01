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

package love.forte.simbot.tencentguild

import kotlinx.serialization.KSerializer
import love.forte.simbot.ID
import love.forte.simbot.Timestamp
import love.forte.simbot.definition.GuildInfo
import love.forte.simbot.tencentguild.internal.TencentGuildInfoImpl

/**
 *
 * [频道]()
 *
 * @author ForteScarlet
 */
public interface TencentGuildInfo : GuildInfo, TencentGuildObjective {

    /**
     * 频道ID
     */
    override val id: ID

    /**
     * 频道名称
     */
    override val name: String

    /**
     * 频道头像地址
     */
    override val icon: String

    /**
     * 创建人用户ID
     */
    override val ownerId: ID

    /**
     * 当前人是否是创建人
     */
    public val isBotOwner: Boolean

    /**
     * 成员数
     */
    public val memberCount: Int

    /**
     * 最大成员数
     */
    public val maxMembers: Int

    /**
     * 描述
     */
    override val description: String

    /**
     * 加入时间.
     */
    public val joinedAt: Timestamp

    /**
     * 游戏绑定公会区服ID，需要特殊申请并配置后才会返回
     */
    public val unionWorldId: String?

    /**
     * 游戏绑定公会/战队ID，需要特殊申请并配置后才会返回
     */
    public val unionOrgId: String?

    override val currentChannel: Int get() = -1
    override val maximumChannel: Int get() = -1
    override val createTime: Timestamp get() = Timestamp.NotSupport
    override val currentMember: Int get() = memberCount
    override val maximumMember: Int get() = maxMembers

    public companion object {
        internal val serializer: KSerializer<out TencentGuildInfo> = TencentGuildInfoImpl.serializer()
    }
}
