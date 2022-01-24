/*
 *  Copyright (c) 2022-2022 ForteScarlet <ForteScarlet@163.com>
 *
 *  根据 GNU LESSER GENERAL PUBLIC LICENSE 3 获得许可；
 *  除非遵守许可，否则您不得使用此文件。
 *  您可以在以下网址获取许可证副本：
 *
 *       https://www.gnu.org/licenses/lgpl-3.0-standalone.html
 *
 *   有关许可证下的权限和限制的具体语言，请参见许可证。
 */

package love.forte.simbot.component.tencentguild.message

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import love.forte.simbot.Component
import love.forte.simbot.ID
import love.forte.simbot.LoggerFactory
import love.forte.simbot.component.tencentguild.ComponentTencentGuild
import love.forte.simbot.component.tencentguild.internal.SendingMessageParser
import love.forte.simbot.component.tencentguild.internal.TencentMessageForSendingBuilder
import love.forte.simbot.message.Message
import love.forte.simbot.message.Messages
import love.forte.simbot.message.RemoteResource
import love.forte.simbot.tencentguild.TencentMessage
import kotlin.reflect.KClass


/**
 * 附件的 [Message.Element] 实现，
 * 仅支持在 接收的消息中。
 *
 * @author ForteScarlet
 */
@SerialName("tcg.attachment")
@Serializable
public data class AttachmentMessage(override val url: String) : RemoteResource<AttachmentMessage> {
    @Transient
    override val id: ID = url.ID

    override val key: Message.Key<AttachmentMessage>
        get() = Key

    public companion object Key : Message.Key<AttachmentMessage> {
        override val component: Component
            get() = ComponentTencentGuild.component
        override val elementType: KClass<AttachmentMessage>
            get() = AttachmentMessage::class
    }
}


public fun TencentMessage.Attachment.toMessage(): AttachmentMessage = AttachmentMessage(url)
public fun AttachmentMessage.toAttachment(): TencentMessage.Attachment = TencentMessage.Attachment(url)


internal object AttachmentParser : SendingMessageParser {
    private val logger = LoggerFactory.getLogger(AttachmentParser::class)
    override fun invoke(
        index: Int,
        element: Message.Element<*>,
        messages: Messages?,
        builder: TencentMessageForSendingBuilder
    ) {
        if (element is AttachmentMessage) {
            logger.warn("Attachment message is not yet supported for sending")
        }
    }

}