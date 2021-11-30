package love.forte.simbot.component.tencentguild.message

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.Component
import love.forte.simbot.component.tencentguild.TencentGuildComponent
import love.forte.simbot.message.Message
import love.forte.simbot.tencentguild.TencentMessage
import kotlin.reflect.KClass

@SerialName("tc.ark")
@Serializable
public data class Ark(public val ark: TencentMessage.Ark) : Message.Element<Ark> {
    override val key: Message.Key<Ark>
        get() = Key


    public companion object Key : Message.Key<Ark> {
        override val component: Component
            get() = TencentGuildComponent.component
        override val elementType: KClass<Ark>
            get() = Ark::class
    }

}

