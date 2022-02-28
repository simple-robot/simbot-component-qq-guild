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

package love.forte.simbot.component.tencentguild.internal

import love.forte.simbot.component.tencentguild.message.*
import love.forte.simbot.message.*
import love.forte.simbot.tencentguild.*


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
