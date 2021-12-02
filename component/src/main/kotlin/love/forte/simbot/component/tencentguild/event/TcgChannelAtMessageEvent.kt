package love.forte.simbot.component.tencentguild.event

import love.forte.simbot.Bot
import love.forte.simbot.Timestamp
import love.forte.simbot.action.MessageReplyReceipt
import love.forte.simbot.action.MessageReplySupport
import love.forte.simbot.component.tencentguild.TencentChannel
import love.forte.simbot.definition.Member
import love.forte.simbot.event.BaseEventKey
import love.forte.simbot.event.ChannelMessageEvent
import love.forte.simbot.event.Event
import love.forte.simbot.message.Message
import love.forte.simbot.message.ReceivedMessageContent
import love.forte.simbot.message.doSafeCast
import love.forte.simbot.tencentguild.TencentMessage

/**
 *
 * 频道（AT_MESSAGE）消息事件.
 *
 * @see
 *
 * @author ForteScarlet
 */
public abstract class TcgChannelAtMessageEvent : ChannelMessageEvent, MessageReplySupport {
    public abstract val sourceMessage: TencentMessage
    abstract override val author: Member
    abstract override val source: TencentChannel
    abstract override val timestamp: Timestamp
    abstract override val visibleScope: Event.VisibleScope
    abstract override val bot: Bot
    abstract override val messageContent: ReceivedMessageContent
    abstract override val metadata: Event.Metadata

    abstract override suspend fun reply(message: Message): MessageReplyReceipt

    override suspend fun delete(): Boolean = false // not support, maybe.
    override val key: Event.Key<out Event>
        get() = Key

    public companion object Key : BaseEventKey<TcgChannelAtMessageEvent>(
        "sr.tcg.at_msg",
        setOf(ChannelMessageEvent.Key)
    ) {
        override fun safeCast(value: Any): TcgChannelAtMessageEvent? = doSafeCast(value)
    }
}