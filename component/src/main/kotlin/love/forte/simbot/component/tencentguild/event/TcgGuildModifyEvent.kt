package love.forte.simbot.component.tencentguild.event

import love.forte.simbot.Api4J
import love.forte.simbot.Timestamp
import love.forte.simbot.component.tencentguild.TencentGuild
import love.forte.simbot.component.tencentguild.TencentGuildBot
import love.forte.simbot.event.BaseEventKey
import love.forte.simbot.event.ChangedEvent
import love.forte.simbot.event.Event
import love.forte.simbot.event.GuildEvent
import love.forte.simbot.message.doSafeCast
import love.forte.simbot.tencentguild.EventSignals
import love.forte.simbot.tencentguild.TencentGuildInfo

/**
 *
 * 频道变更事件相关。
 *
 * 变更相关事件均属于 [ChangedEvent].
 *
 * [guild](https://bot.q.qq.com/wiki/develop/api/gateway/guild.html)
 *
 * @author ForteScarlet
 */
public sealed class TcgGuildModifyEvent<F, T> : TcgEvent<TencentGuildInfo>(), ChangedEvent<TencentGuildBot, F, T>,
    GuildEvent {
    abstract override val sourceEventEntity: TencentGuildInfo
    abstract override val eventSignal: EventSignals.Guilds<TencentGuildInfo>
    abstract override val after: T
    abstract override val before: F

    abstract override val changedTime: Timestamp
    abstract override val source: TencentGuildBot
    abstract override val key: Event.Key<out TcgGuildModifyEvent<*, *>>
    abstract override val metadata: Event.Metadata

    @JvmSynthetic
    abstract override suspend fun guild(): TencentGuild

    @Api4J
    abstract override val guild: TencentGuild

    @Api4J
    override val organization: TencentGuild
        get() = guild

    @JvmSynthetic
    override suspend fun organization(): TencentGuild = guild()
    override val bot: TencentGuildBot get() = source

    /**
     * 可见范围是内部的。
     */
    override val visibleScope: Event.VisibleScope get() = Event.VisibleScope.INTERNAL


    public companion object Key :
        BaseEventKey<TcgGuildModifyEvent<*, *>>("tcg.guild_modify", setOf(ChangedEvent, GuildEvent)) {
        override fun safeCast(value: Any): TcgGuildModifyEvent<*, *>? = doSafeCast(value)
    }

    /**
     * 频道创建事件。
     * [before] 恒为null。
     * - 机器人被加入到某个频道的时候
     */
    public abstract class Create : TcgGuildModifyEvent<Any?, TencentGuild>() {
        override val before: Any? get() = null
        abstract override val after: TencentGuild
        override val eventSignal: EventSignals.Guilds<TencentGuildInfo>
            get() = EventSignals.Guilds.GuildCreate


        @OptIn(Api4J::class)
        override val guild: TencentGuild
            get() = after

        @JvmSynthetic
        override suspend fun guild(): TencentGuild = guild

        override val key: Event.Key<out Create> get() = Key


        public companion object Key : BaseEventKey<Create>("tcg.guild_create", setOf(TcgGuildModifyEvent)) {
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
    public abstract class Update : TcgGuildModifyEvent<Any?, TencentGuild>() {
        override val before: Any? get() = null
        abstract override val after: TencentGuild
        override val eventSignal: EventSignals.Guilds<TencentGuildInfo>
            get() = EventSignals.Guilds.GuildUpdate

        @OptIn(Api4J::class)
        override val guild: TencentGuild
            get() = after

        @JvmSynthetic
        override suspend fun guild(): TencentGuild = guild

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
    public abstract class Delete : TcgGuildModifyEvent<TencentGuild, Any?>() {
        abstract override val before: TencentGuild
        override val after: Any? get() = null
        override val eventSignal: EventSignals.Guilds<TencentGuildInfo>
            get() = EventSignals.Guilds.GuildUpdate

        @OptIn(Api4J::class)
        override val guild: TencentGuild
            get() = before

        @JvmSynthetic
        override suspend fun guild(): TencentGuild = guild

        override val key: Event.Key<out Delete> get() = Key

        public companion object Key : BaseEventKey<Delete>("tcg.guild_delete", setOf(TcgGuildModifyEvent)) {
            override fun safeCast(value: Any): Delete? = doSafeCast(value)
        }
    }
}