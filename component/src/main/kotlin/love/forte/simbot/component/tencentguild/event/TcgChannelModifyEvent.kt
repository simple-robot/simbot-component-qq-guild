package love.forte.simbot.component.tencentguild.event

import love.forte.simbot.Api4J
import love.forte.simbot.Timestamp
import love.forte.simbot.component.tencentguild.TencentChannel
import love.forte.simbot.component.tencentguild.TencentGuild
import love.forte.simbot.component.tencentguild.TencentGuildBot
import love.forte.simbot.event.BaseEventKey
import love.forte.simbot.event.ChangedEvent
import love.forte.simbot.event.ChannelEvent
import love.forte.simbot.event.Event
import love.forte.simbot.message.doSafeCast
import love.forte.simbot.tencentguild.EventSignals
import love.forte.simbot.tencentguild.TencentChannelInfo

/**
 *
 * 子频道相关事件
 *
 * @author ForteScarlet
 */
public sealed class TcgChannelModifyEvent<F, T> : TcgEvent<TencentChannelInfo>(), ChangedEvent<TencentGuild, F, T>,
    ChannelEvent {
    abstract override val sourceEventEntity: TencentChannelInfo
    abstract override suspend fun after(): T
    abstract override suspend fun before(): F
    abstract override suspend fun source(): TencentGuild
    @OptIn(Api4J::class)
    abstract override val channel: TencentChannel


    abstract override val changedTime: Timestamp
    abstract override val bot: TencentGuildBot
    abstract override val key: Event.Key<out TcgChannelModifyEvent<*, *>>
    abstract override val metadata: Event.Metadata
    abstract override val timestamp: Timestamp

    abstract override val eventSignal: EventSignals.Guilds<TencentChannelInfo>

    //// impl

    override suspend fun channel(): TencentChannel = channel

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
        override suspend fun after(): Any? = null
        override val after: Any? get() = null

        override val key: Event.Key<out Delete> get() = Key

        public companion object Key : BaseEventKey<Delete>("tcg.channel_delete", setOf(TcgChannelModifyEvent)) {
            override fun safeCast(value: Any): Delete? = doSafeCast(value)
        }
    }


}