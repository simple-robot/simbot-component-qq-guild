package love.forte.simbot.component.tencentguild.event

import love.forte.simbot.component.tencentguild.TencentGuildBot
import love.forte.simbot.definition.BotContainer
import love.forte.simbot.tencentguild.EventSignals

/**
 *
 * 腾讯频道bot的事件总类。
 *
 * @param T 此类型代表其真正事件所得到的结果。
 *
 * @author ForteScarlet
 */
public abstract class TcgEvent<T> : BotContainer {
    abstract override val bot: TencentGuildBot


    /**
     * 真正的原始事件所得到的事件实体。
     */
    public abstract val sourceEventEntity: T

    /**
     * 这个事件所对应的 [EventSignals] 类型。
     */
    public abstract val eventSignal: EventSignals<T>

}