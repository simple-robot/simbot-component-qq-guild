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

package love.forte.simbot.component.qguild.internal.event

import love.forte.simbot.annotations.ExperimentalSimbotAPI
import love.forte.simbot.common.id.ID
import love.forte.simbot.common.id.UUID
import love.forte.simbot.common.time.Timestamp
import love.forte.simbot.component.qguild.bot.QGBot
import love.forte.simbot.component.qguild.event.OneBotMessageEventPostReplyEvent
import love.forte.simbot.component.qguild.event.OneBotMessageEventPreReplyEvent
import love.forte.simbot.component.qguild.event.QGMessageEvent
import love.forte.simbot.event.InteractionMessage
import love.forte.simbot.message.MessageReceipt


/**
 * QG组件中针对 [QGMessageEvent.reply] 的拦截事件。
 * 可以对其中的参数进行修改。
 *
 * @since 4.2.0
 */
@OptIn(ExperimentalSimbotAPI::class)
internal class OneBotMessageEventPreReplyEventImpl(
    override val bot: QGBot,
    override val content: QGMessageEvent,
    override val message: InteractionMessage,
    override val time: Timestamp = Timestamp.now(),
) : OneBotMessageEventPreReplyEvent {
    override val id: ID = UUID.random()
    override var currentMessage: InteractionMessage = message
}

/**
 * QG组件中针对 [QGMessageEvent.reply] 的通知事件。
 *
 * @since 4.2.0
 */
@OptIn(ExperimentalSimbotAPI::class)
internal class OneBotMessageEventPostReplyEventImpl(
    override val bot: QGBot,
    override val content: QGMessageEvent,
    override val message: InteractionMessage,
    override val receipt: MessageReceipt,
    override val time: Timestamp = Timestamp.now(),
) : OneBotMessageEventPostReplyEvent {
    override val id: ID = UUID.random()
}