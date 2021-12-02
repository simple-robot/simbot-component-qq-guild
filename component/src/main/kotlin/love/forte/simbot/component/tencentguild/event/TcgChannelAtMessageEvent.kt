package love.forte.simbot.component.tencentguild.event

import love.forte.simbot.Bot
import love.forte.simbot.Timestamp
import love.forte.simbot.definition.Channel
import love.forte.simbot.definition.Member
import love.forte.simbot.event.BaseEventKey
import love.forte.simbot.event.ChannelMessageEvent
import love.forte.simbot.event.Event
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
public interface TcgChannelAtMessageEvent : ChannelMessageEvent {
    public val sourceMessage: TencentMessage
    override val author: Member
    override val source: Channel
    override val timestamp: Timestamp
    override val visibleScope: Event.VisibleScope
    override val bot: Bot
    override val messageContent: ReceivedMessageContent
    override val metadata: Event.Metadata

    override suspend fun delete(): Boolean = false // not support, maybe.
    override val key: Event.Key<out Event>
        get() = Key

    public companion object Key : BaseEventKey<TcgChannelAtMessageEvent>("sr.tcg.at_msg") {
        override fun safeCast(value: Any): TcgChannelAtMessageEvent? = doSafeCast(value)
    }
}