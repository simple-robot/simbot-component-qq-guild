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
import love.forte.simbot.Component
import love.forte.simbot.ID
import love.forte.simbot.component.tencentguild.ComponentTencentGuild
import love.forte.simbot.component.tencentguild.internal.SendingMessageParser
import love.forte.simbot.component.tencentguild.internal.TencentMessageForSendingBuilder
import love.forte.simbot.component.tencentguild.message.Ark.Key.byArk
import love.forte.simbot.message.Message
import love.forte.simbot.message.Messages
import love.forte.simbot.tencentguild.TencentMessage
import love.forte.simbot.tencentguild.buildArk
import kotlin.reflect.KClass

/**
 * [TencentMessage.Ark] 对应的 [Message.Element].
 *
 * 需要注意在直接使用 [byArk] 构建实例的时候，属性拷贝为浅拷贝。
 */
@SerialName("tcg.ark")
@Serializable
public data class Ark internal constructor(
    @SerialName("template_id")
    @Serializable(ID.AsCharSequenceIDSerializer::class)
    public val templateId: ID,
    public val kvs: List<TencentMessage.Ark.Kv> = emptyList()
) : Message.Element<Ark> {
    override val key: Message.Key<Ark>
        get() = Key

    /**
     * 转化为一个真正的 [TencentMessage.Ark].
     */
    public fun toRealArk(): TencentMessage.Ark = TencentMessage.Ark(templateId, kvs.toList())

    public companion object Key : Message.Key<Ark> {
        override val component: Component
            get() = ComponentTencentGuild.component
        override val elementType: KClass<Ark>
            get() = Ark::class

        @JvmStatic
        public fun byArk(ark: TencentMessage.Ark) : Ark = Ark(ark.templateId, ark.kv.toList())

        @JvmStatic
        public fun create(templateId: ID, kv: List<TencentMessage.Ark.Kv> = emptyList()) : Ark = Ark(templateId, kv.toList())
    }

}


public fun TencentMessage.Ark.toMessage(): Ark = Ark(templateId, kv)

public fun Ark.toArk(): TencentMessage.Ark = buildArk(templateId) {
    kvs = this@toArk.kvs.toMutableList()
}



internal object ArkParser : SendingMessageParser {
    override fun invoke(
        index: Int,
        element: Message.Element<*>,
        messages: Messages?,
        builder: TencentMessageForSendingBuilder
    ) {
        if (element is Ark) {
            builder.arkAppend {
                from(element.toRealArk())
            }
        }
    }
}