package love.forte.simbot.component.tencentguild.internal.event

import love.forte.simbot.ID
import love.forte.simbot.Timestamp
import love.forte.simbot.component.tencentguild.TencentChannel
import love.forte.simbot.component.tencentguild.TencentGuild
import love.forte.simbot.component.tencentguild.event.TcgChannelModifyEvent
import love.forte.simbot.component.tencentguild.internal.TencentChannelImpl
import love.forte.simbot.component.tencentguild.internal.TencentGuildBotImpl
import love.forte.simbot.component.tencentguild.internal.TencentGuildImpl
import love.forte.simbot.component.tencentguild.util.request
import love.forte.simbot.event.Event
import love.forte.simbot.tencentguild.EventSignals
import love.forte.simbot.tencentguild.TencentChannelInfo
import love.forte.simbot.tencentguild.api.guild.GetGuildApi
import love.forte.simbot.utils.lazyValue


internal class TcgChannelCreate
constructor(
    override val sourceEventEntity: TencentChannelInfo,
    override val bot: TencentGuildBotImpl,
    override val channel: TencentChannelImpl,
) : TcgChannelModifyEvent.Create() {
    override val after: TencentChannelImpl get() = channel
    override val changedTime: Timestamp = Timestamp.now()
    override val timestamp: Timestamp get() = changedTime
    override val metadata: Event.Metadata = TcgChannelModifyMetadata(0, bot.id, channel.id, changedTime)
    override val eventSignal: EventSignals.Guilds.ChannelCreate
        get() = EventSignals.Guilds.ChannelCreate

    override suspend fun after(): TencentChannel = after
    override suspend fun source(): TencentGuild = channel.guild()

    internal object Parser : BaseSignalToEvent<TencentChannelInfo>() {
        override val key: Event.Key<out Create> get() = Create
        override val type: EventSignals<TencentChannelInfo> = EventSignals.Guilds.ChannelCreate
        override suspend fun doParser(data: TencentChannelInfo, bot: TencentGuildBotImpl): TcgChannelCreate {
            val guildId = data.guildId

            return TcgChannelCreate(
                data,
                bot,
                TencentChannelImpl(bot, data, bot.lazyValue {
                    TencentGuildImpl(bot, GetGuildApi(guildId).request(bot))
                })
            )
        }
    }
}


internal class TcgChannelUpdate
constructor(
    override val sourceEventEntity: TencentChannelInfo,
    override val bot: TencentGuildBotImpl,
    override val channel: TencentChannelImpl,
) : TcgChannelModifyEvent.Update() {
    override val after: TencentChannelImpl get() = channel
    override val changedTime: Timestamp = Timestamp.now()
    override val timestamp: Timestamp get() = changedTime
    override val metadata: Event.Metadata = TcgChannelModifyMetadata(1, bot.id, channel.id, changedTime)
    override val eventSignal: EventSignals.Guilds.ChannelUpdate
        get() = EventSignals.Guilds.ChannelUpdate

    override suspend fun source(): TencentGuild = channel.guild()
    override suspend fun after(): TencentChannel = after

    internal object Parser : BaseSignalToEvent<TencentChannelInfo>() {
        override val key: Event.Key<out Update> get() = Update
        override val type: EventSignals<TencentChannelInfo> = EventSignals.Guilds.ChannelUpdate

        override suspend fun doParser(data: TencentChannelInfo, bot: TencentGuildBotImpl): TcgChannelUpdate {
            val guildId = data.guildId

            return TcgChannelUpdate(
                data,
                bot,
                TencentChannelImpl(bot, data, bot.lazyValue {
                    TencentGuildImpl(bot, GetGuildApi(guildId).request(bot))
                })
            )
        }
    }
}


internal class TcgChannelDelete
constructor(
    override val sourceEventEntity: TencentChannelInfo,
    override val bot: TencentGuildBotImpl,
    override val channel: TencentChannelImpl,
) : TcgChannelModifyEvent.Delete() {
    override val before: TencentChannel get() = channel
    override val changedTime: Timestamp = Timestamp.now()
    override val timestamp: Timestamp get() = changedTime
    override val metadata: Event.Metadata = TcgChannelModifyMetadata(2, bot.id, channel.id, changedTime)
    override val eventSignal: EventSignals.Guilds.ChannelDelete
        get() = EventSignals.Guilds.ChannelDelete

    override suspend fun before(): TencentChannel = before
    override suspend fun source(): TencentGuild = channel.guild()

    internal object Parser : BaseSignalToEvent<TencentChannelInfo>() {
        override val key: Event.Key<out Delete> get() = Delete
        override val type: EventSignals<TencentChannelInfo> = EventSignals.Guilds.ChannelDelete

        override suspend fun doParser(data: TencentChannelInfo, bot: TencentGuildBotImpl): TcgChannelDelete {
            val guildId = data.guildId

            return TcgChannelDelete(
                data,
                bot,
                TencentChannelImpl(bot, data, bot.lazyValue {
                    TencentGuildImpl(bot, GetGuildApi(guildId).request(bot))
                })
            )
        }
    }
}


private class TcgChannelModifyMetadata(t: Int, sourceBot: ID, sourceChannel: ID, timestamp: Timestamp) :
    Event.Metadata {
    override val id: ID = "$t$sourceBot.${timestamp.second}.$sourceChannel".ID
}