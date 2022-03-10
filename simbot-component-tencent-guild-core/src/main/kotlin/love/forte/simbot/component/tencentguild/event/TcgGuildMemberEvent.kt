/*
 *  Copyright (c) 2022-2022 ForteScarlet <ForteScarlet@163.com>
 *
 *  本文件是 simbot-component-tencent-guild 的一部分。
 *
 *  simbot-component-tencent-guild 是自由软件：你可以再分发之和/或依照由自由软件基金会发布的 GNU 通用公共许可证修改之，无论是版本 3 许可证，还是（按你的决定）任何以后版都可以。
 *
 *  发布 simbot-component-tencent-guild 是希望它能有用，但是并无保障;甚至连可销售和符合某个特定的目的都不保证。请参看 GNU 通用公共许可证，了解详情。
 *
 *  你应该随程序获得一份 GNU 通用公共许可证的复本。如果没有，请看:
 *  https://www.gnu.org/licenses
 *  https://www.gnu.org/licenses/gpl-3.0-standalone.html
 *  https://www.gnu.org/licenses/lgpl-3.0-standalone.html
 *
 *
 */

package love.forte.simbot.component.tencentguild.event

import kotlinx.coroutines.*
import love.forte.simbot.*
import love.forte.simbot.component.tencentguild.*
import love.forte.simbot.definition.*
import love.forte.simbot.event.*
import love.forte.simbot.message.*
import love.forte.simbot.tencentguild.*

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
    abstract override val timestamp: Timestamp
    abstract override val visibleScope: Event.VisibleScope

    @JvmSynthetic
    abstract override suspend fun guild(): TencentGuild

    @JvmSynthetic
    abstract override suspend fun member(): TencentMember

    abstract override val bot: TencentGuildComponentBot
    abstract override val sourceEventEntity: T
    abstract override val eventSignal: EventSignals<T>

    //// impl

    @JvmSynthetic
    override suspend fun user(): TencentMember = member()

    @JvmSynthetic
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
        @JvmSynthetic
        abstract override suspend fun source(): TencentGuild
        abstract override val target: TencentMember

        override val eventSignal: EventSignals<TencentMemberInfo>
            get() = EventSignals.GuildMembers.GuildMemberAdd

        override val key: Event.Key<Increase> get() = Key

        @JvmSynthetic
        override suspend fun operator(): MemberInfo = target

        @OptIn(Api4J::class)
        override val operator: MemberInfo?
            get() = target

        override val source: TencentGuild get() = runBlocking { source() }

        @Api4J
        override val organization: TencentGuild
            get() = source
        override val after: TencentMember get() = target
        override val before: TencentMember? get() = null

        @JvmSynthetic
        override suspend fun target(): TencentMember = target

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
        @JvmSynthetic
        abstract override suspend fun source(): TencentGuild
        abstract override val target: TencentMember

        override val eventSignal: EventSignals<TencentMemberInfo>
            get() = EventSignals.GuildMembers.GuildMemberRemove

        override val key: Event.Key<Decrease> get() = Key

        @JvmSynthetic
        override suspend fun operator(): MemberInfo = target

        @OptIn(Api4J::class)
        override val operator: MemberInfo?
            get() = target


        @Api4J
        override val organization: TencentGuild
            get() = source
        override val source: TencentGuild get() = runBlocking { source() }
        override val after: TencentMember? get() = null
        override val before: TencentMember get() = target

        @JvmSynthetic
        override suspend fun target(): TencentMember = target

        public companion object Key : BaseEventKey<Decrease>(
            "tcg.guild_member_decrease", setOf(
                TcgGuildMemberEvent, MemberDecreaseEvent
            )
        ) {
            override fun safeCast(value: Any): Decrease? = doSafeCast(value)
        }
    }


}