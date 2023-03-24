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

import love.forte.simbot.ID
import love.forte.simbot.message.At
import love.forte.simbot.message.AtAll
import love.forte.simbot.message.Messages
import love.forte.simbot.message.Text
import love.forte.simbot.qguild.api.message.MessageSendApi
import love.forte.simbot.qguild.message.ContentTextEncoder
import love.forte.simbot.qguild.model.Message
import love.forte.simbot.message.Message as SimbotMessage


internal object ContentParser : SendingMessageParser {
    override suspend fun invoke(
        index: Int,
        element: SimbotMessage.Element<*>,
        messages: Messages?,
        builder: MessageSendApi.Body.Builder
    ) {
        when (element) {
            is Text -> {
                // 转义为无内嵌格式的文本
                builder.appendContent(ContentTextEncoder.encode(element.text))
            }

            is QGContentText -> {
                builder.appendContent(element.content)
            }
        }
    }
}



internal object QGMessageParser : ReceivingMessageParser {


    override fun invoke(qgMessage: Message, context: ReceivingMessageParser.Context): ReceivingMessageParser.Context {
        val messageList = mutableListOf<SimbotMessage.Element<*>>()

        // TODO 解析content，拆解并拼接各元素

        // at and text
        val content = qgMessage.content

        messageList.add(QGContentText(content))

        if (qgMessage.mentionEveryone) {
            messageList.add(AtAll)
        }

        qgMessage.mentions.takeIf { it.isNotEmpty() }?.also {
            messageList.addAll(it.map { u -> At(u.id.ID) })
        }

        qgMessage.ark?.also {
            messageList.add(it.toMessage())
        }

        qgMessage.attachments.takeIf { it.isNotEmpty() }
            ?.map(Message.Attachment::toMessage)
            ?.also {
                messageList.addAll(it)
            }

        // TODO embeds
        // TODO reference
        // TODO mention channels?

        context.messages += messageList
        return context
    }
}
