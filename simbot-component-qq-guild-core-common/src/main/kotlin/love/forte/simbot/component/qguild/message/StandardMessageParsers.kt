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
import love.forte.simbot.component.qguild.QQGuildComponent
import love.forte.simbot.message.*
import love.forte.simbot.qguild.api.message.MessageSendApi
import love.forte.simbot.qguild.message.ContentTextDecoder
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
    private const val AT_USER_VALUE = "uv"
    private const val AT_EVERYONE_GROUP = "all"
    private const val MENTION_CHANNEL_VALUE = "cv"
    private const val EMOJI_VALUE = "ev"

    private val replaceRegex = Regex(
        "<@!?(?<$AT_USER_VALUE>\\d+)>" +
                "|(?<$AT_EVERYONE_GROUP>@everyone)" +
                "|<#(?<$MENTION_CHANNEL_VALUE>\\d+)>" +
                "|<emoji:(?<$EMOJI_VALUE>\\d+)>"
    )

    private val replaceWithoutMentionAllRegex = Regex(
        "<@!?(?<$AT_USER_VALUE>\\d+)>" +
                "|<#(?<$MENTION_CHANNEL_VALUE>\\d+)>" +
                "|<emoji:(?<$EMOJI_VALUE>\\d+)>"
    )


    override fun invoke(qgMessage: Message, context: ReceivingMessageParser.Context): ReceivingMessageParser.Context {
        val contentText = qgMessage.content
        val isMentionEveryone = qgMessage.mentionEveryone

        var match: MatchResult? =
            (if (isMentionEveryone) replaceRegex else replaceWithoutMentionAllRegex).find(contentText)

        if (match == null) {
            context.plainTextBuilder.append(ContentTextDecoder.decode(contentText))
            context.messages += QGContentText(contentText)
            return context
        }

        val messageList = mutableListOf<SimbotMessage.Element<*>>()

        // plainText builder
        val textBuilder = StringBuilder()

        fun appendText(value: String, startIndex: Int, endIndex: Int) {
            ContentTextDecoder.decodeTo(value, startIndex, endIndex, textBuilder)
        }

        fun flushText() {
            if (textBuilder.isNotEmpty()) {
                val text = textBuilder.toString()
                messageList.add(text.toText())
                context.plainTextBuilder.append(text)
                textBuilder.clear()
            }
        }

        var lastStart = 0
        val length = contentText.length
        do {
            val foundMatch = match!!

            appendText(contentText, lastStart, foundMatch.range.first)
            //region match transform
            kotlin.run {
                val groups = foundMatch.groups

                groups[AT_USER_VALUE]?.also { atUserValue ->
                    flushText()
                    messageList.add(At(atUserValue.value.ID, originContent = "<@${atUserValue.value}>"))
                    return@run
                }

                groups[MENTION_CHANNEL_VALUE]?.also { atChannelValue ->
                    flushText()
                    messageList.add(
                        At(
                            atChannelValue.value.ID,
                            type = QQGuildComponent.AT_CHANNEL_TYPE,
                            originContent = "<#${atChannelValue.value}>"
                        )
                    )
                    return@run
                }

                if (isMentionEveryone) {
                    groups[AT_EVERYONE_GROUP]?.also {
                        flushText()
                        messageList.add(AtAll)
                        return@run
                    }
                }

                groups[EMOJI_VALUE]?.also { emojiValue ->
                    flushText()
                    messageList.add(Face(emojiValue.value.ID))
                    return@run
                }

            }

            //endregion

            lastStart = foundMatch.range.last + 1
            match = foundMatch.next()
        } while (lastStart < length && match != null)

        if (lastStart < length) {
            appendText(contentText, lastStart, contentText.length)
        }

        // ark
        qgMessage.ark?.toMessage()?.also { messageList.add(it) }

        // attachments
        qgMessage.attachments.mapTo(messageList) { it.toMessage() }

        // TODO embeds?
        // TODO reference?

        context.messages += messageList
        return context
    }
}
