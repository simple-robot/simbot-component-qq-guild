/*
 * Copyright (c) 2022-2023. ForteScarlet.
 *
 * This file is part of simbot-component-qq-guild.
 *
 * simbot-component-qq-guild is free software: you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * simbot-component-qq-guild is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with simbot-component-qq-guild.
 * If not, see <https://www.gnu.org/licenses/>.
 */

package love.forte.simbot.component.qguild.event

import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import love.forte.simbot.FragileSimbotApi
import love.forte.simbot.ID
import love.forte.simbot.Timestamp
import love.forte.simbot.component.qguild.JSTP
import love.forte.simbot.component.qguild.QGBot
import love.forte.simbot.component.qguild.QGGuild
import love.forte.simbot.event.*
import love.forte.simbot.message.doSafeCast
import love.forte.simbot.qguild.event.EventGuild
import love.forte.simbot.qguild.model.Guild as QGSourceGuild

/**
 *
 * 频道变更事件相关。
 *
 * [QGGuildEvent] 主要为子类型提供基本类型，但 [QGGuildEvent] 本身没有过多事件类型约束。
 *
 * [频道新增事件][QGGuildCreateEvent] 表示bot进入到了一个新的频道中。
 *
 * [频道更新事件][QGGuildUpdateEvent] 表示某个频道的信息发生了更新。
 *
 * [频道移除事件][QGGuildDeleteEvent] 表示bot离开了某个频道。
 * 此事件与前两者有较大差别：它不实现 [GuildEvent] —— 毕竟频道已经离开（不存在）了。
 *
 * @see QGGuildCreateEvent
 * @see QGGuildUpdateEvent
 * @see QGGuildDeleteEvent
 *
 * @author ForteScarlet
 */
public sealed class QGGuildEvent : QGEvent<EventGuild>() {
    /**
     * 事件涉及的bot
     */
    abstract override val bot: QGBot

    /**
     * 事件操作者的ID
     */
    public abstract val operatorId: ID

    abstract override val key: Event.Key<out QGGuildEvent>
    
    public companion object Key :
        BaseEventKey<QGGuildEvent>(
            "qg.guild_modify", QGEvent
        ) {
        override fun safeCast(value: Any): QGGuildEvent? = doSafeCast(value)
    }
}

/**
 * 频道创建事件。
 *
 * 触发时机：
 * - 机器人被加入到某个频道服务器的时候
 *
 * [before] 恒为null。
 */
@JSTP
public abstract class QGGuildCreateEvent : QGGuildEvent(), StartPointEvent, GuildEvent, ChangedEvent {
    override val changedTime: Timestamp = Timestamp.now()
    override val timestamp: Timestamp get() = changedTime

    /**
     * 变更源。同 [bot].
     */
    override suspend fun source(): QGBot = bot

    /**
     * 创建前。始终为null。
     */
    override suspend fun before(): QGGuild? = null

    /**
     * 创建的guild。
     */
    abstract override suspend fun guild(): QGGuild

    /**
     * 创建的guild。同 [guild].
     */
    override suspend fun after(): QGGuild = guild()

    /**
     * 创建的guild。同 [guild].
     */
    override suspend fun organization(): QGGuild = guild()

    override val key: Event.Key<out QGGuildCreateEvent> get() = Key

    public companion object Key : BaseEventKey<QGGuildCreateEvent>(
        "qg.guild_create", setOf(QGGuildEvent, StartPointEvent, GuildEvent, ChangedEvent)
    ) {
        override fun safeCast(value: Any): QGGuildCreateEvent? = doSafeCast(value)
    }
}

/**
 * 频道更新事件。
 *
 * 触发时机：
 * - 频道信息变更
 *
 * [before] 恒为null。
 *
 * [after] 字段内容为变更后的字段内容
 */
@JSTP
public abstract class QGGuildUpdateEvent : QGGuildEvent(), GuildEvent, ChangedEvent {
    override val changedTime: Timestamp = Timestamp.now()
    override val timestamp: Timestamp get() = changedTime

    /**
     * 变更源。同 [bot].
     */
    override suspend fun source(): QGBot = bot

    /**
     * 频道更新前。无法得知，始终为null。
     */
    override suspend fun before(): QGGuild? = null

    /**
     * 创建的guild。
     */
    abstract override suspend fun guild(): QGGuild

    /**
     * 创建的guild。同 [guild].
     */
    override suspend fun organization(): QGGuild = guild()

    /**
     * 变更的guild。同 [guild].
     */
    override suspend fun after(): QGGuild = guild()

    override val key: Event.Key<out QGGuildUpdateEvent> get() = Key

    public companion object Key : BaseEventKey<QGGuildUpdateEvent>("qg.guild_update", setOf(QGGuildEvent, GuildEvent, ChangedEvent)) {
        override fun safeCast(value: Any): QGGuildUpdateEvent? = doSafeCast(value)
    }
}

/**
 * 频道删除事件。
 *
 * [QGGuildDeleteEvent] 不实现 [GuildEvent]，因为事件触发时已经离开频道，不存在可稳定获取的 [QGGuild] 实例。
 * 但是此事件额外提供属性 [guild], 代表获取一个**可能存在**的不稳定 [QGGuild]. 详细内容参考 [guild] 说明。
 *
 *
 * 触发时机：
 * - 频道被解散
 * - 机器人被移除
 *
 * [before] 字段内容为变更前的字段内容
 *
 */
@JSTP
public abstract class QGGuildDeleteEvent : QGGuildEvent(), ChangedEvent {
    override val changedTime: Timestamp = Timestamp.now()
    override val timestamp: Timestamp get() = changedTime

    /**
     * 变更源。同 [bot].
     */
    override suspend fun source(): QGBot = bot

    /**
     * 被删除的guild。
     *
     * 获取一个**可能存在**的不稳定 [QGGuild].
     * 之所以称其为**不稳定**，因为这个对象**可能**来自于内部缓存中已经被移除的对象。当此对象能够被获取时，
     * 它已经被 [关闭][QGGuild.cancel] 了，不再存在可用的 [Job] ，且已经无法使用大部分API（发送消息等）
     *
     * 如果希望得到较为可靠的**频道信息**，使用 [sourceEventEntity] 或 [before].
     */
    @FragileSimbotApi
    public abstract val guild: QGGuild?


    /**
     * 被删除的guild信息。
     *
     * 同 [sourceEventEntity]
     */
    override suspend fun before(): QGSourceGuild = sourceEventEntity

    /**
     * 删除后。始终为null。
     */
    override suspend fun after(): QGGuild? = null

    override val key: Event.Key<out QGGuildDeleteEvent> get() = Key

    public companion object Key : BaseEventKey<QGGuildDeleteEvent>("qg.guild_delete", setOf(QGGuildEvent, ChangedEvent)) {
        override fun safeCast(value: Any): QGGuildDeleteEvent? = doSafeCast(value)
    }
}
