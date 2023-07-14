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
import love.forte.simbot.component.qguild.message.QGAttachmentMessage.Key.toMessage
import love.forte.simbot.component.qguild.message.QGReference.Key.toMessage
import love.forte.simbot.literal
import love.forte.simbot.message.*
import love.forte.simbot.qguild.message.ContentTextDecoder
import love.forte.simbot.qguild.message.ContentTextEncoder
import love.forte.simbot.qguild.model.Message
import love.forte.simbot.message.Message as SimbotMessage


internal object ContentParser : SendingMessageParser {
    override suspend fun invoke(
        index: Int,
        element: SimbotMessage.Element<*>,
        messages: Messages?,
        builderContext: SendingMessageParser.BuilderContext
    ) {
        when (element) {
            is Text -> {
                // 转义为无内嵌格式的文本
                builderContext.builder.appendContent(ContentTextEncoder.encode(element.text))
            }

            is QGContentText -> {
                builderContext.builder.appendContent(element.content)
            }
        }
    }
}

/**
 * 解析 [Face] 为 [内嵌格式](https://bot.q.qq.com/wiki/develop/api/openapi/message/message_format.html) 中的 `表情` 使用。
 *
 * 解析为系统表情，具体表情id参考 [Emoji 列表](https://bot.q.qq.com/wiki/develop/api/openapi/emoji/model.html#emoji-%E5%88%97%E8%A1%A8)，
 * 仅支持type=1的系统表情，type=2的emoji表情直接按字符串填写即可
 *
 * @see love.forte.simbot.message.Face
 */
internal object FaceParser : SendingMessageParser {
    // <emoji:id>
    private const val EMOJI_PREFIX = "<emoji:"
    private const val EMOJI_SUFFIX = ">"

    override suspend fun invoke(
        index: Int,
        element: love.forte.simbot.message.Message.Element<*>,
        messages: Messages?,
        builderContext: SendingMessageParser.BuilderContext
    ) {
        if (element is Face) {
            val id = element.id.literal
            builderContext.builder.appendContent("$EMOJI_PREFIX$id$EMOJI_SUFFIX")
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
            ContentTextDecoder.decodeTo(value, startIndex, endIndex, object : Appendable {
                override fun append(csq: CharSequence?): Appendable = also {
                    textBuilder.append(csq)
                    context.plainTextBuilder.append(csq)
                }

                override fun append(csq: CharSequence?, start: Int, end: Int): Appendable = also {
                    textBuilder.append(csq, start, end)
                    context.plainTextBuilder.append(csq, start, end)
                }

                override fun append(c: Char): Appendable = also {
                    textBuilder.append(c)
                    context.plainTextBuilder.append(c)
                }
            })
        }

        fun flushText() {
            if (textBuilder.isNotEmpty()) {
                val text = textBuilder.toString()
                messageList.add(text.toText())
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

        // reference
        qgMessage.messageReference?.also { messageList.add(it.toMessage()) }

        context.messages += messageList
        return context
    }
}
