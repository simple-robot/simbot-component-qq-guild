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

import love.forte.plugin.suspendtrans.annotation.JvmAsync
import love.forte.plugin.suspendtrans.annotation.JvmBlocking
import love.forte.simbot.Timestamp
import love.forte.simbot.component.tencentguild.TencentChannel
import love.forte.simbot.event.BaseEvent
import love.forte.simbot.event.BaseEventKey
import love.forte.simbot.event.ChannelEvent
import love.forte.simbot.event.Event
import love.forte.simbot.message.doSafeCast
import love.forte.simbot.tencentguild.EventSignals
import love.forte.simbot.tencentguild.TencentAudioAction


/**
 * 子频道音频事件.
 *
 * @see EventSignals.AudioAction.AudioStart
 * @see EventSignals.AudioAction.AudioFinish
 * @see EventSignals.AudioAction.AudioOnMic
 * @see EventSignals.AudioAction.AudioOffMic
 */
@BaseEvent
@JvmBlocking
@JvmAsync
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


