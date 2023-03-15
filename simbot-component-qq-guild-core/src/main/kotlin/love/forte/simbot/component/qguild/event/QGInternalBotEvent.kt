/*
 * Copyright (c) 2022-2023. ForteScarlet.
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

import love.forte.simbot.component.qguild.QGBot
import love.forte.simbot.component.qguild.QGBotManager
import love.forte.simbot.event.BaseEventKey
import love.forte.simbot.event.Event
import love.forte.simbot.event.internal.*
import love.forte.simbot.message.doSafeCast

/**
 *
 * 频道组件中对于 [InternalBotEvent] 事件的统一实现接口.
 *
 * 虽然 [QGInternalBotEvent] 实现的是 [Event], 但是它作为 [InternalBotEvent] 的标准实现使用。
 *
 * @author ForteScarlet
 */
public sealed interface QGInternalBotEvent : Event {


    public companion object Key : BaseEventKey<QGInternalBotEvent>(
        "qg.internal.bot", InternalBotEvent
    ) {
        override fun safeCast(value: Any): QGInternalBotEvent? = doSafeCast(value)
    }
}

/**
 * tcg组件中，每当 [QGBotManager] 通过任意 [QGBotManager.register] 注册并得到Bot实例后触发的事件。
 *
 * 遵循 [BotRegisteredEvent] 约定特性，此事件会在注册完成后**异步**触发.
 *
 * @see BotRegisteredEvent
 * @see QGBotManager
 * @see QGBotManager.register
 */
public abstract class QGBotRegisteredEvent : BotRegisteredEvent(), QGInternalBotEvent {

    abstract override val bot: QGBot

    ////
    override val key: InternalEvent.Key<QGBotRegisteredEvent> get() = Key

    public companion object Key : BaseInternalKey<QGBotRegisteredEvent>(
        "qg.internal.bot_registered", InternalBotEvent, QGInternalBotEvent
    ) {
        override fun safeCast(value: Any): QGBotRegisteredEvent? = doSafeCast(value)
    }
}


/**
 * tcg组件中，每当 [QGBot.start] 被执行的时候会被推送的事件。
 * 当事件被推送的时候代表此bot实际上已经完成的 `start` 的逻辑，但是[QGBot.start]会直到事件处理流程完成后才会最终返回。
 *
 * @see BotStartedEvent
 * @see QGBot
 * @see QGBot.start
 */
public abstract class QGBotStartedEvent : BotStartedEvent(), QGInternalBotEvent {
    abstract override val bot: QGBot

    ////
    override val key: InternalEvent.Key<QGBotStartedEvent> get() = Key

    public companion object Key : BaseInternalKey<QGBotStartedEvent>(
        "qg.internal.bot_started", BotStartedEvent, QGInternalBotEvent
    ) {
        override fun safeCast(value: Any): QGBotStartedEvent? = doSafeCast(value)
    }

}
