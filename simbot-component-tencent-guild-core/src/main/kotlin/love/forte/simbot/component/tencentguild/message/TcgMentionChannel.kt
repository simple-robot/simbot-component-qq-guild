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
import love.forte.simbot.message.At
import love.forte.simbot.message.Message
import love.forte.simbot.message.Messages
import love.forte.simbot.message.doSafeCast
import love.forte.simbot.tencentguild.api.message.MessageSendApi


@SerialName("tcg.mentionChannel") // tencentguild.channel.mention
@Serializable
public data class TcgMentionChannel(
    @Serializable(ID.AsCharSequenceIDSerializer::class)
    public val target: ID
) : TcgMessageElement<TcgMentionChannel> {
    override val key: Message.Key<TcgMentionChannel>
        get() = Key

    public companion object Key : Message.Key<TcgMentionChannel> {
        override fun safeCast(value: Any): TcgMentionChannel? = doSafeCast(value)
    }
}


internal object MentionParser : SendingMessageParser {
    override suspend fun invoke(
        index: Int,
        element: Message.Element<*>,
        messages: Messages?,
        builder: MessageSendApi.Body.Builder
    ) {
        if (element is At) {
            if (element.type == "channel") {
                builder.contentAppend("<#${element.target}>")
            } else {
                builder.contentAppend("<@${element.target}>")
            }
        }
        if (element is TcgMentionChannel) {
            builder.contentAppend("<#${element.target}>")
        }
    }
}
