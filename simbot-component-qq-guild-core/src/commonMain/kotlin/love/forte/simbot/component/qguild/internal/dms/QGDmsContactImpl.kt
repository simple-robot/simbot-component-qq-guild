/*
 * Copyright (c) 2024. ForteScarlet.
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

package love.forte.simbot.component.qguild.internal.dms

import love.forte.simbot.component.qguild.dms.QGDmsContact
import love.forte.simbot.component.qguild.internal.bot.QGBotImpl
import love.forte.simbot.component.qguild.internal.bot.newSupervisorCoroutineContext
import love.forte.simbot.component.qguild.message.sendDmsMessage
import love.forte.simbot.component.qguild.message.sendMessage
import love.forte.simbot.message.Message
import love.forte.simbot.message.MessageContent
import love.forte.simbot.message.MessageReceipt
import love.forte.simbot.qguild.QQGuildApiException
import love.forte.simbot.qguild.addStackTrace
import love.forte.simbot.qguild.model.User
import kotlin.coroutines.CoroutineContext


/**
 *
 * @author ForteScarlet
 */
internal class QGDmsContactImpl(
    private val bot: QGBotImpl,
    override val source: User,
    private val guildId: String,
    private val currentMsgId: String?,
) : QGDmsContact {
    override val coroutineContext: CoroutineContext = bot.newSupervisorCoroutineContext()

    override suspend fun send(text: String): MessageReceipt {
        return try {
            bot.sendDmsMessage(guildId, text) {
                if (msgId == null) {
                    msgId = currentMsgId
                }
            }
        } catch (e: QQGuildApiException) {
            throw e.addStackTrace { "dmsContact.send" }
        }
    }

    override suspend fun send(message: Message): MessageReceipt {
        return try {
            bot.sendDmsMessage(guildId, message) {
                if (msgId == null) {
                    msgId = currentMsgId
                }
            }
        } catch (e: QQGuildApiException) {
            throw e.addStackTrace { "dmsContact.send" }
        }
    }

    override suspend fun send(messageContent: MessageContent): MessageReceipt {
        return try {
            bot.sendMessage(source.id, messageContent) {
                if (msgId == null) {
                    msgId = currentMsgId
                }
            }
        } catch (e: QQGuildApiException) {
            throw e.addStackTrace { "dmsContact.send" }
        }
    }

    override fun toString(): String {
        return "QGDmsContact(id=$id, name=$name, source=$source)"
    }
}

internal fun User.toDmsContact(
    bot: QGBotImpl,
    guildId: String,
    currentMsgId: String? = null,
): QGDmsContactImpl = QGDmsContactImpl(
    bot = bot,
    source = this,
    guildId = guildId,
    currentMsgId = currentMsgId
)
