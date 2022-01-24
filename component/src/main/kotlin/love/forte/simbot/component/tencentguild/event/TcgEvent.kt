/*
 *  Copyright (c) 2022-2022 ForteScarlet <ForteScarlet@163.com>
 *
 *  根据 GNU LESSER GENERAL PUBLIC LICENSE 3 获得许可；
 *  除非遵守许可，否则您不得使用此文件。
 *  您可以在以下网址获取许可证副本：
 *
 *       https://www.gnu.org/licenses/lgpl-3.0-standalone.html
 *
 *   有关许可证下的权限和限制的具体语言，请参见许可证。
 */

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
public abstract class TcgEvent<T : Any> : BotContainer {
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