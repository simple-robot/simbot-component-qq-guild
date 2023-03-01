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

package love.forte.simbot.component.tencentguild

import love.forte.plugin.suspendtrans.annotation.JvmAsync
import love.forte.plugin.suspendtrans.annotation.JvmBlocking
import love.forte.simbot.ID
import love.forte.simbot.Timestamp
import love.forte.simbot.definition.Guild
import love.forte.simbot.definition.Organization
import love.forte.simbot.utils.item.Items
import love.forte.simbot.utils.item.Items.Companion.emptyItems

/**
 *
 * @author ForteScarlet
 */
public interface TencentGuild : Guild {
    override val bot: TencentGuildComponentGuildBot
    override val createTime: Timestamp
    override val currentMember: Int
    override val description: String
    override val icon: String
    override val id: ID
    override val maximumMember: Int
    override val name: String
    override val ownerId: ID

    public val source: love.forte.simbot.tencentguild.model.Guild

    /**
     * 得到此频道服务器下的所有子频道。
     */
    override val channels: Items<TencentChannel>
    
    
    /**
     * 得到此频道服务器下的所有子频道的列表副本。
     */
    public val channelList: List<TencentChannel>
    
    /**
     * 获取指定ID的子频道。
     */
    @JvmBlocking(baseName = "getChannel", suffix = "")
    @JvmAsync(baseName = "getChannel")
    override suspend fun channel(id: ID): TencentChannel?
    
    
    /**
     * 得到此频道服务器下的所有子频道。
     *
     * @see channels
     */
    override val children: Items<TencentChannel> get() = channels
    
    
    /**
     * 获取指定ID的子频道。
     *
     * @see channel
     */
    @JvmBlocking(baseName = "getChild", suffix = "")
    @JvmAsync(baseName = "getChild")
    override suspend fun child(id: ID): TencentChannel? = channel(id)
    
    
    @JvmBlocking(asProperty = true, suffix = "")
    @JvmAsync(asProperty = true)
    override suspend fun owner(): TencentMember
    
    
    /**
     * 获取指定成员信息。
     */
    @JvmBlocking(baseName = "getMember", suffix = "")
    @JvmAsync(baseName = "getMember")
    override suspend fun member(id: ID): TencentMember?
    
    /**
     * 频道无法获取成员列表。
     */
    @Deprecated(
        "Get member list is not supported",
        ReplaceWith("emptyItems()", "love.forte.simbot.utils.item.Items.Companion.emptyItems")
    )
    override val members: Items<TencentMember>
        get() = emptyItems()
    
    
    override val roles: Items<TencentRole>
    
    //// Impls
    
    @JvmSynthetic
    override suspend fun unmute(): Boolean = false
    
    @JvmSynthetic
    override suspend fun previous(): Organization? = null
}
