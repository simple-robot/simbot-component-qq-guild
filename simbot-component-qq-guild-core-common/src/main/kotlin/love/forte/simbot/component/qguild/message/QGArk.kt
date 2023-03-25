/*
 * Copyright (c) 2022-2023. ForteScarlet.
 *
 * This file is part of simbot-component-qq-guild.
 *
 * simbot-component-qq-guild is free software: you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * simbot-component-qq-guild is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with simbot-component-qq-guild.
 * If not, see <https://www.gnu.org/licenses/>.
 */

package love.forte.simbot.component.qguild.message

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.ID
import love.forte.simbot.component.qguild.message.QGArk.Key.byArk
import love.forte.simbot.literal
import love.forte.simbot.message.Messages
import love.forte.simbot.message.doSafeCast
import love.forte.simbot.qguild.api.message.MessageSendApi
import love.forte.simbot.qguild.message.buildArk
import love.forte.simbot.qguild.model.Message
import love.forte.simbot.message.Message as SimbotMessage

/**
 * [Message.Ark] 对应的 [SimbotMessage.Element].
 *
 * 需要注意在直接使用 [byArk] 构建实例的时候，属性拷贝为浅拷贝。
 */
@SerialName("qg.ark")
@Serializable
public data class QGArk internal constructor(
    @SerialName("template_id")
    @Serializable(ID.AsCharSequenceIDSerializer::class)
    public val templateId: ID,
    public val kvs: List<Message.Ark.Kv> = emptyList()
) : QGMessageElement<QGArk> {
    override val key: SimbotMessage.Key<QGArk>
        get() = Key

    /**
     * 转化为一个真正的 [Message.Ark].
     */
    public fun toRealArk(): Message.Ark =
        Message.Ark(templateId.literal, kvs.toList())

    public companion object Key : SimbotMessage.Key<QGArk> {
        override fun safeCast(value: Any): QGArk? = doSafeCast(value)

        @JvmStatic
        public fun byArk(ark: Message.Ark): QGArk =
            QGArk(ark.templateId.ID, ark.kv.toList())

        @JvmStatic
        public fun create(
            templateId: ID,
            kv: List<Message.Ark.Kv> = emptyList()
        ): QGArk =
            QGArk(templateId, kv.toList())
    }
}


public fun Message.Ark.toMessage(): QGArk = QGArk(templateId.ID, kv)

public fun QGArk.toArk(): Message.Ark = buildArk(templateId.literal) {
    kvs = this@toArk.kvs.toMutableList()
}


internal object ArkParser : SendingMessageParser {
    override suspend fun invoke(
        index: Int,
        element: SimbotMessage.Element<*>,
        messages: Messages?,
        builder: MessageSendApi.Body.Builder
    ) {
        if (element is QGArk) {
            val realArk = element.toRealArk()
            builder.ark = buildArk(realArk.templateId) { from(realArk) }
        }
    }
}
