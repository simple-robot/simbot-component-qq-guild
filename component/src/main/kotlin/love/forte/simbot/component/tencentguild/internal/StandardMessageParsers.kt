package love.forte.simbot.component.tencentguild.internal

import love.forte.simbot.component.tencentguild.message.Ark
import love.forte.simbot.component.tencentguild.message.MentionChannel
import love.forte.simbot.message.At
import love.forte.simbot.message.Message
import love.forte.simbot.message.Messages
import love.forte.simbot.message.PlainText


internal object ContentParser : MessageParser {
    override fun invoke(
        index: Int,
        element: Message.Element<*>,
        messages: Messages?,
        builder: TencentMessageForSendingBuilder
    ) {
        if (element is PlainText<*>) {
            builder.contentAppend(element.text)
        }
    }
}

internal object MentionParser : MessageParser {
    override fun invoke(
        index: Int,
        element: Message.Element<*>,
        messages: Messages?,
        builder: TencentMessageForSendingBuilder
    ) {
        if (element is At) {
            builder.contentAppend("<@${element.target}>")
        }
        if (element is MentionChannel) {
            builder.contentAppend("<#${element.target}>")
        }
    }
}

internal object ArkParser : MessageParser {
    override fun invoke(
        index: Int,
        element: Message.Element<*>,
        messages: Messages?,
        builder: TencentMessageForSendingBuilder
    ) {
        if (element is Ark) {
            builder.arkAppend {
                from(element.ark)
            }
        }
    }
}

