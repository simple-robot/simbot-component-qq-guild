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

import love.forte.simbot.*
import love.forte.simbot.component.tencentguild.*
import love.forte.simbot.event.*
import love.forte.simbot.message.*
import love.forte.simbot.tencentguild.*

/**
 *
 * 子频道相关事件
 *
 * @author ForteScarlet
 */
public sealed class TcgChannelModifyEvent<F, T> : TcgEvent<TencentChannelInfo>(), ChangedEvent<TencentGuild, F, T>,
    ChannelEvent {
    abstract override val sourceEventEntity: TencentChannelInfo

    @JvmSynthetic
    abstract override suspend fun after(): T

    @JvmSynthetic
    abstract override suspend fun before(): F

    @JvmSynthetic
    abstract override suspend fun source(): TencentGuild

    @OptIn(Api4J::class)
    abstract override val channel: TencentChannel


    abstract override val changedTime: Timestamp
    abstract override val bot: TencentGuildBot
    abstract override val key: Event.Key<out TcgChannelModifyEvent<*, *>>
    abstract override val timestamp: Timestamp

    abstract override val eventSignal: EventSignals.Guilds<TencentChannelInfo>

    //// impl

    @JvmSynthetic
    override suspend fun channel(): TencentChannel = channel

    @JvmSynthetic
    override suspend fun organization(): TencentChannel = channel()

    @Api4J
    override val organization: TencentChannel
        get() = channel

    override val visibleScope: Event.VisibleScope get() = Event.VisibleScope.INTERNAL

    public companion object Key :
        BaseEventKey<TcgChannelModifyEvent<*, *>>("tcg.channel_modify", setOf(ChangedEvent, ChannelEvent)) {
        override fun safeCast(value: Any): TcgChannelModifyEvent<*, *>? = doSafeCast(value)
    }


    /**
     * 子频道被创建
     */
    public abstract class Create : TcgChannelModifyEvent<Any?, TencentChannel>() {
        @JvmSynthetic
        override suspend fun before(): Any? = null
        override val before: Any? get() = null

        override val key: Event.Key<out Create> get() = Key


        public companion object Key : BaseEventKey<Create>("tcg.channel_create", setOf(TcgChannelModifyEvent)) {
            override fun safeCast(value: Any): Create? = doSafeCast(value)
        }
    }

    /**
     * 子频道信息变更
     *
     * 无法得知变更前 ([before]) 的信息。
     */
    public abstract class Update : TcgChannelModifyEvent<Any?, TencentChannel>() {
        @JvmSynthetic
        override suspend fun before(): Any? = null
        override val before: Any? get() = null
        override val key: Event.Key<out Update> get() = Key

        public companion object Key : BaseEventKey<Update>("tcg.channel_update", setOf(TcgChannelModifyEvent)) {
            override fun safeCast(value: Any): Update? = doSafeCast(value)
        }
    }

    /**
     * 子频道被删除
     */
    public abstract class Delete : TcgChannelModifyEvent<TencentChannel, Any?>() {
        @JvmSynthetic
        override suspend fun after(): Any? = null
        override val after: Any? get() = null

        override val key: Event.Key<out Delete> get() = Key

        public companion object Key : BaseEventKey<Delete>("tcg.channel_delete", setOf(TcgChannelModifyEvent)) {
            override fun safeCast(value: Any): Delete? = doSafeCast(value)
        }
    }


}