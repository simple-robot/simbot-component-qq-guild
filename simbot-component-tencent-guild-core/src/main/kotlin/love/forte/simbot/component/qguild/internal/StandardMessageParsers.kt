/*
 * Copyright (c) 2022-2023. ForteScarlet.
 *
 * This file is part of simbot-component-tencent-guild.
 *
 * simbot-component-tencent-guild is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * simbot-component-tencent-guild is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with simbot-component-tencent-guild. If not, see <https://www.gnu.org/licenses/>.
 */

package love.forte.simbot.component.qguild.internal

import love.forte.simbot.ID
import love.forte.simbot.component.qguild.message.toMessage
import love.forte.simbot.message.*
import love.forte.simbot.qguild.api.message.MessageSendApi
import love.forte.simbot.qguild.model.Message
import love.forte.simbot.message.Message as SimbotMessage


internal object ContentParser : SendingMessageParser {
    override suspend fun invoke(
        index: Int,
        element: SimbotMessage.Element<*>,
        messages: Messages?,
        builder: MessageSendApi.Body.Builder
    ) {
        if (element is PlainText<*>) {
            // TODO 转义
            builder.appendContent(element.text)
        }
        // TODO ContentText
    }
}


internal object TencentMessageParser : ReceivingMessageParser {
    // private val mentionRegex = Regex("<@!(?<uid>\\d+)>|<#!(?<cid>\\d+)>")

    override fun invoke(qgMessage: Message, messages: Messages): Messages {
        val messageList = mutableListOf<SimbotMessage.Element<*>>()

        // at and text
        val content = qgMessage.content

        // 与 plainText 属性不同，此处不对 content 做任何操作。
        messageList.add(Text { content })

        if (qgMessage.mentionEveryone) {
            messageList.add(AtAll)
        }

        qgMessage.mentions.takeIf { it.isNotEmpty() }?.also {
            messageList.addAll(it.map { u -> At(u.id.ID) })
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

        qgMessage.ark?.also {
            messageList.add(it.toMessage())
        }

        qgMessage.attachments.takeIf { it.isNotEmpty() }
            ?.map(Message.Attachment::toMessage)
            ?.also {
                messageList.addAll(it)
            }


        return messages + messageList.toMessages()
    }
}
