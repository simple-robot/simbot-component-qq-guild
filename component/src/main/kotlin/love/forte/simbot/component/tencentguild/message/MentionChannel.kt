package love.forte.simbot.component.tencentguild.message

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.Component
import love.forte.simbot.ID
import love.forte.simbot.component.tencentguild.ComponentTencentGuild
import love.forte.simbot.component.tencentguild.internal.SendingMessageParser
import love.forte.simbot.component.tencentguild.internal.TencentMessageForSendingBuilder
import love.forte.simbot.message.At
import love.forte.simbot.message.Message
import love.forte.simbot.message.Messages
import kotlin.reflect.KClass


@SerialName("tcg.mentionChannel") // tencentguild.channel.mention
@Serializable
public data class MentionChannel(
    @Serializable(ID.AsCharSequenceIDSerializer::class)
    public val target: ID
) : Message.Element<MentionChannel> {
    override val key: Message.Key<MentionChannel>
        get() = Key

    public companion object Key : Message.Key<MentionChannel> {
        override val component: Component
            get() = ComponentTencentGuild.component
        override val elementType: KClass<MentionChannel>
            get() = MentionChannel::class
    }
}


internal object MentionParser : SendingMessageParser {
    override fun invoke(
        index: Int,
        element: Message.Element<*>,
        messages: Messages?,
        builder: TencentMessageForSendingBuilder
    ) {
        if (element is At) {
            if (element.atType == "channel") {
                builder.contentAppend("<#${element.target}>")
            } else {
                builder.contentAppend("<@${element.target}>")
            }
        }
        if (element is MentionChannel) {
            builder.contentAppend("<#${element.target}>")
        }
    }
}