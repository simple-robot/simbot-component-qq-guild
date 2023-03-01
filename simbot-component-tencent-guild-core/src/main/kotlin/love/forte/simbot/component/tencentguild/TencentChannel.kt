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
import love.forte.simbot.component.tencentguild.internal.TencentMessageReceipt
import love.forte.simbot.definition.Channel
import love.forte.simbot.message.Message
import love.forte.simbot.message.MessageContent
import love.forte.simbot.message.Text
import love.forte.simbot.tencentguild.TencentChannelInfo
import love.forte.simbot.utils.item.Items
import love.forte.simbot.utils.item.Items.Companion.emptyItems
import kotlin.time.Duration

/**
 * 子频道。来自于一个 [TencentGuild]。
 *
 * @see TencentGuild
 * @author ForteScarlet
 */
public interface TencentChannel : Channel, TencentGuildObjectiveContainer<TencentChannelInfo> {
    
    override val bot: TencentGuildComponentGuildBot
    override val createTime: Timestamp
    override val currentMember: Int
    override val description: String
    override val guildId: ID
    override val icon: String
    override val id: ID
    override val maximumMember: Int
    override val name: String
    override val ownerId: ID
    
    /**
     * 子频道始终有分组。
     */
    override val category: TencentChannelCategory
    
    @JvmBlocking(asProperty = true, suffix = "")
    @JvmAsync(asProperty = true)
    override suspend fun guild(): TencentGuild
    
    @JvmBlocking(asProperty = true, suffix = "")
    @JvmAsync(asProperty = true)
    override suspend fun owner(): TencentMember
    
    
    override val roles: Items<TencentRole>
    
    @JvmBlocking(asProperty = true, suffix = "")
    @JvmAsync(asProperty = true)
    override suspend fun previous(): TencentGuild = guild()
    
    @JvmBlocking
    @JvmAsync
    override suspend fun send(message: Message): TencentMessageReceipt
    
    @JvmBlocking
    @JvmAsync
    override suspend fun send(text: String): TencentMessageReceipt {
        return send(Text.of(text))
    }
    
    @JvmBlocking
    @JvmAsync
    override suspend fun send(message: MessageContent): TencentMessageReceipt {
        return send(message.messages)
    }
    
    /**
     * 目前无法直接获取成员列表。
     */
    @Deprecated(
        "Get member list is not supported",
        ReplaceWith("emptyItems()", "love.forte.simbot.utils.item.Items.Companion.emptyItems")
    )
    override val members: Items<TencentMember>
        get() = emptyItems() // previous.members // TODO
    
    
    @JvmBlocking(baseName = "getMember", suffix = "")
    @JvmAsync(baseName = "getMember")
    override suspend fun member(id: ID): TencentMember? {
        return previous().member(id)
    }
    
    
    @Deprecated("Mute API is not supported", ReplaceWith("false"))
    @JvmSynthetic
    override suspend fun mute(duration: Duration): Boolean = false
    
    @Deprecated("Mute API is not supported", ReplaceWith("false"))
    @JvmSynthetic
    override suspend fun unmute(): Boolean = false
}
