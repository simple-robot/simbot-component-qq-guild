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

import kotlinx.serialization.*
import love.forte.simbot.*
import love.forte.simbot.component.tencentguild.internal.*
import love.forte.simbot.message.*

/**
 *
 * 腾讯频道机器人为公域时可能需要指定一个回复消息的目标，通过拼接 [ReplyTo] 到当前消息列表中来提供一个回复消息的目标信息。
 *
 * @author ForteScarlet
 */
@SerialName("tcg.replyTo")
@Serializable
public data class ReplyTo(@Serializable(ID.AsCharSequenceIDSerializer::class) val id: ID) : Message.Element<ReplyTo> {
    override val key: Message.Key<ReplyTo>
        get() = Key

    public companion object Key : Message.Key<ReplyTo> {
        override fun safeCast(value: Any): ReplyTo? = doSafeCast(value)
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