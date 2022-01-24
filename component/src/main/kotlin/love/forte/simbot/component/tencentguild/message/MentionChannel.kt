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