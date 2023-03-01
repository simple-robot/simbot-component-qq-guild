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

package love.forte.simbot.component.tencentguild.event

import love.forte.simbot.ID
import love.forte.simbot.Timestamp
import love.forte.simbot.component.tencentguild.TencentGuildComponentBot
import love.forte.simbot.definition.BotContainer
import love.forte.simbot.event.BaseEvent
import love.forte.simbot.event.BaseEventKey
import love.forte.simbot.event.Event
import love.forte.simbot.message.doSafeCast
import love.forte.simbot.tencentguild.EventSignals

/**
 *
 * QQ频道bot的事件总类。
 *
 * @param T 此类型代表其真正事件所得到的结果。
 *
 * @author ForteScarlet
 */
@BaseEvent
public abstract class TcgEvent<T : Any> : Event, BotContainer {
    abstract override val bot: TencentGuildComponentBot

    /**
     * 事件ID。
     */
    abstract override val id: ID

    /**
     * 接收到事件的时间。
     */
    abstract override val timestamp: Timestamp

    /**
     * 真正的原始事件所得到的事件实体。
     */
    public abstract val sourceEventEntity: T

    /**
     * 这个事件所对应的 [EventSignals] 类型。
     */
    public abstract val eventSignal: EventSignals<T>


    abstract override val key: Event.Key<out TcgEvent<*>>

    public companion object : BaseEventKey<TcgEvent<*>>("tcg.event", Event) {
        override fun safeCast(value: Any): TcgEvent<*>? = doSafeCast(value)
    }
}
