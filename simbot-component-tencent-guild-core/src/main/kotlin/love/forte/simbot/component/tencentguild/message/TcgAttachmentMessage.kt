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
import love.forte.simbot.logger.LoggerFactory
import love.forte.simbot.message.Messages
import love.forte.simbot.message.doSafeCast
import love.forte.simbot.tencentguild.api.message.MessageSendApi
import love.forte.simbot.tencentguild.model.Message
import love.forte.simbot.message.Message as SimbotMessage


/**
 * 附件的 [SimbotMessage.Element] 实现，
 * 仅支持在 接收的消息中。
 *
 * @author ForteScarlet
 */
@SerialName("tcg.attachment")
@Serializable
public data class TcgAttachmentMessage(public val url: String) : TcgMessageElement<TcgAttachmentMessage> {

    @Deprecated("Just get url", ReplaceWith("url.ID", "love.forte.simbot.ID"))
    public val id: ID get() = url.ID

    override val key: SimbotMessage.Key<TcgAttachmentMessage>
        get() = Key

    public companion object Key : SimbotMessage.Key<TcgAttachmentMessage> {
        override fun safeCast(value: Any): TcgAttachmentMessage? = doSafeCast(value)
    }
}


public fun Message.Attachment.toMessage(): TcgAttachmentMessage = TcgAttachmentMessage(url)
public fun TcgAttachmentMessage.toAttachment(): Message.Attachment = Message.Attachment(url)


internal object AttachmentParser : SendingMessageParser {
    private val logger = LoggerFactory.getLogger(AttachmentParser::class)
    override suspend fun invoke(
        index: Int,
        element: SimbotMessage.Element<*>,
        messages: Messages?,
        builder: MessageSendApi.Body.Builder,
    ) {
        if (element is TcgAttachmentMessage) {
            logger.warn("Attachment message is not yet supported for sending")
        }
    }

}
