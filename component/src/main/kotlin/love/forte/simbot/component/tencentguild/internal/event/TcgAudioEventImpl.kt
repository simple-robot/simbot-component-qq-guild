package love.forte.simbot.component.tencentguild.internal.event

import love.forte.simbot.ID
import love.forte.simbot.Timestamp
import love.forte.simbot.component.tencentguild.TencentGuild
import love.forte.simbot.component.tencentguild.event.TcgAudioEvent
import love.forte.simbot.component.tencentguild.internal.TencentChannelImpl
import love.forte.simbot.component.tencentguild.internal.TencentGuildBotImpl
import love.forte.simbot.component.tencentguild.internal.TencentGuildImpl
import love.forte.simbot.event.Event
import love.forte.simbot.tencentguild.EventSignals
import love.forte.simbot.tencentguild.TencentAudioAction
import love.forte.simbot.tencentguild.api.channel.GetChannelApi
import love.forte.simbot.tencentguild.api.guild.GetGuildApi
import love.forte.simbot.tencentguild.request
import love.forte.simbot.utils.LazyValue
import love.forte.simbot.utils.lazyValue


private fun TencentGuildBotImpl.lazyGuild(
    action: TencentAudioAction
): LazyValue<TencentGuildImpl> = lazyValue {
    TencentGuildImpl(
        this@lazyGuild, GetGuildApi(action.guildId).request(sourceBot)
    )
}

private fun TencentGuildBotImpl.lazyChannel(
    guildValue: suspend () -> TencentGuildImpl,
    action: TencentAudioAction
): LazyValue<TencentChannelImpl> = lazyValue {
    TencentChannelImpl(
        this@lazyChannel,
        GetChannelApi(action.channelId).request(sourceBot),
        guildValue
    )
}

private class TcgAudioEventMetadata(
    type: Int,
    guildId: ID,
    channelId: ID,
    timestamp: Timestamp
) : Event.Metadata {
    override val id: ID = "$type.$guildId.$channelId.${timestamp.second}".ID
}

/**
 *
 */
internal class TcgAudioStart(
    override val bot: TencentGuildBotImpl,
    override val sourceEventEntity: TencentAudioAction
) : TcgAudioEvent.Start() {
    private val lazyGuild: LazyValue<TencentGuildImpl> = bot.lazyGuild(sourceEventEntity)
    private val lazyChannel: LazyValue<TencentChannelImpl> = bot.lazyChannel(lazyGuild, sourceEventEntity)

    override suspend fun channel(): TencentChannelImpl = lazyChannel.await()
    override suspend fun guild(): TencentGuild = lazyGuild.await()
    override suspend fun organization(): TencentGuildImpl = lazyGuild.await()

    override val timestamp: Timestamp
        get() = Timestamp.now()

    override val visibleScope: Event.VisibleScope
        get() = Event.VisibleScope.PUBLIC

    override val metadata: Event.Metadata =
        TcgAudioEventMetadata(0, sourceEventEntity.guildId, sourceEventEntity.channelId, timestamp)


    internal object Parser : BaseSignalToEvent<TencentAudioAction>() {
        override val type: EventSignals<TencentAudioAction>
            get() = EventSignals.AudioAction.AudioStart
        override val key: Event.Key<*>
            get() = Start

        override suspend fun doParser(data: TencentAudioAction, bot: TencentGuildBotImpl): Event {
            return TcgAudioStart(bot, data)
        }
    }
}

/**
 *
 */
internal class TcgAudioFinish(
    override val bot: TencentGuildBotImpl,
    override val sourceEventEntity: TencentAudioAction
) : TcgAudioEvent.Finish() {
    private val lazyGuild: LazyValue<TencentGuildImpl> = bot.lazyGuild(sourceEventEntity)
    private val lazyChannel: LazyValue<TencentChannelImpl> = bot.lazyChannel(lazyGuild, sourceEventEntity)

    override suspend fun channel(): TencentChannelImpl = lazyChannel.await()
    override suspend fun guild(): TencentGuild = lazyGuild.await()
    override suspend fun organization(): TencentGuildImpl = lazyGuild.await()

    override val timestamp: Timestamp
        get() = Timestamp.now()

    override val visibleScope: Event.VisibleScope
        get() = Event.VisibleScope.PUBLIC

    override val metadata: Event.Metadata =
        TcgAudioEventMetadata(1, sourceEventEntity.guildId, sourceEventEntity.channelId, timestamp)

    internal object Parser : BaseSignalToEvent<TencentAudioAction>() {
        override val type: EventSignals<TencentAudioAction>
            get() = EventSignals.AudioAction.AudioFinish
        override val key: Event.Key<*>
            get() = Finish

        override suspend fun doParser(data: TencentAudioAction, bot: TencentGuildBotImpl): Event {
            return TcgAudioFinish(bot, data)
        }
    }
}

/**
 *
 */
internal class TcgAudioOnMic(
    override val bot: TencentGuildBotImpl,
    override val sourceEventEntity: TencentAudioAction
) : TcgAudioEvent.OnMic() {
    private val lazyGuild: LazyValue<TencentGuildImpl> = bot.lazyGuild(sourceEventEntity)
    private val lazyChannel: LazyValue<TencentChannelImpl> = bot.lazyChannel(lazyGuild, sourceEventEntity)

    override suspend fun channel(): TencentChannelImpl = lazyChannel.await()
    override suspend fun guild(): TencentGuild = lazyGuild.await()
    override suspend fun organization(): TencentGuildImpl = lazyGuild.await()

    override val timestamp: Timestamp
        get() = Timestamp.now()

    override val visibleScope: Event.VisibleScope
        get() = Event.VisibleScope.PUBLIC

    override val metadata: Event.Metadata =
        TcgAudioEventMetadata(2, sourceEventEntity.guildId, sourceEventEntity.channelId, timestamp)

    internal object Parser : BaseSignalToEvent<TencentAudioAction>() {
        override val type: EventSignals<TencentAudioAction>
            get() = EventSignals.AudioAction.AudioOnMic
        override val key: Event.Key<*>
            get() = OnMic

        override suspend fun doParser(data: TencentAudioAction, bot: TencentGuildBotImpl): Event {
            return TcgAudioOnMic(bot, data)
        }
    }
}

/**
 *
 */
internal class TcgAudioOffMic(
    override val bot: TencentGuildBotImpl,
    override val sourceEventEntity: TencentAudioAction
) : TcgAudioEvent.OffMic() {
    private val lazyGuild: LazyValue<TencentGuildImpl> = bot.lazyGuild(sourceEventEntity)
    private val lazyChannel: LazyValue<TencentChannelImpl> = bot.lazyChannel(lazyGuild, sourceEventEntity)

    override suspend fun channel(): TencentChannelImpl = lazyChannel.await()
    override suspend fun guild(): TencentGuild = lazyGuild.await()
    override suspend fun organization(): TencentGuildImpl = lazyGuild.await()

    override val timestamp: Timestamp
        get() = Timestamp.now()

    override val visibleScope: Event.VisibleScope
        get() = Event.VisibleScope.PUBLIC

    override val metadata: Event.Metadata =
        TcgAudioEventMetadata(3, sourceEventEntity.guildId, sourceEventEntity.channelId, timestamp)

    internal object Parser : BaseSignalToEvent<TencentAudioAction>() {
        override val type: EventSignals<TencentAudioAction>
            get() = EventSignals.AudioAction.AudioOffMic
        override val key: Event.Key<*>
            get() = OffMic

        override suspend fun doParser(data: TencentAudioAction, bot: TencentGuildBotImpl): Event {
            return TcgAudioOffMic(bot, data)
        }
    }
}