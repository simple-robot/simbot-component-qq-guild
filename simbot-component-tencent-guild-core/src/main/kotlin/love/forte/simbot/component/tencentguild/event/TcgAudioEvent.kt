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
import love.forte.simbot.event.*
import love.forte.simbot.message.*
import love.forte.simbot.tencentguild.*


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
    abstract override val id: ID

    @JvmSynthetic
    abstract override suspend fun channel(): TencentChannel

    @JvmSynthetic
    abstract override suspend fun guild(): TencentGuild
    abstract override val key: Event.Key<out TcgAudioEvent>
    abstract override val bot: TencentGuildComponentBot

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


