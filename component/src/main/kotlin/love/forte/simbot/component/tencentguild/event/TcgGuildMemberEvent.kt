package love.forte.simbot.component.tencentguild.event

import kotlinx.coroutines.runBlocking
import love.forte.simbot.Api4J
import love.forte.simbot.Timestamp
import love.forte.simbot.component.tencentguild.TencentGuild
import love.forte.simbot.component.tencentguild.TencentGuildBot
import love.forte.simbot.component.tencentguild.TencentMember
import love.forte.simbot.event.*
import love.forte.simbot.message.doSafeCast
import love.forte.simbot.tencentguild.EventSignals
import love.forte.simbot.tencentguild.TencentMemberInfo

/**
 *
 * 频道成员变更事件。
 *
 *
 *
 * @author ForteScarlet
 */
public sealed class TcgGuildMemberEvent<T : Any> : TcgEvent<T>(), GuildEvent, MemberEvent {
    abstract override val key: Event.Key<out TcgGuildMemberEvent<*>>
    abstract override val metadata: Event.Metadata
    abstract override val timestamp: Timestamp
    abstract override val visibleScope: Event.VisibleScope

    abstract override suspend fun guild(): TencentGuild
    abstract override suspend fun member(): TencentMember

    abstract override val bot: TencentGuildBot
    abstract override val sourceEventEntity: T
    abstract override val eventSignal: EventSignals<T>

    //// impl

    override suspend fun user(): TencentMember = member()
    override suspend fun organization(): TencentGuild = guild()


    @Api4J
    override val guild: TencentGuild
        get() = runBlocking { guild() }

    @Api4J
    override val member: TencentMember
        get() = runBlocking { member() }

    @Api4J
    override val user: TencentMember
        get() = runBlocking { user() }

    @Api4J
    override val organization: TencentGuild
        get() = runBlocking { organization() }

    public companion object Key : BaseEventKey<TcgGuildMemberEvent<*>>(
        "tcg.guild_member", setOf(
            MemberEvent, GuildEvent
        )
    ) {
        override fun safeCast(value: Any): TcgGuildMemberEvent<*>? = doSafeCast(value)
    }

    /**
     * 成员增加
     *
     * 发送时机
     * - 新用户加入频道
     */
    public abstract class Increase : TcgGuildMemberEvent<TencentMemberInfo>(), MemberIncreaseEvent {
        abstract override suspend fun source(): TencentGuild
        abstract override val target: TencentMemberInfo

        override val eventSignal: EventSignals<TencentMemberInfo>
            get() = EventSignals.GuildMembers.GuildMemberAdd

        override val key: Event.Key<Increase> get() = Key

        override val source: TencentGuild get() = runBlocking { source() }

        @Api4J
        override val organization: TencentGuild
            get() = source
        override val after: TencentMemberInfo get() = target
        override val before: TencentMemberInfo? get() = null
        override suspend fun target(): TencentMemberInfo = target

        public companion object Key : BaseEventKey<Increase>(
            "tcg.guild_member_increase", setOf(
                TcgGuildMemberEvent, MemberIncreaseEvent
            )
        ) {
            override fun safeCast(value: Any): Increase? = doSafeCast(value)
        }

    }

    /**
     * 成员资料变更
     *
     * @see EventSignals.GuildMembers.GuildMemberUpdate
     */
    @Deprecated("暂无")
    public abstract class Update
    //: TcgGuildMemberEvent(), ChangedEvent<TencentMember, Any?, TencentMember>
    {

    }

    /**
     * 成员减少：成员离开或退出
     *
     * 发送时机
     * - 用户离开频道
     *
     */
    public abstract class Decrease : TcgGuildMemberEvent<TencentMemberInfo>(), MemberDecreaseEvent {
        abstract override suspend fun source(): TencentGuild
        abstract override val target: TencentMemberInfo

        override val eventSignal: EventSignals<TencentMemberInfo>
            get() = EventSignals.GuildMembers.GuildMemberRemove

        override val key: Event.Key<Decrease> get() = Key


        @Api4J
        override val organization: TencentGuild get() = source
        override val source: TencentGuild get() = runBlocking { source() }
        override val after: TencentMemberInfo? get() = null
        override val before: TencentMemberInfo get() = target
        override suspend fun target(): TencentMemberInfo = target

        public companion object Key : BaseEventKey<Decrease>(
            "tcg.guild_member_decrease", setOf(
                TcgGuildMemberEvent, MemberDecreaseEvent
            )
        ) {
            override fun safeCast(value: Any): Decrease? = doSafeCast(value)
        }
    }


}