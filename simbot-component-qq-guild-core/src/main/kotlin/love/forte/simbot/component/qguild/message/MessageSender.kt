/*
 * Copyright (c) 2023. ForteScarlet.
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

import love.forte.simbot.InternalSimbotApi
import love.forte.simbot.component.qguild.QGBot
import love.forte.simbot.component.qguild.internal.message.asReceipt
import love.forte.simbot.component.qguild.util.requestBy
import love.forte.simbot.message.Message
import love.forte.simbot.message.MessageContent
import love.forte.simbot.qguild.api.message.MessageSendApi
import love.forte.simbot.qguild.api.message.direct.DmsSendApi
import love.forte.simbot.qguild.message.ContentTextEncoder

/**
 * TODO
 *
 */
@InternalSimbotApi
public suspend inline fun QGBot.sendMessage(
    channelId: String,
    message: Message,
    crossinline onEachPre: MessageSendApi.Body.Builder.() -> Unit = {},
    onEachPost: MessageSendApi.Body.Builder.() -> Unit = {},
): QGMessageReceipt {
    val parsed = MessageParsers.parse(message = message, onEachPre = onEachPre, onEachPost = onEachPost)

    if (parsed.size == 1) {
        val body = parsed[0].build()
        return sendMessage(channelId, body)
    }

    return sendMessage(channelId, parsed)
}

/**
 * TODO
 *
 */
@InternalSimbotApi
public suspend inline fun QGBot.sendMessage(
    channelId: String,
    messageContent: MessageContent,
    crossinline onEachPre: MessageSendApi.Body.Builder.() -> Unit = {},
    onEachPost: MessageSendApi.Body.Builder.() -> Unit = {},
): QGMessageReceipt {
    if (messageContent is QGReceiveMessageContent) {
        val body = MessageSendApi.Body {
            onEachPre()
            fromMessage(messageContent.sourceMessage)
            onEachPost()
        }

        return sendMessage(channelId, body)
    }

    return sendMessage(channelId, messageContent.messages, onEachPre, onEachPost)
}

/**
 * TODO
 *
 */
@InternalSimbotApi
public suspend inline fun QGBot.sendMessage(
    channelId: String,
    text: String,
    onEachPre: MessageSendApi.Body.Builder.() -> Unit = {},
    onEachPost: MessageSendApi.Body.Builder.() -> Unit = {},
): QGMessageReceipt {
    val body = MessageSendApi.Body {
        onEachPre()
        // 转移后的纯文本字符串
        content = ContentTextEncoder.encode(text)
        onEachPost()
    }

    return sendMessage(channelId, body)
}


@PublishedApi
@InternalSimbotApi
internal suspend fun QGBot.sendMessage(channelId: String, body: MessageSendApi.Body): QGMessageReceipt =
    MessageSendApi.create(channelId, body).requestBy(this).asReceipt()

@PublishedApi
@InternalSimbotApi
internal suspend fun QGBot.sendMessage(
    channelId: String, bodyBuilderList: List<MessageSendApi.Body.Builder>
): QGMessageReceipt = bodyBuilderList.mapTo(ArrayList(bodyBuilderList.size)) {
    MessageSendApi.create(channelId, it.build()).requestBy(this)
}.asReceipt()


@PublishedApi
@InternalSimbotApi
internal suspend fun QGBot.sendDmsMessage(guildId: String, body: MessageSendApi.Body): QGMessageReceipt =
    DmsSendApi.create(guildId, body).requestBy(this).asReceipt()
