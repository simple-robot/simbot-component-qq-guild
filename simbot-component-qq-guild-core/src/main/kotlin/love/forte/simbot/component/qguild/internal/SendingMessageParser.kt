/*
 * Copyright (c) 2022-2023. ForteScarlet.
 *
 * This file is part of simbot-component-qq-guild.
 *
 * simbot-component-qq-guild is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * simbot-component-qq-guild is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with simbot-component-qq-guild. If not, see <https://www.gnu.org/licenses/>.
 */

package love.forte.simbot.component.qguild.internal

import love.forte.simbot.*
import love.forte.simbot.component.qguild.message.*
import love.forte.simbot.message.*
import love.forte.simbot.qguild.*
import love.forte.simbot.qguild.api.message.*
import love.forte.simbot.qguild.model.Message
import love.forte.simbot.resources.Resource
import java.util.*
import java.util.concurrent.*
import love.forte.simbot.message.Message as SimbotMessage


/**
 * 通过消息体和message builder, 以责任链的形式构建消息体。
 */
public fun interface SendingMessageParser :
    suspend (Int, SimbotMessage.Element<*>, Messages?, MessageSendApi.Body.Builder) -> Unit {
    /**
     * 将 [Message.Element] 拼接到 [TencentMessageForSendingBuilder] 中。
     *
     * @param index 当前消息链中的数据.
     */
    override suspend fun invoke(
        index: Int,
        element: SimbotMessage.Element<*>,
        messages: Messages?,
        builder: MessageSendApi.Body.Builder,
    )
}

/**
 * 将一个 [Message] 转化为 [Message].
 */
public fun interface ReceivingMessageParser : (Message, Messages) -> Messages {

    /**
     * 将一个 [Message] 接入到 [Messages] 中，并在最后作为 [ReceivedMessageContent] 输出至事件。
     * 对于第一个转化器来讲，[messages] 是一个 [EmptyMessages].
     *
     */
    override fun invoke(qgMessage: Message, messages: Messages): Messages
}


/**
 * 将一个 [Message.Element] 拼接到 [TencentMessageForSendingBuilder] 中，或者将一个 [Message] 转化为 [Message].
 */
public object MessageParsers {
    @ExperimentalSimbotApi
    public val sendingParsers: Queue<SendingMessageParser> = ConcurrentLinkedQueue<SendingMessageParser>().apply {
        add(ContentParser)
        add(MentionParser)
        add(ArkParser)
        add(AttachmentParser)
        add(ReplyToParser)
        add(ImageParser)
    }

    @ExperimentalSimbotApi
    public val receivingParsers: Queue<ReceivingMessageParser> = ConcurrentLinkedQueue<ReceivingMessageParser>().apply {
        add(QGMessageParser)
    }


    @ExperimentalSimbotApi
    public fun addParser(parser: SendingMessageParser) {
        sendingParsers.add(parser)
    }

    @ExperimentalSimbotApi
    public fun addParser(parser: ReceivingMessageParser) {
        receivingParsers.add(parser)
    }

    @OptIn(ExperimentalSimbotApi::class)
    @JvmOverloads
    public suspend fun parse(
        message: SimbotMessage,
        builderInitProcess: MessageSendApi.Body.Builder.() -> Unit = {},
        builderPostProcess: MessageSendApi.Body.Builder.() -> Unit = {},
    ): MessageSendApi.Body.Builder {
        val builder = MessageSendApi.Body.Builder().also(builderInitProcess)

        when (message) {
            is SimbotMessage.Element<*> -> {
                for (parser in sendingParsers) {
                    parser(0, message, null, builder)
                }
            }

            is Messages -> {
                message.forEachIndexed { index, element ->
                    for (parser in sendingParsers) {
                        parser(index, element, message, builder)
                    }
                }
            }
        }

        return builder.also(builderPostProcess)
    }


    @OptIn(ExperimentalSimbotApi::class)
    @JvmOverloads
    public fun parse(
        message: Message,
        messagesInit: Messages = emptyMessages(),
    ): Messages {
        return receivingParsers.fold(messagesInit) { messages, parser ->
            parser(message, messages)
        }
    }

}

public class QGMessageForSendingForParse internal constructor() {
    public var sendBodyBuilder: MessageSendApi.Body.Builder = MessageSendApi.Body.Builder()

    /**
     * 通过 form-data上传图片的 `file_image` 参数。
     * 目前支持使用的类型：
     * - [Resource]
     */
//    public var fileImage: Resource? = null


    @TmfsDsl
    public inline fun forSending(block: MessageSendApi.Body.Builder.() -> Unit) {
        sendBodyBuilder.block()
    }


    public fun contentAppend(contentText: String) {
        forSending {
            if (content == null) {
                content = contentText
            } else {
                content += contentText
            }
        }
    }

}

//internal operator fun TencentMessageForSendingForParse.component1(): MessageSendApi.Body.Builder = sendBodyBuilder
//internal operator fun TencentMessageForSendingForParse.component2(): Resource? = fileImage


@Retention(AnnotationRetention.BINARY)
@DslMarker
internal annotation class TmfsDsl // TencentMessageForSendingBuilderDsl

