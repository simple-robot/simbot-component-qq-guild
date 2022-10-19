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
import love.forte.simbot.component.tencentguild.internal.TencentMessageForSendingForParse
import love.forte.simbot.message.Message
import love.forte.simbot.message.Messages
import love.forte.simbot.message.doSafeCast

/**
 *
 * 腾讯频道机器人为公域时可能需要指定一个回复消息的目标，通过拼接 [TcgReplyTo] 到当前消息列表中来提供一个回复消息的目标信息。
 *
 * @author ForteScarlet
 */
@SerialName("tcg.replyTo")
@Serializable
public data class TcgReplyTo(@Serializable(ID.AsCharSequenceIDSerializer::class) val id: ID) : TcgMessageElement<TcgReplyTo> {
    override val key: Message.Key<TcgReplyTo>
        get() = Key

    public companion object Key : Message.Key<TcgReplyTo> {
        override fun safeCast(value: Any): TcgReplyTo? = doSafeCast(value)
    }

}


internal object ReplyToParser : SendingMessageParser {
    override suspend fun invoke(
        index: Int,
        element: Message.Element<*>,
        messages: Messages?,
        builder: TencentMessageForSendingForParse
    ) {
        if (element is TcgReplyTo) {
            builder.forSending.msgId = element.id
        }
    }

}