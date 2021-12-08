package love.forte.simbot.component.tencentguild.internal

import love.forte.simbot.component.tencentguild.message.toMessage
import love.forte.simbot.message.*
import love.forte.simbot.tencentguild.TencentMessage


internal object ContentParser : SendingMessageParser {
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


internal object TencentMessageParser : ReceivingMessageParser {
    // private val mentionRegex = Regex("<@!(?<uid>\\d+)>|<#!(?<cid>\\d+)>")

    override fun invoke(tencentMessage: TencentMessage, messages: Messages): Messages {
        val messageList = mutableListOf<Message.Element<*>>()

        // at and text
        val content = tencentMessage.content

        // 与 plainText 属性不同，此处不对 content 做任何操作。
        messageList.add(Text { content })

        if (tencentMessage.mentionEveryone) {
            messageList.add(AtAll)
        }
        
        tencentMessage.mentions.takeIf { it.isNotEmpty() }?.also {
            messageList.addAll(it.map { u -> At(u.id) })
        }

        // if (content.isEmpty()) {
        //     messageList.add(Text())
        // } else {
        //     var lastTextIndex = 0
        //     mentionRegex.findAll(content).forEach { result ->
        //         if (result.range.first != lastTextIndex) {
        //             messageList.add(Text { content.substring(lastTextIndex until result.range.first) })
        //         }
        //         lastTextIndex = result.range.last + 1
        //         val groups = result.groups
        //         groups["uid"]?.apply {
        //             // uid, at user
        //             messageList.add(At(this.value.ID, originContent = result.value))
        //         } ?: groups["cid"]?.apply {
        //             messageList.add(At(this.value.ID, atType = "channel", originContent = result.value))
        //
        //         }
        //
        //     }
        // }

        tencentMessage.ark?.also {
            messageList.add(it.toMessage())
        }

        tencentMessage.attachments.takeIf { it.isNotEmpty() }
            ?.map(TencentMessage.Attachment::toMessage)
            ?.also {
                messageList.addAll(it)
            }


        return messages + messageList.toMessages()
    }
}
