/*
 * Copyright (c) 2022-2023. ForteScarlet.
 *
 * This file is part of simbot-component-tencent-guild.
 *
 * simbot-component-tencent-guild is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * simbot-component-tencent-guild is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with simbot-component-tencent-guild. If not, see <https://www.gnu.org/licenses/>.
 */

package love.forte.simbot.component.qguild.event

import love.forte.plugin.suspendtrans.annotation.JvmAsync
import love.forte.plugin.suspendtrans.annotation.JvmBlocking
import love.forte.simbot.ID
import love.forte.simbot.Timestamp
import love.forte.simbot.component.qguild.QGChannel
import love.forte.simbot.component.qguild.QGGuild
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
 *
 * @author ForteScarlet
 */
@BaseEvent
@JvmBlocking(asProperty = true, suffix = "")
@JvmAsync(asProperty = true)
public sealed class QGChannelEvent : QGEvent<QGSourceEventChannel>() {
    /**
     * 标准库中子频道相关事件得到的事件本体。
     */
    abstract override val sourceEventEntity: EventChannel

    /**
     * 操作者ID
     */
    public abstract val operatorId: ID

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
@JvmBlocking(asProperty = true, suffix = "")
@JvmAsync(asProperty = true)
public abstract class QGChannelCreateEvent : QGChannelEvent(), StartPointEvent, ChangedEvent, ChannelEvent {

    override val changedTime: Timestamp = Timestamp.now()
    override val timestamp: Timestamp get() = changedTime

    /**
     * 变更源。即发生变更的频道。
     */
    abstract override suspend fun channel(): QGChannel

    /**
     * 变更源。即发生变更的频道。同 [channel]
     */
    override suspend fun source(): QGChannel = channel()

    /**
     * 事件发生的频道。同 [channel].
     */
    override suspend fun organization(): QGChannel = channel()

    /**
     * 被创建的频道。同 [source]。
     */
    override suspend fun after(): QGChannel = channel()

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
@JvmBlocking(asProperty = true, suffix = "")
@JvmAsync(asProperty = true)
public abstract class QGChannelUpdateEvent : QGChannelEvent(), ChangedEvent, ChannelEvent {
    override val changedTime: Timestamp = Timestamp.now()
    override val timestamp: Timestamp get() = changedTime

    /**
     * 变更源。即发生变更的频道。
     */
    abstract override suspend fun channel(): QGChannel

    /**
     * 变更源。即发生变更的频道。同 [channel]
     */
    override suspend fun source(): QGChannel = channel()

    /**
     * 事件发生的频道。同 [channel].
     */
    override suspend fun organization(): QGChannel = channel()

    /**
     * 发生变更的频道。同 [source].
     */
    override suspend fun after(): QGChannel? = channel()

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
 *
 * 当收到此事件时，channel 已经被删除，因此此处仅能 通过 [before] 获取到被删除子频道的 [事件信息][QGSourceEventChannel] 实例。
 */
@JvmBlocking(asProperty = true, suffix = "")
@JvmAsync(asProperty = true)
public abstract class QGChannelDeleteEvent : QGChannelEvent(), EndPointEvent {
    override val changedTime: Timestamp = Timestamp.now()
    override val timestamp: Timestamp get() = changedTime

    /**
     * 被删除的频道。同 [sourceEventEntity]
     *
     * 当收到此事件时，channel 已经被删除，因此此处仅能获取到被删除子频道的 [事件信息][QGSourceEventChannel] 实例。
     */
    override suspend fun before(): QGSourceEventChannel = sourceEventEntity

    /**
     * 被删除的子频道所在的频道服务器。
     */
    abstract override suspend fun source(): QGGuild

    override val key: Event.Key<out QGChannelDeleteEvent> get() = Key

    public companion object Key : BaseEventKey<QGChannelDeleteEvent>(
        "qg.channel_delete", setOf(QGChannelEvent, EndPointEvent)
    ) {
        override fun safeCast(value: Any): QGChannelDeleteEvent? = doSafeCast(value)
    }
}
