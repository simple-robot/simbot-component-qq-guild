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

import love.forte.simbot.ID
import love.forte.simbot.Timestamp
import love.forte.simbot.component.qguild.JSTP
import love.forte.simbot.component.qguild.QGGuild
import love.forte.simbot.component.qguild.QGTextChannel
import love.forte.simbot.event.*
import love.forte.simbot.message.doSafeCast
import love.forte.simbot.qguild.event.EventChannel
import love.forte.simbot.qguild.event.EventChannel as QGSourceEventChannel

/**
 *
 * 子频道相关的事件。
 *
 * [QGChannelEvent] 主要用于为子类型提供一个基础类型，其本身没有过多的事件类型约束。
 *
 * @see QGChannelCreateEvent
 * @see QGChannelUpdateEvent
 * @see QGChannelDeleteEvent
 * @see QGChannelCategoryCreateEvent
 * @see QGChannelCategoryUpdateEvent
 * @see QGChannelCategoryDeleteEvent
 *
 * @author ForteScarlet
 */
@BaseEvent
@JSTP
public sealed class QGChannelEvent : QGEvent<QGSourceEventChannel>() {
    /**
     * 标准库中子频道相关事件得到的事件本体。
     */
    abstract override val sourceEventEntity: EventChannel

    /**
     * 操作者ID
     */
    public val operatorId: ID get() = sourceEventEntity.opUserId.ID

    abstract override val key: Event.Key<out QGChannelEvent>

    public companion object Key : BaseEventKey<QGChannelEvent>(
        "qg.channel_modify", QGEvent
    ) {
        override fun safeCast(value: Any): QGChannelEvent? = doSafeCast(value)
    }
}


/**
 * 子频道被创建
 */
@JSTP
public abstract class QGChannelCreateEvent : QGChannelEvent(), StartPointEvent, ChangedEvent, ChannelEvent {
    abstract override val changedTime: Timestamp
    override val timestamp: Timestamp get() = changedTime

    /**
     * 变更源。即发生变更的频道。
     */
    abstract override suspend fun channel(): QGTextChannel

    /**
     * 变更源。即发生的所在频道服务器
     */
    abstract override suspend fun source(): QGGuild

    /**
     * 事件发生的频道。同 [channel].
     */
    override suspend fun organization(): QGTextChannel = channel()

    /**
     * 被创建的频道。同 [source]。
     */
    override suspend fun after(): QGTextChannel = channel()

    /**
     * 始终为null。
     */
    override suspend fun before(): Any? = null

    override val key: Event.Key<out QGChannelCreateEvent> get() = Key


    public companion object Key : BaseEventKey<QGChannelCreateEvent>(
        "qg.channel_create", setOf(QGChannelEvent, StartPointEvent, ChangedEvent, ChannelEvent)
    ) {
        override fun safeCast(value: Any): QGChannelCreateEvent? = doSafeCast(value)
    }
}

/**
 * 子频道信息变更
 *
 * 无法得知变更前 ([before]) 的信息。
 */
@JSTP
public abstract class QGChannelUpdateEvent : QGChannelEvent(), ChangedEvent, ChannelEvent {
    abstract override val changedTime: Timestamp
    override val timestamp: Timestamp get() = changedTime

    /**
     * 变更源。即发生变更的频道。
     */
    abstract override suspend fun channel(): QGTextChannel

    /**
     * 变更源。即发生的所在频道服务器
     */
    abstract override suspend fun source(): QGGuild

    /**
     * 事件发生的频道。同 [channel].
     */
    override suspend fun organization(): QGTextChannel = channel()

    /**
     * 发生变更的频道。同 [source].
     */
    override suspend fun after(): QGTextChannel = channel()

    /**
     * 始终为null。
     */
    override suspend fun before(): Any? = null

    ////

    override val key: Event.Key<out QGChannelUpdateEvent> get() = Key

    public companion object Key : BaseEventKey<QGChannelUpdateEvent>(
        "qg.channel_update", QGChannelEvent, ChangedEvent, ChannelEvent
    ) {
        override fun safeCast(value: Any): QGChannelUpdateEvent? = doSafeCast(value)
    }
}

/**
 * 子频道被删除
 *
 * 当收到此事件时，channel 已经被删除。
 */
@JSTP
public abstract class QGChannelDeleteEvent : QGChannelEvent(), EndPointEvent, ChannelEvent {
    abstract override val changedTime: Timestamp
    override val timestamp: Timestamp get() = changedTime


    /**
     * 被删除的频道
     */
    abstract override suspend fun channel(): QGTextChannel

    /**
     * 被删除的频道，同 [channel]
     */
    override suspend fun before(): QGTextChannel = channel()

    /**
     * 被删除的频道，同 [channel]
     */
    override suspend fun organization(): QGTextChannel = channel()

    /**
     * 变更源。即发生的所在频道服务器
     */
    abstract override suspend fun source(): QGGuild

    override val key: Event.Key<out QGChannelDeleteEvent> get() = Key

    public companion object Key : BaseEventKey<QGChannelDeleteEvent>(
        "qg.channel_delete", setOf(QGChannelEvent, EndPointEvent, ChannelEvent)
    ) {
        override fun safeCast(value: Any): QGChannelDeleteEvent? = doSafeCast(value)
    }
}