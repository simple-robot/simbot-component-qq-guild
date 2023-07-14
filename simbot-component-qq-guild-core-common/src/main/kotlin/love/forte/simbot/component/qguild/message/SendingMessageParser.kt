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

import love.forte.simbot.ExperimentalSimbotApi
import love.forte.simbot.message.Messages
import love.forte.simbot.message.emptyMessages
import love.forte.simbot.qguild.api.message.MessageSendApi
import love.forte.simbot.qguild.model.Message
import love.forte.simbot.resources.Resource
import java.util.*
import java.util.concurrent.ConcurrentLinkedDeque
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import love.forte.simbot.message.Message as SimbotMessage


/**
 * 通过消息体和message builder, 以责任链的形式构建消息体。
 */
public fun interface SendingMessageParser :
    suspend (Int, SimbotMessage.Element<*>, Messages?, SendingMessageParser.BuilderContext) -> Unit {
    /**
     * 将 [SimbotMessage.Element] 拼接到 [MessageSendApi.Body.Builder] 中。
     *
     * @param index 当前消息链中的数据.
     */
    override suspend fun invoke(
        index: Int,
        element: SimbotMessage.Element<*>,
        messages: Messages?,
        builderContext: BuilderContext,
    )

    public data class BuilderContext(
        val builderFactory: () -> MessageSendApi.Body.Builder
    ) {
        public val builders: Deque<MessageSendApi.Body.Builder>
        init {
            builders = ConcurrentLinkedDeque()
            builders.add(builderFactory())
        }
        val builder: MessageSendApi.Body.Builder get() = builders.peekLast()
        public fun newBuilder(): MessageSendApi.Body.Builder {
            return builderFactory().also { builders.addLast(it) }
        }

        /**
         * 如果符合 [check] 的条件，得到 [builder], 否则使用 [newBuilder] 构建一个新的builder。
         *
         */
        public inline fun builderOrNew(check: (MessageSendApi.Body.Builder) -> Boolean): MessageSendApi.Body.Builder {
            return builder.takeIf(check) ?: newBuilder()
        }

    }
}

/**
 * 将一个 [Message] 转化为 [Message].
 */
public fun interface ReceivingMessageParser {

    /**
     * 解析一个 [Message], 并将其内部信息并入 [Context] 中。
     */
    public operator fun invoke(qgMessage: Message, context: Context): Context

    /**
     * 消息链和正文文本内容的容器，用于 [ReceivingMessageParser.invoke] 中进行传递解析。
     *
     */
    public data class Context(public var messages: Messages, public var plainTextBuilder: StringBuilder)
}


/**
 * 将一个 [Message.Element][SimbotMessage.Element] 拼接到 [MessageSendApi.Body.Builder] 中。
 */
public object MessageParsers {
    @ExperimentalSimbotApi
    public val sendingParsers: Queue<SendingMessageParser> = ConcurrentLinkedQueue<SendingMessageParser>().apply {
        add(ContentParser)
        add(FaceParser)
        add(MentionParser)
        add(ArkParser)
        add(AttachmentParser)
        add(ReplyToParser)
        add(ImageParser)
        add(ReferenceParser)
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

    /**
     * 将 [message] 解析为一个或多个 [MessageSendApi.Body.Builder]。
     *
     * ## 消息拆分
     *
     * 当按照顺序解析且出现无法被叠加的消息元素时，
     * 例如：
     * - [ark][MessageSendApi.Body.ark]
     * - [messageReference][MessageSendApi.Body.messageReference]
     * - [embed][MessageSendApi.Body.embed]
     * - [image][MessageSendApi.Body.image] / [fileImage][[MessageSendApi.Body.fileImage]
     * - 使用模板的 [markdown][MessageSendApi.Body.markdown]
     *
     * 会先当前已经解析完毕的内容构建一个 [MessageSendApi.Body.Builder], 然后再构建一个新的继续解析，
     * 直到结果全部解析完毕。
     *
     * 部分可叠加的消息元素，例如：
     * - [content][MessageSendApi.Body.content]
     * - 使用 [Markdown.content][Message.Markdown.content] 的 [markdown][MessageSendApi.Body.markdown]
     *
     * 会尝试直接拼接/叠加而不是构建新的 builder。
     *
     * ## 注意避免
     *
     * 但是虽然说可以尝试着进行 Builder 的拆分，但是仍请尽可能确保使用的消息内容不会出现冲突。
     * 因为拆分后的消息体并不能保证可以进行发送，它们有可能会因为缺失部分属性而导致发送失败、进而导致预期外的行为或异常。
     *
     * 举个例子，当解析的消息链中存在 [QGReplyTo] 时，其会尝试将当前已经解析出来的所有结果的 [msgId][MessageSendApi.Body.msgId]
     * 进行覆盖，但是它因无法影响到后续**可能**会继续被拆分出来的新的消息体而存在隐患。
     *
     * @return 解析结果的 [MessageSendApi.Body.Builder] 序列。
     */
    @OptIn(ExperimentalSimbotApi::class, ExperimentalContracts::class)
    @JvmOverloads
    @JvmName("parse")
    public suspend inline fun parse(
        message: SimbotMessage,
        crossinline onEachPre: MessageSendApi.Body.Builder.() -> Unit = {},
        onEachPost: MessageSendApi.Body.Builder.() -> Unit = {},
    ): List<MessageSendApi.Body.Builder> {
        contract {
            callsInPlace(onEachPre, InvocationKind.AT_LEAST_ONCE)
            callsInPlace(onEachPost, InvocationKind.AT_LEAST_ONCE)
        }

//        val builder = MessageSendApi.Body.Builder().also(onEachPre)

        val context = SendingMessageParser.BuilderContext { MessageSendApi.Body.Builder().also(onEachPre) }

        when (message) {
            is SimbotMessage.Element<*> -> {
                for (parser in sendingParsers) {
                    parser(0, message, null, context)
                }
            }

            is Messages -> {
                message.forEachIndexed { index, element ->
                    for (parser in sendingParsers) {
                        parser(index, element, message, context)
                    }
                }
            }
        }

        val builders = context.builders
        if (builders.size <= 1) {
            return listOf(builders.first.also(onEachPost))
        }

        return builders.mapTo(ArrayList(builders.size)) { it.also(onEachPost) }
    }


    @OptIn(ExperimentalSimbotApi::class)
    @JvmOverloads
    public fun parse(
        message: Message,
        messagesInit: Messages = emptyMessages(),
    ): ReceivingMessageParser.Context {
        return receivingParsers.fold(
            ReceivingMessageParser.Context(
                messagesInit,
                StringBuilder(message.content.length)
            )
        ) { context, parser ->
            parser(message, context)
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

