/*
 * Copyright (c) 2025. ForteScarlet.
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

import love.forte.simbot.component.qguild.bot.QGBot
import love.forte.simbot.event.*

/**
 * QQ组件中针对 [SendSupportInteractionEvent] 的子类型实现。
 *
 * @since 4.2.0
 * @author ForteScarlet
 */
@SubclassOptInRequired(FuzzyEventTypeImplementation::class)
public interface QGMessageEventInteractionEvent : MessageEventInteractionEvent,
    QGInternalMessageInteractionEvent {
    override val bot: QGBot
    override val content: QGMessageEvent
}

/**
 * QG组件中针对 [QGMessageEvent.reply] 的拦截事件。
 * 可以对其中的参数进行修改。
 *
 * @since 4.2.0
 */
@OptIn(FuzzyEventTypeImplementation::class)
public interface OneBotMessageEventPreReplyEvent :
    QGMessageEventInteractionEvent,
    MessageEventPreReplyEvent {
    override val content: QGMessageEvent
    override val message: InteractionMessage
}

/**
 * QG组件中针对 [QGMessageEvent.reply] 的通知事件。
 *
 * @since 4.2.0
 */
@OptIn(FuzzyEventTypeImplementation::class)
public interface OneBotMessageEventPostReplyEvent :
    QGMessageEventInteractionEvent,
    MessageEventPostReplyEvent {
    override val content: QGMessageEvent
    override val message: InteractionMessage
}