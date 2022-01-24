/*
 *  Copyright (c) 2022-2022 ForteScarlet <ForteScarlet@163.com>
 *
 *  根据 GNU LESSER GENERAL PUBLIC LICENSE 3 获得许可；
 *  除非遵守许可，否则您不得使用此文件。
 *  您可以在以下网址获取许可证副本：
 *
 *       https://www.gnu.org/licenses/lgpl-3.0-standalone.html
 *
 *   有关许可证下的权限和限制的具体语言，请参见许可证。
 */

package love.forte.simbot.component.tencentguild.event

import kotlinx.coroutines.runBlocking
import love.forte.simbot.Api4J
import love.forte.simbot.component.tencentguild.TencentChannel
import love.forte.simbot.component.tencentguild.TencentGuild
import love.forte.simbot.component.tencentguild.TencentGuildBot
import love.forte.simbot.event.BaseEventKey
import love.forte.simbot.event.ChannelEvent
import love.forte.simbot.event.Event
import love.forte.simbot.event.GuildEvent
import love.forte.simbot.message.doSafeCast
import love.forte.simbot.tencentguild.EventSignals
import love.forte.simbot.tencentguild.TencentAudioAction


/**
 * 音频事件
 *
 * @see EventSignals.AudioAction.AudioStart
 * @see EventSignals.AudioAction.AudioFinish
 * @see EventSignals.AudioAction.AudioOnMic
 * @see EventSignals.AudioAction.AudioOffMic
 */
public sealed class TcgAudioEvent : TcgEvent<TencentAudioAction>(),
    ChannelEvent, GuildEvent {
    @JvmSynthetic
    abstract override suspend fun channel(): TencentChannel

    @JvmSynthetic
    abstract override suspend fun guild(): TencentGuild
    abstract override val key: Event.Key<out TcgAudioEvent>
    abstract override val metadata: Event.Metadata
    abstract override val bot: TencentGuildBot

    @JvmSynthetic
    abstract override suspend fun organization(): TencentGuild

    abstract override val sourceEventEntity: TencentAudioAction
    abstract override val eventSignal: EventSignals<TencentAudioAction>

    @Api4J
    override val channel: TencentChannel
        get() = runBlocking { channel() }

    @Api4J
    override val guild: TencentGuild
        get() = runBlocking { guild() }

    @Api4J
    override val organization: TencentGuild
        get() = runBlocking { organization() }


    public companion object Key : BaseEventKey<TcgAudioEvent>(
        "tcg.audio", setOf(ChannelEvent)
    ) {
        override fun safeCast(value: Any): TcgAudioEvent? = doSafeCast(value)
    }

    /**
     * @see EventSignals.AudioAction.AudioStart
     */
    public abstract class Start : TcgAudioEvent() {
        override val eventSignal: EventSignals<TencentAudioAction>
            get() = EventSignals.AudioAction.AudioStart

        override val key: Event.Key<Start>
            get() = Key

        public companion object Key : BaseEventKey<Start>(
            "tcg.audio.start", setOf(TcgAudioEvent)
        ) {
            override fun safeCast(value: Any): Start? = doSafeCast(value)
        }
    }

    /**
     * @see EventSignals.AudioAction.AudioFinish
     */
    public abstract class Finish : TcgAudioEvent() {
        override val eventSignal: EventSignals<TencentAudioAction>
            get() = EventSignals.AudioAction.AudioFinish

        override val key: Event.Key<Finish>
            get() = Key

        public companion object Key : BaseEventKey<Finish>(
            "tcg.audio.finish", setOf(TcgAudioEvent)
        ) {
            override fun safeCast(value: Any): Finish? = doSafeCast(value)
        }
    }

    /**
     * @see EventSignals.AudioAction.AudioOnMic
     */
    public abstract class OnMic : TcgAudioEvent() {
        override val eventSignal: EventSignals<TencentAudioAction>
            get() = EventSignals.AudioAction.AudioOnMic

        override val key: Event.Key<OnMic>
            get() = Key

        public companion object Key : BaseEventKey<OnMic>(
            "tcg.audio.on_mic", setOf(TcgAudioEvent)
        ) {
            override fun safeCast(value: Any): OnMic? = doSafeCast(value)
        }
    }

    /**
     * @see EventSignals.AudioAction.AudioOffMic
     */
    public abstract class OffMic : TcgAudioEvent() {
        override val eventSignal: EventSignals<TencentAudioAction>
            get() = EventSignals.AudioAction.AudioOffMic

        override val key: Event.Key<OffMic>
            get() = Key

        public companion object Key : BaseEventKey<OffMic>(
            "tcg.audio.off_mic", setOf(TcgAudioEvent)
        ) {
            override fun safeCast(value: Any): OffMic? = doSafeCast(value)
        }
    }

}


