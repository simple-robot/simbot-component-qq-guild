/*
 * Copyright (c) 2022-2023. ForteScarlet.
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

package love.forte.simbot.component.qguild.internal

import love.forte.simbot.ID
import love.forte.simbot.component.qguild.message.QGMessageReceipt
import love.forte.simbot.message.SingleMessageReceipt
import love.forte.simbot.qguild.model.Message


private class QGMessageAsReceipt(override val messageResult: Message) : QGMessageReceipt, SingleMessageReceipt() {
    override val id: ID = messageResult.id.ID
}


internal fun Message.asReceipt(): QGMessageReceipt = QGMessageAsReceipt(this)

