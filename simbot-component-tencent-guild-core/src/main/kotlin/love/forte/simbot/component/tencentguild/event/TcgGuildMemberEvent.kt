/*
 * Copyright (c) 2022-2023. ForteScarlet.
 *
 * This file is part of simbot-component-tencent-guild.
 *
 * simbot-component-tencent-guild is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * simbot-component-tencent-guild is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with simbot-component-tencent-guild. If not, see <https://www.gnu.org/licenses/>.
 */

package love.forte.simbot.component.tencentguild.event

import love.forte.plugin.suspendtrans.annotation.JvmAsync
import love.forte.plugin.suspendtrans.annotation.JvmBlocking
import love.forte.simbot.action.ActionType
import love.forte.simbot.component.tencentguild.TencentGuild
import love.forte.simbot.component.tencentguild.TencentMember
import love.forte.simbot.event.*
import love.forte.simbot.message.doSafeCast
import love.forte.simbot.qguild.TencentMemberInfo
import love.forte.simbot.qguild.event.EventSignals

/**
 *
 * 频道成员变更事件。
 *
 *
 *
 * @author ForteScarlet
 */
@BaseEvent
@JvmBlocking(asProperty = true, suffix = "")
@JvmAsync(asProperty = true)
public sealed class TcgGuildMemberEvent : TcgEvent<TencentMemberInfo>(), GuildEvent, MemberChangedEvent {
    abstract override val key: Event.Key<out TcgGuildMemberEvent>
    
    /**
     * 涉及的频道服务器信息。同 [organization].
     */
    abstract override suspend fun guild(): TencentGuild
    
    /**
     * 涉及的用户信息。同 [user].
     */
    abstract override suspend fun member(): TencentMember
    
    /**
     * 涉及的用户信息。同 [member].
     */
    override suspend fun user(): TencentMember = member()
    
    /**
     * 涉及的频道服务器信息。同 [guild].
     */
    override suspend fun organization(): TencentGuild = guild()
    
    /**
     * 变更行为后的内容。
     */
    abstract override suspend fun after(): TencentMember?
    
    /**
     * 变更行为前的内容。
     */
    abstract override suspend fun before(): TencentMember?
    
    /**
     * 涉及的频道服务器信息。同 [guild].
     */
    override suspend fun source(): TencentGuild = guild()
    
    /**
     * 本次变更的操作者。不支持的情况下可能为null。
     */
    abstract override suspend fun operator(): TencentMember?
    
    public companion object Key : BaseEventKey<TcgGuildMemberEvent>(
        "tcg.guild_member", setOf(
            TcgEvent, GuildEvent, MemberChangedEvent
        )
    ) {
        override fun safeCast(value: Any): TcgGuildMemberEvent? = doSafeCast(value)
    }
    
    /**
     * 成员增加事件，同时属于 [GuildEvent].
     *
     * 发送时机：新用户加入频道
     */
    @JvmBlocking(asProperty = true, suffix = "")
    @JvmAsync(asProperty = true)
    public abstract class Increase : TcgGuildMemberEvent(), GuildMemberIncreaseEvent {
        /**
         * 增加的成员。同 [user].
         */
        abstract override suspend fun member(): TencentMember
        
        /**
         * 增加的成员。同 [member].
         */
        override suspend fun user(): TencentMember = member()
        
        /**
         * 增加的成员。同 [member].
         */
        override suspend fun after(): TencentMember = member()
        
        /**
         * 成员增加前，没有东西，使用得到null。
         */
        override suspend fun before(): TencentMember? = null
        
        /**
         * 增加成员的频道。
         */
        abstract override suspend fun source(): TencentGuild
        
        /**
         * 增加成员的频道。同 [source].
         */
        override suspend fun organization(): TencentGuild = source()
        
        /**
         * 增加成员的频道。同 [source].
         */
        override suspend fun guild(): TencentGuild = source()
        
        /**
         * 无法判断加入类型。通常为频道分享链接点击加入，视为主动加入。
         */
        override val actionType: ActionType
            get() = ActionType.PROACTIVE
        
        /**
         * 操作者。无法得知，始终为null。
         */
        override suspend fun operator(): TencentMember? = null
        
        
        override val key: Event.Key<out Increase> get() = Key
        
        override val eventSignal: EventSignals<TencentMemberInfo>
            get() = EventSignals.GuildMembers.GuildMemberAdd
        
        
        public companion object Key : BaseEventKey<Increase>(
            "tcg.guild_member_increase", setOf(
                TcgGuildMemberEvent, GuildMemberIncreaseEvent
            )
        ) {
            override fun safeCast(value: Any): Increase? = doSafeCast(value)
        }
        
    }
    
    /**
     * 成员资料变更。无法得到变更前的信息。
     *
     * @see EventSignals.GuildMembers.GuildMemberUpdate
     */
    @Deprecated("暂无")
    public abstract class Update : TcgGuildMemberEvent(), MemberChangedEvent {
        // TODO
        
        @JvmBlocking(asProperty = true, suffix = "")
        @JvmAsync(asProperty = true)
        abstract override suspend fun after(): TencentMember
        
        
        public companion object Key : BaseEventKey<Decrease>(
            "tcg.guild_member_update", setOf(
                TcgGuildMemberEvent, MemberChangedEvent
            )
        ) {
            override fun safeCast(value: Any): Decrease? = doSafeCast(value)
        }
    }
    
    /**
     * 成员减少：成员离开或退出，同时属于 [GuildEvent].
     *
     * 发送时机: 用户离开频道
     *
     */
    @JvmBlocking(asProperty = true, suffix = "")
    @JvmAsync(asProperty = true)
    public abstract class Decrease : TcgGuildMemberEvent(), GuildMemberDecreaseEvent {
        
        /**
         * 离开的成员。同 [user].
         */
        abstract override suspend fun member(): TencentMember
        
        /**
         * 离开的成员。同 [member].
         */
        override suspend fun user(): TencentMember = member()
        
        /**
         * 离开的成员。同 [member].
         */
        override suspend fun before(): TencentMember = member()
    
        /**
         * 离开后，成员不再存在，始终得到null。
         */
        override suspend fun after(): TencentMember? = null
        
        /**
         * 离开成员的频道。
         */
        abstract override suspend fun source(): TencentGuild
        
        /**
         * 离开成员的频道。同 [source].
         */
        override suspend fun organization(): TencentGuild = source()
        
        /**
         * 离开成员的频道。同 [source].
         */
        override suspend fun guild(): TencentGuild = source()
        
        /**
         * 离开频道是主动的。（无法区分）
         */
        override val actionType: ActionType get() = ActionType.PROACTIVE
        
        /**
         * 无法获取操作者，通常为其自行操作。始终为null。
         */
        override suspend fun operator(): TencentMember? = null
        
        
        override val eventSignal: EventSignals<TencentMemberInfo>
            get() = EventSignals.GuildMembers.GuildMemberRemove
        
        override val key: Event.Key<out Decrease> get() = Key
        
        public companion object Key : BaseEventKey<Decrease>(
            "tcg.guild_member_decrease", setOf(
                TcgGuildMemberEvent, GuildMemberDecreaseEvent
            )
        ) {
            override fun safeCast(value: Any): Decrease? = doSafeCast(value)
        }
    }
    
    
}
