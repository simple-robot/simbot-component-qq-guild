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

package love.forte.simbot.component.qguild

import love.forte.plugin.suspendtrans.annotation.JvmAsync
import love.forte.plugin.suspendtrans.annotation.JvmBlocking
import love.forte.simbot.ID
import love.forte.simbot.Timestamp
import love.forte.simbot.component.qguild.internal.QGMessageReceipt
import love.forte.simbot.definition.Channel
import love.forte.simbot.message.Message
import love.forte.simbot.message.MessageContent
import love.forte.simbot.message.Text
import love.forte.simbot.utils.item.Items
import love.forte.simbot.utils.item.Items.Companion.emptyItems
import kotlin.time.Duration
import love.forte.simbot.qguild.model.Channel as QGuildChannel

/**
 * 子频道。来自于一个 [QGGuild]。
 *
 * @see QGGuild
 * @author ForteScarlet
 */
public interface QGChannel : Channel, QGObjectiveContainer<QGuildChannel> {
    override val source: love.forte.simbot.qguild.model.Channel
    override val bot: QGGuildBot
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
     * 子频道分组**的ID**。
     *
     * 子频道分组ID实例 [QGChannelCategoryId] 是一个仅包含 `id`
     * 信息的未初始化实例，其 [id][QGChannelCategoryId.id] 和 [name][QGChannelCategoryId.name]
     * 的值都是 [source.parentId][love.forte.simbot.qguild.model.Channel.parentId] 的值，即分组ID的字符串值。
     *
     * 如果希望获取完整信息，使用 [QGChannelCategoryId.category].
     *
     * @see QGChannelCategoryId
     */
    override val category: QGChannelCategoryId

    /**
     * 得到当前子频道所属频道服务器
     */
    @JvmBlocking(asProperty = true, suffix = "")
    @JvmAsync(asProperty = true)
    override suspend fun guild(): QGGuild

    /**
     * 得到当前子频道所属用户
     */
    @JvmBlocking(asProperty = true, suffix = "")
    @JvmAsync(asProperty = true)
    override suspend fun owner(): QGMember
    
    
    override val roles: Items<QGRole>
    
    @JvmBlocking(asProperty = true, suffix = "")
    @JvmAsync(asProperty = true)
    override suspend fun previous(): QGGuild = guild()
    
    @JvmBlocking
    @JvmAsync
    override suspend fun send(message: Message): QGMessageReceipt
    
    @JvmBlocking
    @JvmAsync
    override suspend fun send(text: String): QGMessageReceipt {
        return send(Text.of(text))
    }
    
    @JvmBlocking
    @JvmAsync
    override suspend fun send(message: MessageContent): QGMessageReceipt {
        return send(message.messages)
    }
    
    /**
     * 目前无法直接获取成员列表。
     */
    @Deprecated(
        "Get member list is not supported",
        ReplaceWith("emptyItems()", "love.forte.simbot.utils.item.Items.Companion.emptyItems")
    )
    override val members: Items<QGMember>
        get() = emptyItems() // previous.members // TODO
    
    
    @JvmBlocking(baseName = "getMember", suffix = "")
    @JvmAsync(baseName = "getMember")
    override suspend fun member(id: ID): QGMember? {
        return previous().member(id)
    }
    
    
    @Deprecated("Mute API is not supported", ReplaceWith("false"))
    @JvmSynthetic
    override suspend fun mute(duration: Duration): Boolean = false
    
    @Deprecated("Mute API is not supported", ReplaceWith("false"))
    @JvmSynthetic
    override suspend fun unmute(): Boolean = false
}
