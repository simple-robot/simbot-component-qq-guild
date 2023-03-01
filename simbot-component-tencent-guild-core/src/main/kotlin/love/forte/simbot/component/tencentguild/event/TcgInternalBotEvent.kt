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

import love.forte.simbot.component.tencentguild.TencentGuildBotManager
import love.forte.simbot.component.tencentguild.TencentGuildComponentBot
import love.forte.simbot.event.BaseEventKey
import love.forte.simbot.event.Event
import love.forte.simbot.event.internal.*
import love.forte.simbot.message.doSafeCast

/**
 *
 * 频道组件中对于 [InternalBotEvent] 事件的统一实现接口.
 *
 * 虽然 [TcgInternalBotEvent] 实现的是 [Event], 但是它作为 [InternalBotEvent] 的标准实现使用。
 *
 * @author ForteScarlet
 */
public sealed interface TcgInternalBotEvent : Event {


    public companion object Key : BaseEventKey<TcgInternalBotEvent>(
        "tcg.internal.bot", InternalBotEvent
    ) {
        override fun safeCast(value: Any): TcgInternalBotEvent? = doSafeCast(value)
    }
}

/**
 * tcg组件中，每当 [TencentGuildBotManager] 通过任意 [TencentGuildBotManager.register] 注册并得到Bot实例后触发的事件。
 *
 * 遵循 [BotRegisteredEvent] 约定特性，此事件会在注册完成后**异步**触发.
 *
 * @see BotRegisteredEvent
 * @see TencentGuildBotManager
 * @see TencentGuildBotManager.register
 */
public abstract class TcgBotRegisteredEvent : BotRegisteredEvent(), TcgInternalBotEvent {

    abstract override val bot: TencentGuildComponentBot

    ////
    override val key: InternalEvent.Key<TcgBotRegisteredEvent> get() = Key

    public companion object Key : BaseInternalKey<TcgBotRegisteredEvent>(
        "tcg.internal.bot.registered", InternalBotEvent, TcgInternalBotEvent
    ) {
        override fun safeCast(value: Any): TcgBotRegisteredEvent? = doSafeCast(value)
    }
}


/**
 * tcg组件中，每当 [TencentGuildComponentBot.start] 被执行的时候会被推送的事件。
 * 当事件被推送的时候代表此bot实际上已经完成的 `start` 的逻辑，但是[TencentGuildComponentBot.start]会直到事件处理流程完成后才会最终返回。
 *
 * @see BotStartedEvent
 * @see TencentGuildComponentBot
 * @see TencentGuildComponentBot.start
 */
public abstract class TcgBotStartedEvent : BotStartedEvent(), TcgInternalBotEvent {
    abstract override val bot: TencentGuildComponentBot

    ////
    override val key: InternalEvent.Key<TcgBotStartedEvent> get() = Key

    public companion object Key : BaseInternalKey<TcgBotStartedEvent>(
        "tcg.internal.bot.started", BotStartedEvent, TcgInternalBotEvent
    ) {
        override fun safeCast(value: Any): TcgBotStartedEvent? = doSafeCast(value)
    }

}
