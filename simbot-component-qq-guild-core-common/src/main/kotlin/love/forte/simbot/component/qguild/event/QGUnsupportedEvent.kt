/*
 * Copyright (c) 2023. ForteScarlet.
 *
 * This file is part of simbot-component-qq-guild.
 *
 * simbot-component-qq-guild is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * simbot-component-qq-guild is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with simbot-component-qq-guild. If not, see <https://www.gnu.org/licenses/>.
 */

package love.forte.simbot.component.qguild.event

import love.forte.simbot.FragileSimbotApi
import love.forte.simbot.ID
import love.forte.simbot.Timestamp
import love.forte.simbot.event.BaseEventKey
import love.forte.simbot.event.Event
import love.forte.simbot.message.doSafeCast
import love.forte.simbot.qguild.event.Signal
import love.forte.simbot.randomID


/**
 * 尚未支持的QQ频道事件类型。
 *
 * 用于表示、推送所有标准库中已经存在、但是组件库内无对应支持的事件类型。
 *
 * ### 仅限临时应用
 *
 * [QGUnsupportedEvent] 用于对尚未支持的**组件事件**提供一个"捡漏"的方案，
 * 但是使用 [QGUnsupportedEvent] 应当只是临时的。当某个事件在某个更新中被支持后，
 * 将不会再出现在 [QGUnsupportedEvent] 中。
 *
 * @author ForteScarlet
 */
@FragileSimbotApi
public abstract class QGUnsupportedEvent : QGEvent<Signal.Dispatch>() {
    /**
     * 事件ID。一个随机字符串。
     */
    override val id: ID = randomID()

    /**
     * 事件构建时间。
     */
    override val timestamp: Timestamp = Timestamp.now()

    /**
     * 原始的标准库事件对象。
     */
    abstract override val sourceEventEntity: Signal.Dispatch

    override val key: Event.Key<out QGEvent<*>> get() = Key

    public companion object Key : BaseEventKey<QGUnsupportedEvent>("qg.unsupported", Event) {
        override fun safeCast(value: Any): QGUnsupportedEvent? = doSafeCast(value)
    }
}
