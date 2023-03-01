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
import love.forte.simbot.definition.ChannelInfo
import love.forte.simbot.tencentguild.internal.TencentChannelInfoImpl

/**
 *
 * tencent channel info
 * @author ForteScarlet
 */
public interface TencentChannelInfo : ChannelInfo, TencentGuildObjective {
    /**
     * 子频道id
     */
    override val id: ID
    
    /**
     * 频道id
     */
    override val guildId: ID
    
    /**
     * 子频道名
     */
    override val name: String
    
    /**
     * 子频道类型
     * @see ChannelType
     */
    @get:JvmSynthetic
    public val channelType: ChannelType
    
    /**
     * 子频道类型
     * @see ChannelType
     */
    public val channelTypeValue: Int get() = channelType.code
    
    /**
     * 子频道子类型
     * @see ChannelSubType
     */
    @get:JvmSynthetic
    public val channelSubType: ChannelSubType
    
    /**
     * 子频道子类型
     * @see ChannelSubType
     */
    public val channelSubTypeValue: Int get() = channelSubType.code
    
    /**
     * 排序，必填，而且不能够和其他子频道的值重复
     */
    public val position: Int
    
    /**
     * 分组 id
     */
    public val parentId: String
    
    /**
     * 创建人 id
     */
    override val ownerId: ID
    
    
    @Deprecated("子频道没有创建时间信息", ReplaceWith("Timestamp.NotSupport", "love.forte.simbot.Timestamp"))
    override val createTime: Timestamp
        get() = Timestamp.NotSupport
    
    @Deprecated("子频道没有人数信息", ReplaceWith("-1"))
    override val currentMember: Int
        get() = -1
    
    @Deprecated("子频道没有描述信息", ReplaceWith("\"\""))
    override val description: String
        get() = ""
    
    @Deprecated("子频道没有图标", ReplaceWith("\"\""))
    override val icon: String
        get() = ""
    
    @Deprecated("子频道没有人数信息", ReplaceWith("-1"))
    override val maximumMember: Int
        get() = -1
    
    public companion object {
        internal val serializer: KSerializer<out TencentChannelInfo> = TencentChannelInfoImpl.serializer()
    }
}


