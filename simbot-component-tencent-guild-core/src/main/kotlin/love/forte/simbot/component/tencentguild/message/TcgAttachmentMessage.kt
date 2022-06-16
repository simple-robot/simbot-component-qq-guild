/*
 *  Copyright (c) 2022-2022 ForteScarlet <ForteScarlet@163.com>
 *
 *  本文件是 simbot-component-tencent-guild 的一部分。
 *
 *  simbot-component-tencent-guild 是自由软件：你可以再分发之和/或依照由自由软件基金会发布的 GNU 通用公共许可证修改之，无论是版本 3 许可证，还是（按你的决定）任何以后版都可以。
 *
 *  发布 simbot-component-tencent-guild 是希望它能有用，但是并无保障;甚至连可销售和符合某个特定的目的都不保证。请参看 GNU 通用公共许可证，了解详情。
 *
 *  你应该随程序获得一份 GNU 通用公共许可证的复本。如果没有，请看:
 *  https://www.gnu.org/licenses
 *  https://www.gnu.org/licenses/gpl-3.0-standalone.html
 *  https://www.gnu.org/licenses/lgpl-3.0-standalone.html
 *
 *
 */

package love.forte.simbot.component.tencentguild.message

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.ID
import love.forte.simbot.LoggerFactory
import love.forte.simbot.component.tencentguild.internal.SendingMessageParser
import love.forte.simbot.component.tencentguild.internal.TencentMessageForSendingBuilder
import love.forte.simbot.message.Message
import love.forte.simbot.message.Messages
import love.forte.simbot.message.doSafeCast
import love.forte.simbot.tencentguild.TencentMessage


/**
 * 附件的 [Message.Element] 实现，
 * 仅支持在 接收的消息中。
 *
 * @author ForteScarlet
 */
@SerialName("tcg.attachment")
@Serializable
public data class TcgAttachmentMessage(public val url: String) : TcgMessageElement<TcgAttachmentMessage> {
    
    @Deprecated("Just get url", ReplaceWith("url.ID", "love.forte.simbot.ID"))
    public val id: ID get() = url.ID
    
    override val key: Message.Key<TcgAttachmentMessage>
        get() = Key
    
    public companion object Key : Message.Key<TcgAttachmentMessage> {
        override fun safeCast(value: Any): TcgAttachmentMessage? = doSafeCast(value)
    }
}


public fun TencentMessage.Attachment.toMessage(): TcgAttachmentMessage = TcgAttachmentMessage(url)
public fun TcgAttachmentMessage.toAttachment(): TencentMessage.Attachment = TencentMessage.Attachment(url)


internal object AttachmentParser : SendingMessageParser {
    private val logger = LoggerFactory.getLogger(AttachmentParser::class)
    override fun invoke(
        index: Int,
        element: Message.Element<*>,
        messages: Messages?,
        builder: TencentMessageForSendingBuilder,
    ) {
        if (element is TcgAttachmentMessage) {
            logger.warn("Attachment message is not yet supported for sending")
        }
    }
    
}