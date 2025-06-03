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

package love.forte.simbot.component.qguild.utils

import love.forte.simbot.ability.SendSupport
import love.forte.simbot.component.qguild.internal.bot.QGBotImpl
import love.forte.simbot.component.qguild.internal.event.QGSendSupportPostSendEventImpl
import love.forte.simbot.component.qguild.message.QGMessageReceipt
import love.forte.simbot.event.InteractionMessage

internal fun QGMessageReceipt.alsoEmitPostSendEvent(
    bot: QGBotImpl,
    content: SendSupport,
    message: InteractionMessage,
):QGMessageReceipt {
    val event = QGSendSupportPostSendEventImpl(
        bot = bot,
        content = content,
        message = message,
        receipt = this
    )
    bot.pushEventAndCollectAsync(event)
    return this
}