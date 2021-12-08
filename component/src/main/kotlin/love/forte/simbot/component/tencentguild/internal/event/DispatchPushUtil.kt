package love.forte.simbot.component.tencentguild.internal.event

import kotlinx.serialization.json.Json
import love.forte.simbot.Simbot
import love.forte.simbot.component.tencentguild.internal.TencentGuildBotImpl
import love.forte.simbot.event.Event
import love.forte.simbot.tencentguild.EventSignals
import love.forte.simbot.tencentguild.Signal


internal interface SignalToEvent {
    val key: Event.Key<*>
    suspend operator fun invoke(
        bot: TencentGuildBotImpl,
        decoder: Json,
        dispatch: Signal.Dispatch, decoded: () -> Any
    ): Event
}

internal abstract class BaseSignalToEvent<S : Any> : SignalToEvent {
    abstract val type: EventSignals<S>
    override suspend fun invoke(
        bot: TencentGuildBotImpl, decoder: Json,
        dispatch: Signal.Dispatch, decoded: () -> Any
    ): Event {
        Simbot.check(dispatch.type == type.type) {
            "Event type does not match: ${dispatch.type} != ${type.type}"
        }

        @Suppress("UNCHECKED_CAST")
        return doParser(decoded() as S, bot)
    }

    protected abstract suspend fun doParser(data: S, bot: TencentGuildBotImpl): Event
}

internal val eventSignalParsers =
    mapOf<EventSignals<*>, SignalToEvent>(
        EventSignals.Guilds.GuildCreate to TcgGuildCreate.Parser,
        EventSignals.Guilds.GuildUpdate to TcgGuildUpdate.Parser,
        EventSignals.Guilds.GuildDelete to TcgGuildDelete.Parser,

        EventSignals.Guilds.ChannelCreate to TcgChannelCreate.Parser,
        EventSignals.Guilds.ChannelUpdate to TcgChannelUpdate.Parser,
        EventSignals.Guilds.ChannelDelete to TcgChannelDelete.Parser,

        EventSignals.GuildMembers.GuildMemberAdd to TcgGuildMemberIncrease.Parser,
        // EventSignals.GuildMembers.GuildMemberUpdate to TODO(),
        EventSignals.GuildMembers.GuildMemberRemove to TcgGuildMemberDecrease.Parser,

        // EventSignals.DirectMessage.DirectMessageCreate to TODO(),

        EventSignals.AudioAction.AudioStart to TcgAudioStart.Parser,
        EventSignals.AudioAction.AudioFinish to TcgAudioFinish.Parser,
        EventSignals.AudioAction.AudioOnMic to TcgAudioOnMic.Parser,
        EventSignals.AudioAction.AudioOffMic to TcgAudioOffMic.Parser,

        EventSignals.AtMessages.AtMessageCreate to TcgChannelAtMessageEventImpl.Parser,
    )

