/*
 * Copyright (c) 2022-2023. ForteScarlet.
 *
 * This file is part of simbot-component-qq-guild.
 *
 * simbot-component-qq-guild is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * simbot-component-qq-guild is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with simbot-component-qq-guild. If not, see <https://www.gnu.org/licenses/>.
 */

package love.forte.simbot.component.qguild.message

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.ID
import love.forte.simbot.literal
import love.forte.simbot.message.Message
import love.forte.simbot.message.Messages
import love.forte.simbot.message.doSafeCast
import love.forte.simbot.qguild.api.message.MessageSendApi

/**
 *
 * QQ频道机器人为公域时可能需要指定一个回复消息的目标，通过拼接 [QGReplyTo] 到当前消息列表中来提供一个回复消息的目标信息。
 *
 * @author ForteScarlet
 */
@SerialName("qg.replyTo")
@Serializable
public data class QGReplyTo(@Serializable(ID.AsCharSequenceIDSerializer::class) val id: ID) :
    QGMessageElement<QGReplyTo> {
    override val key: Message.Key<QGReplyTo>
        get() = Key

    public companion object Key : Message.Key<QGReplyTo> {
        override fun safeCast(value: Any): QGReplyTo? = doSafeCast(value)
    }

}


internal object ReplyToParser : SendingMessageParser {
    override suspend fun invoke(
        index: Int,
        element: Message.Element<*>,
        messages: Messages?,
        builder: MessageSendApi.Body.Builder
    ) {
        if (element is QGReplyTo) {
            builder.msgId = element.id.literal
        }
    }

}
