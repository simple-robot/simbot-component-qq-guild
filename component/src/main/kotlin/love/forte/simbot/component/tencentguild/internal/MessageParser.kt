package love.forte.simbot.component.tencentguild.internal

import love.forte.simbot.Experimental
import love.forte.simbot.ID
import love.forte.simbot.message.Message
import love.forte.simbot.message.Messages
import love.forte.simbot.tencentguild.ArkBuilder
import love.forte.simbot.tencentguild.EmbedBuilder
import love.forte.simbot.tencentguild.api.message.TencentMessageForSending


/**
 * 通过消息体和message builder, 以责任链的形式构建消息体。
 */
public fun interface MessageParser {
    /**
     * 将 [Message.Element] 拼接到 [TencentMessageForSendingBuilder] 中。
     *
     * @param index 当前消息链中的数据.
     */
    public operator fun invoke(index: Int, element: Message.Element<*>, messages: Messages?, builder: TencentMessageForSendingBuilder)
}


/**
 * 将一个 [Message.Element] 拼接到 [TencentMessageForSendingBuilder] 中，或者将一个 [TencentMessage] 转化为 [Message].
 */
public object MessageParsers {
    @Experimental
    @Suppress("NO_EXPLICIT_RETURN_TYPE_IN_API_MODE_WARNING")
    public val parsers: MutableList<MessageParser> = mutableListOf(
        ContentParser,
        MentionParser,
        ArkParser,
    )


    @Experimental
    public fun addParser(parser: MessageParser) {
        parsers.add(parser)
    }

    @OptIn(Experimental::class)
    public fun parse(
        message: Message,
        builderInit: TencentMessageForSendingBuilder.() -> Unit = {}
    ): TencentMessageForSending {
        val builder = TencentMessageForSendingBuilder().also(builderInit)

        when (message) {
            is Message.Element<*> -> {
                for (parser in parsers) {
                    parser(0, message, null, builder)
                }
            }
            is Messages -> {
                message.forEachIndexed { index, element ->
                    for (parser in parsers) {
                        parser(index, element, message, builder)
                    }
                }
            }
        }
        return builder.build()
    }


}


@Retention(AnnotationRetention.BINARY)
@DslMarker
internal annotation class TmfsbDsl // TencentMessageForSendingBuilderDsl

@TmfsbDsl
public class TencentMessageForSendingBuilder {

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
            content, embed?.build(), ark?.build(), image
        )
    }
}
