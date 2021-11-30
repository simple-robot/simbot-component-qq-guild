package love.forte.simbot.component.tencentguild.message

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.Component
import love.forte.simbot.ID
import love.forte.simbot.component.tencentguild.TencentGuildComponent
import love.forte.simbot.message.Message
import kotlin.reflect.KClass


@SerialName("tcg.cl.mt") // tencentguild.channel.mention
@Serializable
public data class MentionChannel(
    @Serializable(ID.AsCharSequenceIDSerializer::class)
    public val target: ID
) : Message.Element<MentionChannel> {
    override val key: Message.Key<MentionChannel>
        get() = Key

    public companion object Key : Message.Key<MentionChannel> {
        override val component: Component
            get() = TencentGuildComponent.component
        override val elementType: KClass<MentionChannel>
            get() = MentionChannel::class
    }
}

