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

package love.forte.simbot.component.tencentguild.message

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.ID
import love.forte.simbot.component.tencentguild.internal.SendingMessageParser
import love.forte.simbot.literal
import love.forte.simbot.message.Message
import love.forte.simbot.message.Messages
import love.forte.simbot.message.doSafeCast
import love.forte.simbot.tencentguild.api.message.MessageSendApi

/**
 *
 * QQ频道机器人为公域时可能需要指定一个回复消息的目标，通过拼接 [TcgReplyTo] 到当前消息列表中来提供一个回复消息的目标信息。
 *
 * @author ForteScarlet
 */
@SerialName("tcg.replyTo")
@Serializable
public data class TcgReplyTo(@Serializable(ID.AsCharSequenceIDSerializer::class) val id: ID) :
    TcgMessageElement<TcgReplyTo> {
    override val key: Message.Key<TcgReplyTo>
        get() = Key

    public companion object Key : Message.Key<TcgReplyTo> {
        override fun safeCast(value: Any): TcgReplyTo? = doSafeCast(value)
    }

}


internal object ReplyToParser : SendingMessageParser {
    override suspend fun invoke(
        index: Int,
        element: Message.Element<*>,
        messages: Messages?,
        builder: MessageSendApi.Body.Builder
    ) {
        if (element is TcgReplyTo) {
            builder.msgId = element.id.literal
        }
    }

}
