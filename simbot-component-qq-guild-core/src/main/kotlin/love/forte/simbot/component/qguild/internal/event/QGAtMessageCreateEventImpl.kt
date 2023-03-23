/*
 * Copyright (c) 2023. ForteScarlet.
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

import love.forte.simbot.ID
import love.forte.simbot.Timestamp
import love.forte.simbot.component.qguild.event.QGAtMessageCreateEvent
import love.forte.simbot.component.qguild.internal.*
import love.forte.simbot.component.qguild.message.MessageParsers
import love.forte.simbot.component.qguild.message.QGMessageReceipt
import love.forte.simbot.component.qguild.message.QGReceiveMessageContent
import love.forte.simbot.component.qguild.util.requestBy
import love.forte.simbot.message.MessageContent
import love.forte.simbot.message.MessageReceipt
import love.forte.simbot.qguild.QQGuildApiException
import love.forte.simbot.qguild.api.channel.GetChannelApi
import love.forte.simbot.qguild.api.member.GetMemberApi
import love.forte.simbot.qguild.api.message.MessageSendApi
import love.forte.simbot.qguild.ifNotFoundThenNoSuch
import love.forte.simbot.qguild.message.ContentTextEncoder
import love.forte.simbot.qguild.model.Message


/**
 *
 * @author ForteScarlet
 */
internal class QGAtMessageCreateEventImpl(
    override val bot: QGBotImpl,
    override val eventRaw: String,
    override val sourceEventEntity: Message
) : QGAtMessageCreateEvent() {
    override val id: ID =
        "${sourceEventEntity.guildId}.${sourceEventEntity.channelId}.${sourceEventEntity.id}.${sourceEventEntity.timestamp.epochSeconds}".ID

    override val timestamp: Timestamp = sourceEventEntity.timestamp.toTimestamp()

    override val messageContent: QGReceiveMessageContentImpl = QGReceiveMessageContentImpl(sourceEventEntity)


    override suspend fun reply(message: love.forte.simbot.message.Message): QGMessageReceipt {
        val bodyBuilder = MessageParsers.parse(message) {
            if (msgId == null) {
                msgId = sourceEventEntity.id
            }
        }

        return reply0(bodyBuilder.build())
    }

    override suspend fun reply(message: MessageContent): MessageReceipt {
        if (message is QGReceiveMessageContent) {
            val body = MessageSendApi.Body {
                fromMessage(message.sourceMessage)
            }
            return reply0(body)
        }

        return reply(message.messages)
    }

    override suspend fun reply(text: String): MessageReceipt {
        return reply0(MessageSendApi.Body {
            content = ContentTextEncoder.encode(text) // 转义，不允许特殊字符
        })
    }

    private suspend fun reply0(body: MessageSendApi.Body): QGMessageReceipt =
        MessageSendApi.create(sourceEventEntity.channelId, body).requestBy(bot).asReceipt()

    override suspend fun author(): QGMemberImpl {
        val member =
            try {
                GetMemberApi.create(sourceEventEntity.guildId, sourceEventEntity.author.id).requestBy(bot)
            } catch (apiEx: QQGuildApiException) {
                apiEx.ifNotFoundThenNoSuch { "member(id=${sourceEventEntity.author.id})" }
            }

        return QGMemberImpl(bot, member, sourceEventEntity.guildId.ID)
    }

    override suspend fun channel(): QGChannelImpl {
        val channel =
            try {
                GetChannelApi.create(sourceEventEntity.channelId).requestBy(bot)
            } catch (apiEx: QQGuildApiException) {
                apiEx.ifNotFoundThenNoSuch { "channel(id=${sourceEventEntity.channelId})" }
            }

        return QGChannelImpl(bot.inGuild(sourceEventEntity.guildId), channel, currentMsgId = sourceEventEntity.id)
    }
}
