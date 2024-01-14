/*
 * Copyright (c) 2022-2024. ForteScarlet.
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

package love.forte.simbot.component.qguild.internal.channel

import love.forte.simbot.common.id.ID
import love.forte.simbot.common.id.StringID.Companion.ID
import love.forte.simbot.component.qguild.channel.QGCategory
import love.forte.simbot.component.qguild.channel.QGTextChannel
import love.forte.simbot.component.qguild.guild.QGGuild
import love.forte.simbot.component.qguild.internal.bot.QGBotImpl
import love.forte.simbot.component.qguild.internal.bot.newSupervisorCoroutineContext
import love.forte.simbot.component.qguild.message.QGMessageReceipt
import love.forte.simbot.component.qguild.message.sendMessage
import love.forte.simbot.message.Message
import love.forte.simbot.message.MessageContent
import love.forte.simbot.qguild.QQGuildApiException
import love.forte.simbot.qguild.addStackTrace
import kotlin.coroutines.CoroutineContext
import love.forte.simbot.qguild.model.Channel as QGSourceChannel

/**
 *
 * @author ForteScarlet
 */
internal class QGTextChannelImpl internal constructor(
    private val bot: QGBotImpl,
    override val source: QGSourceChannel,
    private val sourceGuild: QGGuild? = null,
    override val category: QGCategory = QGCategoryImpl(
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
//    private val guildId: ID get() = source.guildId.ID
//    private val ownerId: ID get() = source.ownerId.ID

//    private suspend fun guild(): QGGuild =
//        sourceGuild
//            ?: bot.guildRelation.guild(guildId)
//            ?: throw NoSuchElementException("guild(id=$guildId)")
//
//    private suspend fun owner(): QGMember =
//        guild().member(ownerId) ?: throw NoSuchElementException("owner(id=$ownerId)")

    override suspend fun send(message: Message): QGMessageReceipt {
        return try {
            bot.sendMessage(source.id, message) {
                if (msgId == null) {
                    msgId = currentMsgId
                }
            }
        } catch (e: QQGuildApiException) {
            throw e.addStackTrace { "channel.send" }
        }
    }

    override suspend fun send(messageContent: MessageContent): QGMessageReceipt {
        return try {
            bot.sendMessage(source.id, messageContent) {
                if (msgId == null) {
                    msgId = currentMsgId
                }
            }
        } catch (e: QQGuildApiException) {
            throw e.addStackTrace { "channel.send" }
        }
    }

    override suspend fun send(text: String): QGMessageReceipt {
        return try {
            bot.sendMessage(source.id, text) {
                if (msgId == null) {
                    msgId = currentMsgId
                }
            }
        } catch (e: QQGuildApiException) {
            throw e.addStackTrace { "channel.send" }
        }
    }

    override fun toString(): String {
        return "QGTextChannel(id=$id, name=$name, category=$category)"
    }
}




