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

import kotlinx.coroutines.runBlocking
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
    abstract override suspend fun source(): TencentGuildBot
    abstract override suspend fun after(): T
    abstract override suspend fun before(): F
    abstract override suspend fun guild(): TencentGuild

    abstract override val changedTime: Timestamp
    abstract override val source: TencentGuildBot
    abstract override val key: Event.Key<out TcgGuildModifyEvent<*, *>>
    abstract override val metadata: Event.Metadata

    //// impl


    @Api4J
    override val guild: TencentGuild
        get() = runBlocking { guild() }

    @Api4J
    override val organization: TencentGuild
        get() = guild

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
        abstract override val source: TencentGuildBot
        abstract override val after: TencentGuild

        override suspend fun source(): TencentGuildBot = source
        override suspend fun after(): TencentGuild = after
        override suspend fun before(): Any? = null
        override val eventSignal: EventSignals.Guilds<TencentGuildInfo>
            get() = EventSignals.Guilds.GuildCreate


        override suspend fun guild(): TencentGuild = after()

        @Api4J
        override val guild: TencentGuild
            get() = after

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
        abstract override val source: TencentGuildBot
        abstract override val after: TencentGuild

        override suspend fun after(): TencentGuild = after
        override suspend fun source(): TencentGuildBot = source
        override suspend fun before(): Any? = null
        override val eventSignal: EventSignals.Guilds<TencentGuildInfo>
            get() = EventSignals.Guilds.GuildUpdate

        @Api4J
        override val guild: TencentGuild
            get() = after

        override suspend fun guild(): TencentGuild = after()

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
        abstract override val source: TencentGuildBot
        abstract override val before: TencentGuild

        override suspend fun before(): TencentGuild = before
        override suspend fun source(): TencentGuildBot = source
        override suspend fun after(): Any? = null
        override val eventSignal: EventSignals.Guilds<TencentGuildInfo>
            get() = EventSignals.Guilds.GuildUpdate

        @Api4J
        override val guild: TencentGuild
            get() = before

        override suspend fun guild(): TencentGuild = before()

        override val key: Event.Key<out Delete> get() = Key

        public companion object Key : BaseEventKey<Delete>("tcg.guild_delete", setOf(TcgGuildModifyEvent)) {
            override fun safeCast(value: Any): Delete? = doSafeCast(value)
        }
    }
}