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
import love.forte.simbot.component.tencentguild.TencentChannel
import love.forte.simbot.event.*
import love.forte.simbot.message.doSafeCast
import love.forte.simbot.tencentguild.EventSignals
import love.forte.simbot.tencentguild.TencentChannelInfo

/**
 *
 * 子频道中与变更相关的事件。
 *
 * @author ForteScarlet
 */
@BaseEvent
public sealed class TcgChannelModifyEvent : TcgEvent<TencentChannelInfo>(), ChangedEvent, ChannelEvent {
    override val changedTime: Timestamp = Timestamp.now()
    override val timestamp: Timestamp get() = changedTime
    abstract override val eventSignal: EventSignals.Guilds<TencentChannelInfo>
    
    protected abstract val sourceInternal: TencentChannel
    
    /**
     * 变更源。即发生变更的频道。
     */
    @JvmBlocking(asProperty = true, suffix = "")
    @JvmAsync(asProperty = true)
    override suspend fun source(): TencentChannel = sourceInternal
    
    
    /**
     * 变更源。即发生变更的频道。同 [source].
     */
    @JvmBlocking(asProperty = true, suffix = "")
    @JvmAsync(asProperty = true)
    override suspend fun channel(): TencentChannel = sourceInternal
    
    
    /**
     * 事件发生的频道。同 [channel].
     */
    @JvmBlocking(asProperty = true, suffix = "")
    @JvmAsync(asProperty = true)
    override suspend fun organization(): TencentChannel = sourceInternal
    
    
    abstract override val key: Event.Key<out TcgChannelModifyEvent>
    
    public companion object Key : BaseEventKey<TcgChannelModifyEvent>(
        "tcg.channel_modify", TcgEvent, ChangedEvent, ChannelEvent
    ) {
        override fun safeCast(value: Any): TcgChannelModifyEvent? = doSafeCast(value)
    }
    
    
    /**
     * 子频道被创建
     */
    public abstract class Create : TcgChannelModifyEvent(), StartPointEvent {
        /**
         * 被创建的频道。同 [source]。
         */
        @JvmBlocking(asProperty = true, suffix = "")
        @JvmAsync(asProperty = true)
        override suspend fun after(): TencentChannel = sourceInternal
        
        
        override val key: Event.Key<out Create> get() = Key
        
        
        public companion object Key : BaseEventKey<Create>(
            "tcg.channel_create", setOf(TcgChannelModifyEvent, StartPointEvent)
        ) {
            override fun safeCast(value: Any): Create? = doSafeCast(value)
        }
    }
    
    /**
     * 子频道信息变更
     *
     * 无法得知变更前 ([before]) 的信息。
     */
    public abstract class Update : TcgChannelModifyEvent() {
        
        /**
         * 发生变更的频道。同 [source].
         */
        @JvmBlocking(asProperty = true, suffix = "")
        @JvmAsync(asProperty = true)
        override suspend fun after(): TencentChannel? = sourceInternal
        
        /**
         * 始终为null。
         */
        @JvmBlocking(asProperty = true, suffix = "")
        @JvmAsync(asProperty = true)
        override suspend fun before(): Any? = null
        
        ////
        
        override val key: Event.Key<out Update> get() = Key
        
        public companion object Key : BaseEventKey<Update>(
            "tcg.channel_update", TcgChannelModifyEvent
        ) {
            override fun safeCast(value: Any): Update? = doSafeCast(value)
        }
    }
    
    /**
     * 子频道被删除
     */
    public abstract class Delete : TcgChannelModifyEvent(), EndPointEvent {
        /**
         * 被删除的频道。同 [channel]。
         */
        @JvmBlocking(asProperty = true, suffix = "")
        @JvmAsync(asProperty = true)
        override suspend fun before(): TencentChannel = sourceInternal
        
        
        override val key: Event.Key<out Delete> get() = Key
        
        public companion object Key : BaseEventKey<Delete>(
            "tcg.channel_delete", setOf(TcgChannelModifyEvent, EndPointEvent)
        ) {
            override fun safeCast(value: Any): Delete? = doSafeCast(value)
        }
    }
    
    
}
