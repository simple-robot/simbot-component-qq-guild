/*
 *  Copyright (c) 2021 ForteScarlet <https://github.com/ForteScarlet>
 *
 *  根据 Apache License 2.0 获得许可；
 *  除非遵守许可，否则您不得使用此文件。
 *  您可以在以下网址获取许可证副本：
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *   有关许可证下的权限和限制的具体语言，请参见许可证。
 */

package love.forte.simbot.component.tencentguild

import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import love.forte.simbot.*
import love.forte.simbot.component.tencentguild.message.Ark
import love.forte.simbot.component.tencentguild.message.AttachmentMessage
import love.forte.simbot.component.tencentguild.message.MentionChannel
import love.forte.simbot.component.tencentguild.message.ReplyTo
import love.forte.simbot.message.Message

/**
 *
 * https://bot.q.qq.com/wiki/develop/api/openapi/guild/model.html
 */
public object ComponentTencentGuild {
    @JvmField public val COMPONENT_ID: CharSequenceID = "simbot.tencentguild".ID
    @Suppress("ObjectPropertyName")
    internal lateinit var _component: Component
    public val component: Component get() = if (::_component.isInitialized) _component else Components[COMPONENT_ID]
}


public class TencentGuildComponentInformationRegistrar : ComponentInformationRegistrar {
    override fun informations(): ComponentInformationRegistrar.Result {
        return ComponentInformationRegistrar.Result.ok(listOf(TencentGuildComponentInformation()))
    }

}


public class TencentGuildComponentInformation : ComponentInformation {
    override val id: ID
        get() = ComponentTencentGuild.COMPONENT_ID
    override val name: String
        get() = ComponentTencentGuild.COMPONENT_ID.toString()

    override val messageSerializersModule: SerializersModule = SerializersModule {
        polymorphic(Message.Element::class) {
            subclass(Ark::class, Ark.serializer())
            subclass(MentionChannel::class, MentionChannel.serializer())
            subclass(AttachmentMessage::class, AttachmentMessage.serializer())
            subclass(ReplyTo::class, ReplyTo.serializer())
        }
    }

    override fun configAttributes(attributes: MutableAttributeMap) {
        // nothing now.
    }

    override fun setComponent(component: Component) {
        ComponentTencentGuild._component = component
    }

}



