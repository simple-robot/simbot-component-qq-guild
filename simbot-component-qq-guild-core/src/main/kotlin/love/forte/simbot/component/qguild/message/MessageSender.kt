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
import love.forte.simbot.qguild.QQGuildApiException
import love.forte.simbot.qguild.api.message.MessageSendApi
import love.forte.simbot.qguild.api.message.direct.DmsSendApi
import love.forte.simbot.qguild.message.ContentTextEncoder

/**
 * 使用当前 [QGBot] 向 [channelId] 通过 [MessageSendApi] 发送一个消息，
 * 消息内容为 [message] 的解析结果。
 *
 * [message] 会通过 [MessageParsers.parse] 解析为一个或多个 [MessageSendApi.Body.Builder]。
 * 当解析结果数量为1时，[sendMessage] 的结果为 [QGSingleMessageReceipt] 类型的回执。
 * 相反如果结果大于1，则 [sendMessage] 会得到实际为 [QGAggregatedMessageReceipt] 类型的结果。
 *
 * 需要注意如果 [message] 的解析结果最终没有任何可用元素（例如元素为空）则可能会引发API请求异常。
 *
 * @param channelId 目标频道ID
 * @param message 解析消息
 * @param onEachPre 对于每个 [MessageSendApi.Body.Builder] 被构造后的初始化行为
 * @param onEachPost 对于每个 [MessageSendApi.Body.Builder] 最终执行的收尾行为
 *
 * @see MessageParsers.parse
 *
 * @throws QQGuildApiException 请求得到了异常的结果
 *
 * @return 发送后的回执
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
 * 使用当前 [QGBot] 向 [channelId] 通过 [MessageSendApi] 发送一个消息，
 * 消息内容为 [messageContent] 的解析结果。
 *
 * 如果 [messageContent] 是其他QQ频道事件收到的消息（即为 [QGReceiveMessageContent] 类型），
 * 则会直接通过 [MessageSendApi.Body.Builder.fromMessage] 转义内部信息而不会尝试解析 [MessageContent.messages]。
 *
 * **注意:** 在转化的过程中会丢失不支持发送的消息（例如附件、图片等）。
 *
 * 如果类型不是上述情况，则行为与使用 [MessageContent.messages] 一致。
 *
 * @param channelId 目标频道ID
 * @param messageContent 解析消息本体
 * @param onEachPre 对于每个 [MessageSendApi.Body.Builder] 被构造后的初始化行为
 * @param onEachPost 对于每个 [MessageSendApi.Body.Builder] 最终执行的收尾行为
 *
 * @see MessageParsers.parse
 *
 * @throws QQGuildApiException 请求得到了异常的结果
 *
 * @return 发送后的回执
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
 * 使用当前 [QGBot] 向 [channelId] 通过 [MessageSendApi] 发送一个消息，
 * 消息内容为通过 [ContentTextEncoder.encode] 转义后的无特殊含义 [text] 文本。
 *
 * [text] 会通过 [ContentTextEncoder.encode] 进行转义来消除其中可能存在的[内嵌格式](https://bot.q.qq.com/wiki/develop/api/openapi/message/message_format.html)文本。
 * 如果你希望保留内嵌格式，参考使用 [QGContentText] 而不是纯文本内容。
 *
 *
 * @param channelId 目标频道ID
 * @param text 纯文本消息
 * @param onEachPre 对于每个 [MessageSendApi.Body.Builder] 被构造后的初始化行为
 * @param onEachPost 对于每个 [MessageSendApi.Body.Builder] 最终执行的收尾行为
 *
 * @see MessageParsers.parse
 * @see QGContentText
 *
 * @throws QQGuildApiException 请求得到了异常的结果
 *
 * @return 发送后的回执
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
