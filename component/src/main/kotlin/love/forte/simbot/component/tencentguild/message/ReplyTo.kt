package love.forte.simbot.component.tencentguild.message

import kotlinx.serialization.Serializable
import love.forte.simbot.Component
import love.forte.simbot.ID
import love.forte.simbot.component.tencentguild.TencentGuildComponent
import love.forte.simbot.component.tencentguild.internal.SendingMessageParser
import love.forte.simbot.component.tencentguild.internal.TencentMessageForSendingBuilder
import love.forte.simbot.message.Message
import love.forte.simbot.message.Messages
import kotlin.reflect.KClass

/**
 *
 * @author ForteScarlet
 */
@Serializable
public data class ReplyTo(@Serializable(ID.AsCharSequenceIDSerializer::class) val id: ID) : Message.Element<ReplyTo> {
    override val key: Message.Key<ReplyTo>
        get() = Key

    public companion object Key : Message.Key<ReplyTo> {
        override val component: Component
            get() = TencentGuildComponent.component

        override val elementType: KClass<ReplyTo>
            get() = ReplyTo::class

    }

}


internal object ReplyToParser : SendingMessageParser {
    override fun invoke(
        index: Int,
        element: Message.Element<*>,
        messages: Messages?,
        builder: TencentMessageForSendingBuilder
    ) {
        if (element is ReplyTo) {
            builder.msgId = element.id
        }
    }

}