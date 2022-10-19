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
import love.forte.simbot.component.tencentguild.TencentGuild
import love.forte.simbot.component.tencentguild.TencentGuildComponentBot
import love.forte.simbot.event.*
import love.forte.simbot.message.doSafeCast
import love.forte.simbot.tencentguild.EventSignals
import love.forte.simbot.tencentguild.TencentGuildInfo

/**
 *
 * 频道变更事件相关。
 *
 * 变更相关事件均属于 [ChangedEvent], [变更源][source] 为当前的bot。
 *
 * [guild](https://bot.q.qq.com/wiki/develop/api/gateway/guild.html)
 *
 * @author ForteScarlet
 */
public sealed class TcgGuildModifyEvent : TcgEvent<TencentGuildInfo>(),
    ChangedEvent, GuildEvent {
    abstract override val eventSignal: EventSignals.Guilds<TencentGuildInfo>
    override val changedTime: Timestamp = Timestamp.now()
    
    /**
     * 变更源。同 [bot].
     */
    @JvmBlocking(asProperty = true, suffix = "")
    @JvmAsync(asProperty = true)
    override suspend fun source(): TencentGuildComponentBot = bot
    
    /**
     * 可能存在的变更后频道服务器。
     */
    @JvmBlocking(asProperty = true, suffix = "")
    @JvmAsync(asProperty = true)
    abstract override suspend fun before(): TencentGuild?
    
    
    /**
     * 可能存在的变更后频道服务器。
     */
    @JvmBlocking(asProperty = true, suffix = "")
    @JvmAsync(asProperty = true)
    abstract override suspend fun after(): TencentGuild?
    
    /**
     * 涉及到的频道服务器。
     */
    @JvmBlocking(asProperty = true, suffix = "")
    @JvmAsync(asProperty = true)
    abstract override suspend fun guild(): TencentGuild
    
    /**
     * 涉及到的频道服务器。同 [guild].
     */
    @JvmBlocking(asProperty = true, suffix = "")
    @JvmAsync(asProperty = true)
    override suspend fun organization(): TencentGuild = guild()
    
    
    abstract override val key: Event.Key<out TcgGuildModifyEvent>
    
    public companion object Key :
        BaseEventKey<TcgGuildModifyEvent>(
            "tcg.guild_modify", setOf(TcgEvent, ChangedEvent, GuildEvent)
        ) {
        override fun safeCast(value: Any): TcgGuildModifyEvent? = doSafeCast(value)
    }
    
    /**
     * 频道创建事件。
     *
     * [before] 恒为null。
     *
     * - 机器人被加入到某个频道的时候
     */
    public abstract class Create : TcgGuildModifyEvent(), StartPointEvent {
        /**
         * 创建前。始终为null。
         */
        @JvmSynthetic
        override suspend fun before(): TencentGuild? = null
        
        /**
         * 创建的guild。同 [guild].
         */
        @JvmSynthetic
        override suspend fun after(): TencentGuild = guild()
        
        override val eventSignal: EventSignals.Guilds<TencentGuildInfo>
            get() = EventSignals.Guilds.GuildCreate
        
        
        override val key: Event.Key<out Create> get() = Key
        
        
        public companion object Key : BaseEventKey<Create>(
            "tcg.guild_create", setOf(TcgGuildModifyEvent, StartPointEvent)
        ) {
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
    public abstract class Update : TcgGuildModifyEvent() {
        
        /**
         * 无法得知，始终为null。
         */
        @JvmSynthetic
        override suspend fun before(): TencentGuild? = null
        
        /**
         * 变更的guild。同 [guild].
         */
        @JvmSynthetic
        override suspend fun after(): TencentGuild = guild()
        
        override val eventSignal: EventSignals.Guilds<TencentGuildInfo>
            get() = EventSignals.Guilds.GuildUpdate
        
        
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
    public abstract class Delete : TcgGuildModifyEvent() {
        
        /**
         * 被删除的guild。同 [guild]。
         */
        @JvmSynthetic
        override suspend fun before(): TencentGuild? = guild()
        
        /**
         * 删除后。始终为null。
         */
        @JvmSynthetic
        override suspend fun after(): TencentGuild? = null
        
        override val eventSignal: EventSignals.Guilds<TencentGuildInfo>
            get() = EventSignals.Guilds.GuildDelete
        
        
        override val key: Event.Key<out Delete> get() = Key
        
        public companion object Key : BaseEventKey<Delete>("tcg.guild_delete", setOf(TcgGuildModifyEvent)) {
            override fun safeCast(value: Any): Delete? = doSafeCast(value)
        }
    }
}