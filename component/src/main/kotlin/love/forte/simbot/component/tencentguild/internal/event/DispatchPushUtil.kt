package love.forte.simbot.component.tencentguild.internal.event

import kotlinx.serialization.json.Json
import love.forte.simbot.component.tencentguild.event.TcgChannelAtMessageEvent
import love.forte.simbot.component.tencentguild.internal.TencentGuildBotImpl
import love.forte.simbot.event.Event
import love.forte.simbot.tencentguild.EventSignals
import love.forte.simbot.tencentguild.Signal


internal interface SignalToEvent {
    val key: Event.Key<*>
    operator fun invoke(bot: TencentGuildBotImpl, decoder: Json, dispatch: Signal.Dispatch): Event
}

internal val eventSignalParsers =
    mapOf<EventSignals<*>, SignalToEvent>(
        // EventSignals.Guilds.GuildCreate to TODO(),
        // EventSignals.Guilds.GuildUpdate to TODO(),
        // EventSignals.Guilds.GuildDelete to TODO(),
        //
        // EventSignals.Guilds.ChannelCreate to TODO(),
        // EventSignals.Guilds.ChannelUpdate to TODO(),
        // EventSignals.Guilds.ChannelDelete to TODO(),

        // EventSignals.GuildMembers.GuildMemberAdd to TODO(),
        // EventSignals.GuildMembers.GuildMemberUpdate to TODO(),
        // EventSignals.GuildMembers.GuildMemberRemove to TODO(),

        // EventSignals.DirectMessage.DirectMessageCreate to TODO(),

        // EventSignals.AudioAction.AudioStart to TODO(),
        // EventSignals.AudioAction.AudioFinish to TODO(),
        // EventSignals.AudioAction.AudioOnMic to TODO(),
        // EventSignals.AudioAction.AudioOffMic to TODO(),

        EventSignals.AtMessages.AtMessageCreate to AtMessageEventParser,
    )


private object AtMessageEventParser : SignalToEvent {
    override val key: Event.Key<*> = TcgChannelAtMessageEvent.Key

    override fun invoke(
        bot: TencentGuildBotImpl,
        decoder: Json,
        dispatch: Signal.Dispatch
    ): Event {
        val type = EventSignals.AtMessages.AtMessageCreate
        val message = decoder.decodeFromJsonElement(type.decoder, dispatch.data)
        return TcgChannelAtMessageEventImpl(message, bot)
    }
}