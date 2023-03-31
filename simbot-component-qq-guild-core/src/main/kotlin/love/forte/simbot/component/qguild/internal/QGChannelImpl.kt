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
import love.forte.simbot.component.qguild.QGChannel
import love.forte.simbot.component.qguild.QGChannelCategoryId
import love.forte.simbot.component.qguild.QGGuild
import love.forte.simbot.component.qguild.QGMember
import love.forte.simbot.component.qguild.message.QGMessageReceipt
import love.forte.simbot.component.qguild.message.sendMessage
import love.forte.simbot.message.Message
import love.forte.simbot.message.MessageContent
import kotlin.coroutines.CoroutineContext
import love.forte.simbot.qguild.model.Channel as QGSourceChannel

/**
 *
 * @author ForteScarlet
 */
internal class QGChannelImpl internal constructor(
    override val bot: QGGuildBotImpl,
    override val source: QGSourceChannel,
    override val category: QGChannelCategoryId = QGChannelCategoryIdImpl(
        bot,
        source.guildId.ID,
        source.parentId.ID
    ),
    /**
     * 如果是从一个事件而来，提供可用于消息回复的 msgId 来避免 event.channel().send(...) 出现问题
     */
    private val currentMsgId: String? = null
) : QGChannel {
    override val coroutineContext: CoroutineContext = bot.newSupervisorCoroutineContext()
    override val id: ID = source.id.ID
    override val guildId: ID = source.guildId.ID


    override suspend fun guild(): QGGuild =
        bot.guild(guildId)?.also { it.currentMsgId = currentMsgId }
            ?: throw NoSuchElementException("guild(id=$guildId)")

    override suspend fun owner(): QGMember = guild().owner()

    override suspend fun send(message: Message): QGMessageReceipt {
        return bot.sendMessage(source.id, message) {
            if (msgId == null) {
                msgId = currentMsgId
            }
        }
    }

    override suspend fun send(message: MessageContent): QGMessageReceipt {
        return bot.sendMessage(source.id, message) {
            if (msgId == null) {
                msgId = currentMsgId
            }
        }
    }

    override suspend fun send(text: String): QGMessageReceipt {
        return bot.sendMessage(source.id, text) {
            if (msgId == null) {
                msgId = currentMsgId
            }
        }
    }

    override fun toString(): String {
        return "QGChannelImpl(id=$id, name=$name, source=$source)"
    }
}




