package love.forte.simbot.component.tencentguild.internal.event

import love.forte.simbot.ID
import love.forte.simbot.Timestamp
import love.forte.simbot.action.ActionType
import love.forte.simbot.component.tencentguild.TencentGuild
import love.forte.simbot.component.tencentguild.TencentMember
import love.forte.simbot.component.tencentguild.event.TcgGuildMemberEvent
import love.forte.simbot.component.tencentguild.internal.TencentGuildBotImpl
import love.forte.simbot.component.tencentguild.internal.TencentGuildImpl
import love.forte.simbot.component.tencentguild.internal.TencentMemberImpl
import love.forte.simbot.event.Event
import love.forte.simbot.tencentguild.EventSignals
import love.forte.simbot.tencentguild.TencentMemberInfo
import love.forte.simbot.tencentguild.api.guild.GetGuildApi
import love.forte.simbot.tencentguild.request
import love.forte.simbot.utils.lazyValue


private class TcgGuildMemberEventMetadata(type: Int, botId: ID, memberId: ID, timestamp: Timestamp) : Event.Metadata {
    override val id: ID = "$type$botId.$memberId.${timestamp.second}".ID
}


/**
 *
 * @see TcgGuildMemberEvent.Increase
 */
internal class TcgGuildMemberIncrease constructor(
    override val bot: TencentGuildBotImpl,
    override val sourceEventEntity: TencentMemberInfo,
    override val target: TencentMemberImpl,
) : TcgGuildMemberEvent.Increase() {

    override val changedTime: Timestamp = Timestamp.now()

    override val timestamp: Timestamp
        get() = changedTime

    /**
     * 离开频道是主动的。（无法区分）
     */
    override val actionType: ActionType
        get() = ActionType.PROACTIVE


    override suspend fun source(): TencentGuild = target.organization()

    override val metadata: Event.Metadata = TcgGuildMemberEventMetadata(0, bot.id, target.id, timestamp)

    override val visibleScope: Event.VisibleScope get() = Event.VisibleScope.INTERNAL

    override suspend fun guild(): TencentGuild = target.organization()
    override suspend fun member(): TencentMember = target


    internal object Parser : BaseSignalToEvent<TencentMemberInfo>() {
        override val type: EventSignals<TencentMemberInfo>
            get() = EventSignals.GuildMembers.GuildMemberAdd

        override val key: Event.Key<*>
            get() = Increase

        override suspend fun doParser(data: TencentMemberInfo, bot: TencentGuildBotImpl): TcgGuildMemberIncrease {
            val guildId = data.guildId!!
            val member = TencentMemberImpl(
                bot, data,
                bot.lazyValue { TencentGuildImpl(bot, GetGuildApi(guildId).request(bot)) }
            )
            return TcgGuildMemberIncrease(bot, data, member)
        }
    }
}

/**
 *
 * @see TcgGuildMemberEvent.Decrease
 */
internal class TcgGuildMemberDecrease constructor(
    override val bot: TencentGuildBotImpl,
    override val sourceEventEntity: TencentMemberInfo,
    override val target: TencentMemberImpl,
) : TcgGuildMemberEvent.Decrease() {

    override val changedTime: Timestamp = Timestamp.now()

    override val timestamp: Timestamp
        get() = changedTime

    /**
     * 离开频道是主动的。（无法区分）
     */
    override val actionType: ActionType
        get() = ActionType.PROACTIVE

    override suspend fun source(): TencentGuild = target.organization()


    override val metadata: Event.Metadata = TcgGuildMemberEventMetadata(2, bot.id, target.id, timestamp)

    override val visibleScope: Event.VisibleScope get() = Event.VisibleScope.INTERNAL

    override suspend fun guild(): TencentGuild = target.organization()

    override suspend fun member(): TencentMember = target


    internal object Parser : BaseSignalToEvent<TencentMemberInfo>() {
        override val type: EventSignals<TencentMemberInfo>
            get() = EventSignals.GuildMembers.GuildMemberRemove

        override val key: Event.Key<*>
            get() = Decrease

        override suspend fun doParser(data: TencentMemberInfo, bot: TencentGuildBotImpl): TcgGuildMemberDecrease {
            val guildId = data.guildId!!
            val member = TencentMemberImpl(
                bot, data,
                bot.lazyValue { TencentGuildImpl(bot, GetGuildApi(guildId).request(bot)) }
            )
            return TcgGuildMemberDecrease(bot, data, member)
        }
    }
}