package love.forte.simbot.component.tencentguild.event

import love.forte.simbot.Timestamp
import love.forte.simbot.component.tencentguild.TencentChannel
import love.forte.simbot.component.tencentguild.TencentGuildBot
import love.forte.simbot.event.BaseEventKey
import love.forte.simbot.event.ChangedEvent
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
public sealed class TcgChannelModifyEvent<F, T> : TcgEvent<TencentChannelInfo>(), ChangedEvent<TencentChannel, F, T> {
    abstract override val sourceEventEntity: TencentChannelInfo
    abstract override val after: T
    abstract override val before: F
    abstract override val changedTime: Timestamp
    abstract override val source: TencentChannel
    abstract override val bot: TencentGuildBot
    abstract override val key: Event.Key<out TcgChannelModifyEvent<*, *>>
    abstract override val metadata: Event.Metadata
    abstract override val timestamp: Timestamp
    abstract override val eventSignal: EventSignals.Guilds<TencentChannelInfo>
    override val visibleScope: Event.VisibleScope get() = Event.VisibleScope.INTERNAL

    public companion object Key :
        BaseEventKey<TcgChannelModifyEvent<*, *>>("sr.tcg.channel_modify", setOf(ChangedEvent)) {
        override fun safeCast(value: Any): TcgChannelModifyEvent<*, *>? = doSafeCast(value)
    }


    /**
     * 子频道被创建
     */
    public abstract class Create : TcgChannelModifyEvent<Any?, TencentChannel>() {
        override val before: Any? get() = null
        override val key: Event.Key<out Create> get() = Key

        public companion object Key : BaseEventKey<Create>("sr.tcg.channel_create", setOf(TcgChannelModifyEvent)) {
            override fun safeCast(value: Any): Create? = doSafeCast(value)
        }
    }

    /**
     * 子频道信息变更
     *
     * 无法得知变更前 ([before]) 的信息。
     */
    public abstract class Update : TcgChannelModifyEvent<Any?, TencentChannel>() {
        override val before: Any? get() = null
        override val key: Event.Key<out Update> get() = Key

        public companion object Key : BaseEventKey<Update>("sr.tcg.channel_update", setOf(TcgChannelModifyEvent)) {
            override fun safeCast(value: Any): Update? = doSafeCast(value)
        }
    }

    /**
     * 子频道被删除
     */
    public abstract class Delete : TcgChannelModifyEvent<TencentChannel, Any?>() {
        override val after: Any? get() = null
        override val key: Event.Key<out Delete> get() = Key

        public companion object Key : BaseEventKey<Delete>("sr.tcg.channel_delete", setOf(TcgChannelModifyEvent)) {
            override fun safeCast(value: Any): Delete? = doSafeCast(value)
        }
    }


}