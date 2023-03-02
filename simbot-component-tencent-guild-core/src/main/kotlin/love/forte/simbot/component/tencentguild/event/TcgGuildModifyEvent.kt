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
import love.forte.simbot.Timestamp
import love.forte.simbot.component.tencentguild.TencentGuild
import love.forte.simbot.component.tencentguild.TencentGuildComponentBot
import love.forte.simbot.event.*
import love.forte.simbot.message.doSafeCast
import love.forte.simbot.qguild.TencentGuildInfo
import love.forte.simbot.qguild.event.EventSignals

/**
 *
 * 频道变更事件相关。
 *
 * 变更相关事件均属于 [ChangedEvent], [变更源][source] 为当前的bot。
 *
 * [guild](https://bot.q.qq.com/wiki/develop/api/gateway/guild.html)
 *
 * @author ForteScarlet
 */
public sealed class TcgGuildModifyEvent : TcgEvent<TencentGuildInfo>(),
    ChangedEvent, GuildEvent {
    abstract override val eventSignal: EventSignals.Guilds<TencentGuildInfo>
    override val changedTime: Timestamp = Timestamp.now()
    
    /**
     * 变更源。同 [bot].
     */
    @JvmBlocking(asProperty = true, suffix = "")
    @JvmAsync(asProperty = true)
    override suspend fun source(): TencentGuildComponentBot = bot
    
    /**
     * 可能存在的变更后频道服务器。
     */
    @JvmBlocking(asProperty = true, suffix = "")
    @JvmAsync(asProperty = true)
    abstract override suspend fun before(): TencentGuild?
    
    
    /**
     * 可能存在的变更后频道服务器。
     */
    @JvmBlocking(asProperty = true, suffix = "")
    @JvmAsync(asProperty = true)
    abstract override suspend fun after(): TencentGuild?
    
    /**
     * 涉及到的频道服务器。
     */
    @JvmBlocking(asProperty = true, suffix = "")
    @JvmAsync(asProperty = true)
    abstract override suspend fun guild(): TencentGuild
    
    /**
     * 涉及到的频道服务器。同 [guild].
     */
    @JvmBlocking(asProperty = true, suffix = "")
    @JvmAsync(asProperty = true)
    override suspend fun organization(): TencentGuild = guild()
    
    
    abstract override val key: Event.Key<out TcgGuildModifyEvent>
    
    public companion object Key :
        BaseEventKey<TcgGuildModifyEvent>(
            "tcg.guild_modify", setOf(TcgEvent, ChangedEvent, GuildEvent)
        ) {
        override fun safeCast(value: Any): TcgGuildModifyEvent? = doSafeCast(value)
    }
    
    /**
     * 频道创建事件。
     *
     * [before] 恒为null。
     *
     * - 机器人被加入到某个频道的时候
     */
    public abstract class Create : TcgGuildModifyEvent(), StartPointEvent {
        /**
         * 创建前。始终为null。
         */
        @JvmBlocking(asProperty = true, suffix = "")
        @JvmAsync(asProperty = true)
        override suspend fun before(): TencentGuild? = null
        
        /**
         * 创建的guild。同 [guild].
         */
        @JvmSynthetic
        override suspend fun after(): TencentGuild = guild()
        
        override val eventSignal: EventSignals.Guilds<TencentGuildInfo>
            get() = EventSignals.Guilds.GuildCreate
        
        
        override val key: Event.Key<out Create> get() = Key
        
        
        public companion object Key : BaseEventKey<Create>(
            "tcg.guild_create", setOf(TcgGuildModifyEvent, StartPointEvent)
        ) {
            override fun safeCast(value: Any): Create? = doSafeCast(value)
        }
    }
    
    /**
     * 频道更新事件。
     * [before] 恒为null。
     *
     * - 频道信息变更
     *
     * [after] 字段内容为变更后的字段内容
     */
    public abstract class Update : TcgGuildModifyEvent() {
        
        /**
         * 无法得知，始终为null。
         */
        @JvmSynthetic
        override suspend fun before(): TencentGuild? = null
        
        /**
         * 变更的guild。同 [guild].
         */
        @JvmSynthetic
        override suspend fun after(): TencentGuild = guild()
        
        override val eventSignal: EventSignals.Guilds<TencentGuildInfo>
            get() = EventSignals.Guilds.GuildUpdate
        
        
        override val key: Event.Key<out Update> get() = Key
        
        public companion object Key : BaseEventKey<Update>("tcg.guild_update", setOf(TcgGuildModifyEvent)) {
            override fun safeCast(value: Any): Update? = doSafeCast(value)
        }
    }
    
    /**
     * 频道删除事件。
     * [after] 恒为null。
     *
     * - 频道被解散
     * - 机器人被移除
     *
     * [before] 字段内容为变更前的字段内容
     *
     */
    public abstract class Delete : TcgGuildModifyEvent() {
        
        /**
         * 被删除的guild。同 [guild]。
         */
        @JvmSynthetic
        override suspend fun before(): TencentGuild? = guild()
        
        /**
         * 删除后。始终为null。
         */
        @JvmSynthetic
        override suspend fun after(): TencentGuild? = null
        
        override val eventSignal: EventSignals.Guilds<TencentGuildInfo>
            get() = EventSignals.Guilds.GuildDelete
        
        
        override val key: Event.Key<out Delete> get() = Key
        
        public companion object Key : BaseEventKey<Delete>("tcg.guild_delete", setOf(TcgGuildModifyEvent)) {
            override fun safeCast(value: Any): Delete? = doSafeCast(value)
        }
    }
}
