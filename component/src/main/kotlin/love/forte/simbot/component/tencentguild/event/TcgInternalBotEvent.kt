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

import love.forte.simbot.component.tencentguild.TencentGuildBot
import love.forte.simbot.component.tencentguild.TencentGuildBotManager
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

    abstract override val bot: TencentGuildBot

    ////
    override val key: InternalEvent.Key<TcgBotRegisteredEvent> get() = Key

    public companion object Key : BaseInternalKey<TcgBotRegisteredEvent>(
        "tcg.internal.bot.registered", InternalBotEvent, TcgInternalBotEvent
    ) {
        override fun safeCast(value: Any): TcgBotRegisteredEvent? = doSafeCast(value)
    }
}


/**
 * tcg组件中，每当 [TencentGuildBot.start] 被执行的时候会被推送的事件。
 * 当事件被推送的时候代表此bot实际上已经完成的 `start` 的逻辑，但是[TencentGuildBot.start]会直到事件处理流程完成后才会最终返回。
 *
 * @see BotStartedEvent
 * @see TencentGuildBot
 * @see TencentGuildBot.start
 */
public abstract class TcgBotStartedEvent : BotStartedEvent(), TcgInternalBotEvent {
    abstract override val bot: TencentGuildBot

    ////
    override val key: InternalEvent.Key<TcgBotStartedEvent> get() = Key

    public companion object Key : BaseInternalKey<TcgBotStartedEvent>(
        "tcg.internal.bot.started", BotStartedEvent, TcgInternalBotEvent
    ) {
        override fun safeCast(value: Any): TcgBotStartedEvent? = doSafeCast(value)
    }

}