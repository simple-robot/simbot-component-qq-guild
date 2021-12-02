package love.forte.simbot.component.tencentguild.internal.event

import love.forte.simbot.Timestamp
import love.forte.simbot.component.tencentguild.event.TcgChannelAtMessageEvent
import love.forte.simbot.component.tencentguild.internal.TencentGuildBotImpl
import love.forte.simbot.definition.Channel
import love.forte.simbot.definition.Member
import love.forte.simbot.event.Event
import love.forte.simbot.message.ReceivedMessageContent
import love.forte.simbot.tencentguild.EventSignals
import love.forte.simbot.tencentguild.TencentMessage

/**
 *
 * @author ForteScarlet
 */
internal class TcgChannelAtMessageEventImpl(
    private val sourceSignals: EventSignals.AtMessages.AtMessageCreate,
    override val bot: TencentGuildBotImpl
) : TcgChannelAtMessageEvent {

    override val sourceMessage: TencentMessage
        get() = TODO("Not yet implemented")

    override val author: Member
        get() = TODO("Not yet implemented")

    override val source: Channel
        get() = TODO("Not yet implemented")

    override val timestamp: Timestamp
        get() = TODO("Not yet implemented")

    override val visibleScope: Event.VisibleScope
        get() = TODO("Not yet implemented")

    override val messageContent: ReceivedMessageContent
        get() = TODO("Not yet implemented")

    override val metadata: Event.Metadata
        get() = TODO("Not yet implemented")
}