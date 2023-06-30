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
import love.forte.simbot.component.qguild.QGChannelCategoryId
import love.forte.simbot.component.qguild.QGGuild
import love.forte.simbot.component.qguild.QGMember
import love.forte.simbot.component.qguild.QGTextChannel
import love.forte.simbot.component.qguild.message.QGMessageReceipt
import love.forte.simbot.component.qguild.message.sendMessage
import love.forte.simbot.message.Message
import love.forte.simbot.message.MessageContent
import love.forte.simbot.qguild.InternalApi
import love.forte.simbot.qguild.QQGuildApiException
import love.forte.simbot.qguild.addStackTrace
import kotlin.coroutines.CoroutineContext
import love.forte.simbot.qguild.model.Channel as QGSourceChannel

/**
 *
 * @author ForteScarlet
 */
internal class QGTextChannelImpl internal constructor(
    override val bot: QGGuildBotImpl,
    override val source: QGSourceChannel,
    private val sourceGuild: QGGuild? = null,
    override val category: QGChannelCategoryId = QGChannelCategoryIdImpl(
        bot = bot,
        guildId = source.guildId.ID,
        id = source.parentId.ID,
        sourceGuild = sourceGuild,
    ),
    /**
     * 如果是从一个事件而来，提供可用于消息回复的 msgId 来避免 event.channel().send(...) 出现问题
     */
    private val currentMsgId: String? = null,
) : QGTextChannel {
    override val coroutineContext: CoroutineContext = bot.newSupervisorCoroutineContext()
    override val id: ID = source.id.ID
    override val guildId: ID = source.guildId.ID
    override val ownerId: ID = source.ownerId.ID

    override suspend fun guild(): QGGuild =
        sourceGuild
            ?: bot.guild(guildId)
            ?: throw NoSuchElementException("guild(id=$guildId)")

    override suspend fun owner(): QGMember =
        guild().member(ownerId) ?: throw NoSuchElementException("owner(id=$ownerId)")

    @OptIn(InternalApi::class)
    override suspend fun send(message: Message): QGMessageReceipt {
        return try {
            bot.sendMessage(source.id, message) {
                if (msgId == null) {
                    msgId = currentMsgId
                }
            }
        } catch (e: QQGuildApiException) {
            throw e.addStackTrace("channel.send")
        }
    }

    @OptIn(InternalApi::class)
    override suspend fun send(message: MessageContent): QGMessageReceipt {
        return try {
            bot.sendMessage(source.id, message) {
                if (msgId == null) {
                    msgId = currentMsgId
                }
            }
        } catch (e: QQGuildApiException) {
            throw e.addStackTrace("channel.send")
        }
    }

    @OptIn(InternalApi::class)
    override suspend fun send(text: String): QGMessageReceipt {
        return try {
            bot.sendMessage(source.id, text) {
                if (msgId == null) {
                    msgId = currentMsgId
                }
            }
        } catch (e: QQGuildApiException) {
            throw e.addStackTrace("channel.send")
        }
    }

    override fun toString(): String {
        return "QGTextChannelImpl(id=$id, name=$name, category=$category)"
    }
}




