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

import love.forte.simbot.Api4J
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

    /**
     * 变更源。即发生变更的频道。
     */
    @OptIn(Api4J::class)
    abstract override val source: TencentChannel

    /**
     * 变更源。即发生变更的频道。
     */
    @JvmSynthetic
    override suspend fun source(): TencentChannel = source

    /**
     * 变更源。即发生变更的频道。同 [source].
     */
    @OptIn(Api4J::class)
    override val channel: TencentChannel get() = source

    /**
     * 变更源。即发生变更的频道。同 [source].
     */
    @JvmSynthetic
    override suspend fun channel(): TencentChannel = channel


    /**
     * 事件发生的频道。同 [channel].
     */
    @Api4J
    override val organization: TencentChannel
        get() = channel

    /**
     * 事件发生的频道。同 [channel].
     */
    @JvmSynthetic
    override suspend fun organization(): TencentChannel = channel()


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
         * 被创建的频道。
         */
        @Suppress("UnnecessaryOptInAnnotation")
        @OptIn(Api4J::class)
        abstract override val source: TencentChannel



        /**
         * 被创建的频道。同 [source]。
         */
        @OptIn(Api4J::class)
        override val after: TencentChannel get() = source

        /**
         * 被创建的频道。同 [source]。
         */
        @JvmSynthetic
        override suspend fun after(): TencentChannel = after

        ////


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
         * 更新的频道。
         */
        @Suppress("UnnecessaryOptInAnnotation")
        @OptIn(Api4J::class)
        abstract override val source: TencentChannel

        /**
         * 发生变更的频道。同 [source].
         */
        @OptIn(Api4J::class)
        override val after: TencentChannel get() = source

        /**
         * 发生变更的频道。同 [source].
         */
        @JvmSynthetic
        override suspend fun after(): TencentChannel? = after


        /**
         * 始终为null。
         */
        @OptIn(Api4J::class)
        override val before: Any? get() = null

        /**
         * 始终为null。
         */
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
         * 被删除的频道。
         */
        @Suppress("UnnecessaryOptInAnnotation")
        @OptIn(Api4J::class)
        abstract override val source: TencentChannel

        /**
         * 被删除的频道。同 [source]。
         */
        @OptIn(Api4J::class)
        override val before: TencentChannel get() = source

        /**
         * 被删除的频道。同 [channel]。
         */
        @JvmSynthetic
        override suspend fun before(): TencentChannel = before


        override val key: Event.Key<out Delete> get() = Key

        public companion object Key : BaseEventKey<Delete>(
            "tcg.channel_delete", setOf(TcgChannelModifyEvent, EndPointEvent)
        ) {
            override fun safeCast(value: Any): Delete? = doSafeCast(value)
        }
    }


}