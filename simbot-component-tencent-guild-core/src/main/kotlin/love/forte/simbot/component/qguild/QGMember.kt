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
import love.forte.simbot.component.qguild.internal.QGMessageReceipt
import love.forte.simbot.definition.GuildMember
import love.forte.simbot.definition.MemberInfo
import love.forte.simbot.message.Message
import love.forte.simbot.message.MessageContent
import love.forte.simbot.message.Text
import love.forte.simbot.utils.item.Items
import kotlin.time.Duration
import love.forte.simbot.qguild.model.Member as QGSourceMember

/**
 *
 * @author ForteScarlet
 */
public interface QGMember : GuildMember, MemberInfo, QGObjectiveContainer<QGSourceMember> {
    override val bot: QGBot
    override val id: ID

    override val avatar: String
    override val username: String
    
    override val roles: Items<QGRole>

    /**
     * 向目标成员发送私聊消息。
     *
     * ### 内嵌格式
     *
     * 如果想允许内嵌格式，请使用 [ContentText] 消息类型而不是纯文本或 [Text] TODO
     */
    @JvmBlocking
    @JvmAsync
    override suspend fun send(message: Message): QGMessageReceipt

    /**
     * 向目标成员发送纯文本的私聊消息。
     *
     * [text] 的内容会根据 [内嵌格式](https://bot.q.qq.com/wiki/develop/api/openapi/message/message_format.html)
     * 自动转义，发送出去的内容将会是原本的纯文本字符串，不会触发内嵌格式的特殊格式。
     *
     * 如果希望允许内嵌格式，请使用 [ContentText] 消息类型而不是纯文本或 [Text] TODO
     */
    @JvmBlocking
    @JvmAsync
    override suspend fun send(text: String): QGMessageReceipt


    /**
     * 向目标成员发送私聊消息。
     */
    @JvmBlocking
    @JvmAsync
    override suspend fun send(message: MessageContent): QGMessageReceipt
    
    @JvmBlocking(asProperty = true, suffix = "")
    @JvmAsync(asProperty = true)
    override suspend fun organization(): QGGuild = guild()
    
    @JvmBlocking(asProperty = true, suffix = "")
    @JvmAsync(asProperty = true)
    override suspend fun guild(): QGGuild

    /**
     * Deprecated: 子频道不支持禁言
     */
    @Deprecated("子频道不支持禁言", ReplaceWith("false"))
    @JvmSynthetic
    override suspend fun mute(duration: Duration): Boolean = false

    /**
     * Deprecated: 子频道不支持禁言
     */
    @Deprecated("子频道不支持禁言", ReplaceWith("false"))
    @JvmSynthetic
    override suspend fun unmute(): Boolean = false
}
