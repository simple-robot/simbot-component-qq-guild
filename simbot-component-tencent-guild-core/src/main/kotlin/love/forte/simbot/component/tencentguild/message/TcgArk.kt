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
import love.forte.simbot.component.tencentguild.internal.SendingMessageParser
import love.forte.simbot.component.tencentguild.message.TcgArk.Key.byArk
import love.forte.simbot.literal
import love.forte.simbot.message.Messages
import love.forte.simbot.message.doSafeCast
import love.forte.simbot.tencentguild.api.message.MessageSendApi
import love.forte.simbot.tencentguild.buildArk
import love.forte.simbot.tencentguild.model.Message
import love.forte.simbot.message.Message as SimbotMessage

/**
 * [Message.Ark] 对应的 [SimbotMessage.Element].
 *
 * 需要注意在直接使用 [byArk] 构建实例的时候，属性拷贝为浅拷贝。
 */
@SerialName("tcg.ark")
@Serializable
public data class TcgArk internal constructor(
    @SerialName("template_id")
    @Serializable(ID.AsCharSequenceIDSerializer::class)
    public val templateId: ID,
    public val kvs: List<Message.Ark.Kv> = emptyList()
) : TcgMessageElement<TcgArk> {
    override val key: SimbotMessage.Key<TcgArk>
        get() = Key

    /**
     * 转化为一个真正的 [Message.Ark].
     */
    public fun toRealArk(): Message.Ark =
        Message.Ark(templateId.literal, kvs.toList())

    public companion object Key : SimbotMessage.Key<TcgArk> {
        override fun safeCast(value: Any): TcgArk? = doSafeCast(value)

        @JvmStatic
        public fun byArk(ark: Message.Ark): TcgArk =
            TcgArk(ark.templateId.ID, ark.kv.toList())

        @JvmStatic
        public fun create(
            templateId: ID,
            kv: List<Message.Ark.Kv> = emptyList()
        ): TcgArk =
            TcgArk(templateId, kv.toList())
    }
}


public fun Message.Ark.toMessage(): TcgArk = TcgArk(templateId.ID, kv)

public fun TcgArk.toArk(): Message.Ark = buildArk(templateId) {
    kvs = this@toArk.kvs.toMutableList()
}


internal object ArkParser : SendingMessageParser {
    override suspend fun invoke(
        index: Int,
        element: SimbotMessage.Element<*>,
        messages: Messages?,
        builder: MessageSendApi.Body.Builder
    ) {
        if (element is TcgArk) {
            val realArk = element.toRealArk()
            builder.ark = buildArk(realArk.templateId.ID) { from(realArk) }
        }
    }
}
