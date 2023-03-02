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
import love.forte.simbot.event.BaseEvent
import love.forte.simbot.event.BaseEventKey
import love.forte.simbot.event.ChannelEvent
import love.forte.simbot.event.Event
import love.forte.simbot.message.doSafeCast
import love.forte.simbot.qguild.TencentAudioAction
import love.forte.simbot.qguild.event.EventSignals


/**
 * 子频道音频事件.
 *
 * @see EventSignals.AudioAction.AudioStart
 * @see EventSignals.AudioAction.AudioFinish
 * @see EventSignals.AudioAction.AudioOnMic
 * @see EventSignals.AudioAction.AudioOffMic
 */
@BaseEvent
@JvmBlocking(asProperty = true, suffix = "")
@JvmAsync(asProperty = true)
public sealed class TcgAudioEvent : TcgEvent<TencentAudioAction>(), ChannelEvent {
    
    override val timestamp: Timestamp = Timestamp.now()
    
    protected abstract val channelInternal: TencentChannel
    
    /**
     * 事件发生的频道。
     */
    override suspend fun channel(): TencentChannel = channelInternal
    
    /**
     * 事件发生的频道。同 [channel].
     */
    override suspend fun organization(): TencentChannel = channelInternal
    
    
    abstract override val key: Event.Key<out TcgAudioEvent>
    
    public companion object Key : BaseEventKey<TcgAudioEvent>(
        "tcg.audio", TcgEvent, ChannelEvent
    ) {
        override fun safeCast(value: Any): TcgAudioEvent? = doSafeCast(value)
    }
    
    /**
     * 音频开始播放时。
     *
     * @see EventSignals.AudioAction.AudioStart
     */
    public abstract class Start : TcgAudioEvent() {
        override val eventSignal: EventSignals<TencentAudioAction>
            get() = EventSignals.AudioAction.AudioStart
        
        override val key: Event.Key<Start>
            get() = Key
        
        public companion object Key : BaseEventKey<Start>(
            "tcg.audio.start", TcgAudioEvent
        ) {
            override fun safeCast(value: Any): Start? = doSafeCast(value)
        }
    }
    
    /**
     * 音频播放结束时
     * @see EventSignals.AudioAction.AudioFinish
     */
    public abstract class Finish : TcgAudioEvent() {
        override val eventSignal: EventSignals<TencentAudioAction>
            get() = EventSignals.AudioAction.AudioFinish
        
        override val key: Event.Key<Finish>
            get() = Key
        
        public companion object Key : BaseEventKey<Finish>(
            "tcg.audio.finish", TcgAudioEvent
        ) {
            override fun safeCast(value: Any): Finish? = doSafeCast(value)
        }
    }
    
    /**
     * 上麦时
     * @see EventSignals.AudioAction.AudioOnMic
     */
    public abstract class OnMic : TcgAudioEvent() {
        override val eventSignal: EventSignals<TencentAudioAction>
            get() = EventSignals.AudioAction.AudioOnMic
        
        override val key: Event.Key<OnMic>
            get() = Key
        
        public companion object Key : BaseEventKey<OnMic>(
            "tcg.audio.on_mic", TcgAudioEvent
        ) {
            override fun safeCast(value: Any): OnMic? = doSafeCast(value)
        }
    }
    
    /**
     * 下麦时
     * @see EventSignals.AudioAction.AudioOffMic
     */
    public abstract class OffMic : TcgAudioEvent() {
        override val eventSignal: EventSignals<TencentAudioAction>
            get() = EventSignals.AudioAction.AudioOffMic
        
        override val key: Event.Key<OffMic>
            get() = Key
        
        public companion object Key : BaseEventKey<OffMic>(
            "tcg.audio.off_mic", TcgAudioEvent
        ) {
            override fun safeCast(value: Any): OffMic? = doSafeCast(value)
        }
    }
    
}


