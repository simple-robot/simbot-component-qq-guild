package love.forte.simbot.component.tencentguild.message

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import love.forte.simbot.Component
import love.forte.simbot.ID
import love.forte.simbot.component.tencentguild.TencentGuildComponent
import love.forte.simbot.message.Message
import love.forte.simbot.message.RemoteResource
import love.forte.simbot.tencentguild.TencentMessage
import kotlin.reflect.KClass

public fun TencentMessage.Attachment.toMessage(): AttachmentMessage = AttachmentMessage(url)

/**
 *
 * @author ForteScarlet
 */
@Serializable
public data class AttachmentMessage(override val url: String) : RemoteResource<AttachmentMessage> {
    @Transient
    override val id: ID = url.ID

    override val key: Message.Key<AttachmentMessage>
        get() = Key

    public companion object Key : Message.Key<AttachmentMessage> {
        override val component: Component
            get() = TencentGuildComponent.component
        override val elementType: KClass<AttachmentMessage>
            get() = AttachmentMessage::class
    }
}