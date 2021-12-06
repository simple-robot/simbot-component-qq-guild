package love.forte.simbot.component.tencentguild.internal.event

import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
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


private fun TencentGuildBotImpl.guildDeferred(
    action: TencentAudioAction
): Deferred<TencentGuildImpl> = async(start = CoroutineStart.LAZY) {
    TencentGuildImpl(
        this@guildDeferred, GetGuildApi(action.guildId).request(this@guildDeferred)
    )
}

private fun TencentGuildBotImpl.channelDeferred(
    guildDeferred: Deferred<TencentGuildImpl>,
    action: TencentAudioAction
): Deferred<TencentChannelImpl> = async(start = CoroutineStart.LAZY) {
    TencentChannelImpl(
        this@channelDeferred,
        GetChannelApi(action.channelId).request(this@channelDeferred),
        guildDeferred
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
    private val guildDeferred: Deferred<TencentGuildImpl> = bot.guildDeferred(sourceEventEntity)
    private val channelDeferred: Deferred<TencentChannelImpl> = bot.channelDeferred(guildDeferred, sourceEventEntity)

    override suspend fun channel(): TencentChannelImpl = channelDeferred.await()
    override suspend fun guild(): TencentGuild = guildDeferred.await()
    override suspend fun organization(): TencentGuildImpl = guildDeferred.await()

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
    private val guildDeferred: Deferred<TencentGuildImpl> = bot.guildDeferred(sourceEventEntity)
    private val channelDeferred: Deferred<TencentChannelImpl> = bot.channelDeferred(guildDeferred, sourceEventEntity)

    override suspend fun channel(): TencentChannelImpl = channelDeferred.await()
    override suspend fun guild(): TencentGuild = guildDeferred.await()
    override suspend fun organization(): TencentGuildImpl = guildDeferred.await()

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
    private val guildDeferred: Deferred<TencentGuildImpl> = bot.guildDeferred(sourceEventEntity)
    private val channelDeferred: Deferred<TencentChannelImpl> = bot.channelDeferred(guildDeferred, sourceEventEntity)

    override suspend fun channel(): TencentChannelImpl = channelDeferred.await()
    override suspend fun guild(): TencentGuild = guildDeferred.await()
    override suspend fun organization(): TencentGuildImpl = guildDeferred.await()

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
    private val guildDeferred: Deferred<TencentGuildImpl> = bot.guildDeferred(sourceEventEntity)
    private val channelDeferred: Deferred<TencentChannelImpl> = bot.channelDeferred(guildDeferred, sourceEventEntity)

    override suspend fun channel(): TencentChannelImpl = channelDeferred.await()
    override suspend fun guild(): TencentGuild = guildDeferred.await()
    override suspend fun organization(): TencentGuildImpl = guildDeferred.await()

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