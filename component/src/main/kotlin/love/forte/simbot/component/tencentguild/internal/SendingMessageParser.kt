package love.forte.simbot.component.tencentguild.internal

import love.forte.simbot.Experimental
import love.forte.simbot.ID
import love.forte.simbot.component.tencentguild.message.ArkParser
import love.forte.simbot.component.tencentguild.message.AttachmentParser
import love.forte.simbot.component.tencentguild.message.MentionParser
import love.forte.simbot.component.tencentguild.message.ReplyToParser
import love.forte.simbot.message.*
import love.forte.simbot.tencentguild.ArkBuilder
import love.forte.simbot.tencentguild.EmbedBuilder
import love.forte.simbot.tencentguild.TencentMessage
import love.forte.simbot.tencentguild.api.message.TencentMessageForSending
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue


/**
 * 通过消息体和message builder, 以责任链的形式构建消息体。
 */
public fun interface SendingMessageParser :
        (Int, Message.Element<*>, Messages?, TencentMessageForSendingBuilder) -> Unit {
    /**
     * 将 [Message.Element] 拼接到 [TencentMessageForSendingBuilder] 中。
     *
     * @param index 当前消息链中的数据.
     */
    override fun invoke(
        index: Int,
        element: Message.Element<*>,
        messages: Messages?,
        builder: TencentMessageForSendingBuilder
    )
}

/**
 * 将一个 [TencentMessage] 转化为 [Message].
 */
public fun interface ReceivingMessageParser : (TencentMessage, Messages) -> Messages {

    /**
     * 将一个 [TencentMessage] 接入到 [Messages] 中，并在最后作为 [ReceivedMessageContent] 输出至事件。
     * 对于第一个转化器来讲，[messages] 是一个 [EmptyMessages].
     *
     */
    override fun invoke(tencentMessage: TencentMessage, messages: Messages): Messages
}


/**
 * 将一个 [Message.Element] 拼接到 [TencentMessageForSendingBuilder] 中，或者将一个 [TencentMessage] 转化为 [Message].
 */
public object MessageParsers {
    @Experimental
    public val sendingParsers: Queue<SendingMessageParser> = ConcurrentLinkedQueue<SendingMessageParser>().apply {
        add(ContentParser)
        add(MentionParser)
        add(ArkParser)
        add(AttachmentParser)
        add(ReplyToParser)
    }

    @Experimental
    public val receivingParsers: Queue<ReceivingMessageParser> = ConcurrentLinkedQueue<ReceivingMessageParser>().apply {
        add(TencentMessageParser)
    }


    @Experimental
    public fun addParser(parser: SendingMessageParser) {
        sendingParsers.add(parser)
    }

    @Experimental
    public fun addParser(parser: ReceivingMessageParser) {
        receivingParsers.add(parser)
    }

    @OptIn(Experimental::class)
    @JvmOverloads
    public fun parse(
        message: Message,
        builderInit: TencentMessageForSendingBuilder.() -> Unit = {}
    ): TencentMessageForSending {
        val builder = TencentMessageForSendingBuilder().also(builderInit)

        when (message) {
            is Message.Element<*> -> {
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
        return builder.build()
    }


    @OptIn(Experimental::class)
    @JvmOverloads
    public fun parse(
        message: TencentMessage,
        messagesInit: Messages = emptyMessages()
    ): Messages {
        return receivingParsers.fold(messagesInit) { messages, parser ->
            parser(message, messages)
        }
    }


}


@Retention(AnnotationRetention.BINARY)
@DslMarker
internal annotation class TmfsbDsl // TencentMessageForSendingBuilderDsl

@TmfsbDsl
public class TencentMessageForSendingBuilder {

    @TmfsbDsl
    public var msgId: ID? = null

    @TmfsbDsl
    public var content: String? = null

    public fun contentAppend(contentText: String) {
        if (content == null) {
            content = contentText
        } else {
            content += contentText
        }
    }

    @TmfsbDsl
    public var embed: EmbedBuilder? = null

    public fun embed(block: EmbedBuilder.() -> Unit) {
        embed = EmbedBuilder().also(block)
    }

    public fun embedAppend(block: EmbedBuilder.() -> Unit) {
        if (embed != null) {
            embed!!.also(block)
        }
    }

    @TmfsbDsl
    public var ark: ArkBuilder? = null

    public fun ark(templateId: ID, block: ArkBuilder.() -> Unit) {
        ark = ArkBuilder(templateId).also(block)
    }

    public fun arkAppendOrCreate(templateId: ID?, block: ArkBuilder.() -> Unit) {
        if (ark == null) {
            ark = ArkBuilder(templateId!!)
        } else {
            ark!!.also(block)
        }
    }

    public fun arkAppend(block: ArkBuilder.() -> Unit) {
        if (ark != null) {
            ark!!.also(block)
        }
    }

    @TmfsbDsl
    public var image: String? = null


    public fun build(): TencentMessageForSending {
        return TencentMessageForSending(
            content, embed?.build(), ark?.build(), image, msgId
        )
    }
}
