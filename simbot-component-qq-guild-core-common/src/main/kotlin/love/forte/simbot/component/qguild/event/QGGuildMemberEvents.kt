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

import love.forte.simbot.Timestamp
import love.forte.simbot.action.ActionType
import love.forte.simbot.component.qguild.JSTP
import love.forte.simbot.component.qguild.QGGuild
import love.forte.simbot.component.qguild.QGMember
import love.forte.simbot.event.*
import love.forte.simbot.message.doSafeCast
import love.forte.simbot.qguild.event.EventIntents
import love.forte.simbot.qguild.event.EventMember

/**
 * QQ频道[成员相关的事件][EventIntents.GuildMembers]。
 *
 * [QGMemberEvent] 是一种父事件类型，本身不存在过多类型约束。
 *
 * - [新增频道成员][QGMemberAddEvent]
 * - [频道成员信息更新][QGMemberUpdateEvent]
 * - [频道成员离开/移除][QGMemberRemoveEvent]
 *
 * @see QGMemberAddEvent
 * @see QGMemberUpdateEvent
 * @see QGMemberRemoveEvent
 */
@BaseEvent
public sealed class QGMemberEvent : QGEvent<EventMember>() {
    /**
     * 事件接收到的原始的用户信息。
     */
    abstract override val sourceEventEntity: EventMember

    abstract override val key: Event.Key<out QGMemberEvent>

    public companion object Key : BaseEventKey<QGMemberEvent>(
        "qg.member", QGEvent
    ) {
        override fun safeCast(value: Any): QGMemberEvent? = doSafeCast(value)
    }
}

/**
 * 频道成员增加事件。
 *
 */
@JSTP
public abstract class QGMemberAddEvent : QGMemberEvent(), GuildMemberIncreaseEvent {
    /**
     * 事件对象构建时间。
     */
    abstract override val changedTime: Timestamp

    /**
     * 同 [changedTime]
     */
    override val timestamp: Timestamp get() = changedTime

    /**
     * 操作者。无权限获取、找不到（例如获取时已经离群）等情况会得到null。
     */
    abstract override suspend fun operator(): QGMember?

    /**
     * 如果 [operator] 与当前成员相同则视为 [主动][ActionType.PROACTIVE]，
     * 其他情况均视为 [被动][ActionType.PASSIVE]
     */
    abstract override val actionType: ActionType

    /**
     * 新增的频道成员。
     */
    abstract override suspend fun member(): QGMember

    /**
     * 新增的频道成员，同 [member]
     */
    override suspend fun after(): QGMember = member()

    /**
     * 新增的频道成员，同 [member]
     */
    override suspend fun user(): QGMember = member()

    /**
     * 增加了频道成员的频道
     */
    abstract override suspend fun guild(): QGGuild

    /**
     * 增加了频道成员的频道，同 [guild]
     */
    override suspend fun source(): QGGuild = guild()

    /**
     * 增加了频道成员的频道，同 [guild]
     */
    override suspend fun organization(): QGGuild = guild()

    override val key: Event.Key<out QGMemberAddEvent> get() = Key

    public companion object Key : BaseEventKey<QGMemberAddEvent>(
        "qg.member_add", QGMemberEvent, GuildMemberIncreaseEvent
    ) {
        override fun safeCast(value: Any): QGMemberAddEvent? = doSafeCast(value)
    }
}

/**
 * 频道成员信息更新事件。
 */
@JSTP
public abstract class QGMemberUpdateEvent : QGMemberEvent(), MemberChangedEvent, GuildEvent {
    /**
     * 事件对象构建时间。
     */
    abstract override val changedTime: Timestamp

    /**
     * 同 [changedTime]
     */
    override val timestamp: Timestamp get() = changedTime

    /**
     * 操作者。无权限获取、找不到（例如获取时已经离群）等情况会得到null。
     */
    abstract override suspend fun operator(): QGMember?

    /**
     * 发生变更的成员。
     */
    abstract override suspend fun member(): QGMember

    /**
     * 发生变更的成员，同 [member]
     */
    override suspend fun user(): QGMember = member()
    /**
     * 发生变更的成员，同 [member]
     */
    override suspend fun after(): QGMember = member()

    /**
     * 变更前成员的属性，无法获取，始终为null。
     */

    override suspend fun before(): Any? = null

    /**
     * 频道成员所处的频道
     */
    abstract override suspend fun guild(): QGGuild

    /**
     * 增加了频道成员的频道，同 [guild]
     */
    override suspend fun source(): QGGuild = guild()

    /**
     * 频道成员所处的频道，同 [guild]
     */
    override suspend fun organization(): QGGuild = guild()

    override val key: Event.Key<out QGMemberUpdateEvent> get() = Key

    public companion object Key : BaseEventKey<QGMemberUpdateEvent>(
        "qg.member_update", QGMemberEvent, MemberChangedEvent, GuildEvent
    ) {
        override fun safeCast(value: Any): QGMemberUpdateEvent? = doSafeCast(value)
    }
}

/**
 * 频道成员离开/被移除事件。事件触发时已经被移除。
 */
@JSTP
public abstract class QGMemberRemoveEvent : QGMemberEvent(), GuildMemberDecreaseEvent {
    /**
     * 事件对象构建时间。
     */
    abstract override val changedTime: Timestamp

    /**
     * 同 [changedTime]
     */
    override val timestamp: Timestamp get() = changedTime

    /**
     * 操作者。无权限获取、找不到（例如获取时已经离群）等情况会得到null。
     */
    abstract override suspend fun operator(): QGMember?

    /**
     * 如果 [operator] 与当前成员相同则视为 [主动][ActionType.PROACTIVE]，
     * 其他情况均视为 [被动][ActionType.PASSIVE]
     */
    abstract override val actionType: ActionType

    /**
     * 离去的成员。
     */
    abstract override suspend fun member(): QGMember

    /**
     * 离去的成员，同 [member]
     */
    override suspend fun before(): QGMember = member()

    /**
     * 离去的成员，同 [member]
     */
    override suspend fun user(): QGMember = member()

    /**
     * 频道成员所处的频道
     */
    abstract override suspend fun guild(): QGGuild

    /**
     * 频道成员所处的频道，同 [guild]
     */
    override suspend fun source(): QGGuild = guild()

    /**
     * 频道成员所处的频道，同 [guild]
     */
    override suspend fun organization(): QGGuild = guild()

    override val key: Event.Key<out QGMemberRemoveEvent> get() = Key

    public companion object Key : BaseEventKey<QGMemberRemoveEvent>(
        "qg.member_remove", QGMemberEvent, GuildMemberDecreaseEvent
    ) {
        override fun safeCast(value: Any): QGMemberRemoveEvent? = doSafeCast(value)
    }
}


