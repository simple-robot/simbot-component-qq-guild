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

package love.forte.simbot.component.qguild.internal.event

import love.forte.simbot.common.atomic.AtomicInt
import love.forte.simbot.common.atomic.atomic
import love.forte.simbot.common.id.ID
import love.forte.simbot.common.id.StringID.Companion.ID
import love.forte.simbot.component.qguild.event.QGGroupAtMessageCreateEvent
import love.forte.simbot.component.qguild.group.QGGroup
import love.forte.simbot.component.qguild.group.QGGroupMember
import love.forte.simbot.component.qguild.internal.bot.QGBotImpl
import love.forte.simbot.component.qguild.internal.group.QGGroupMemberImpl
import love.forte.simbot.component.qguild.internal.group.toGroup
import love.forte.simbot.component.qguild.internal.message.QGGroupAndC2CMessageContentImpl
import love.forte.simbot.component.qguild.message.QGGroupAndC2CMessageContent
import love.forte.simbot.component.qguild.message.sendGroupMessage
import love.forte.simbot.message.Message
import love.forte.simbot.message.MessageContent
import love.forte.simbot.message.MessageReceipt
import love.forte.simbot.qguild.api.message.GroupAndC2CSendBody
import love.forte.simbot.qguild.api.message.group.GroupMessageSendApi
import love.forte.simbot.qguild.event.GroupAtMessageCreate


/**
 *
 * @author ForteScarlet
 */
internal class QGGroupAtMessageCreateEventImpl(
    override val bot: QGBotImpl,
    override val sourceEventRaw: String,
    override val sourceEventEntity: GroupAtMessageCreate,
    private val eventId: String?,
    private val msgSeq: AtomicInt = atomic(1),
) : QGGroupAtMessageCreateEvent() {
    override val id: ID
        get() = eventId?.ID ?: sourceEventEntity.data.id.ID

    override suspend fun author(): QGGroupMember {
        return QGGroupMemberImpl(
            bot,
            sourceEventEntity.data.author.memberOpenid.ID,
        )
    }

    override val messageContent: QGGroupAndC2CMessageContent = QGGroupAndC2CMessageContentImpl(
        sourceEventEntity.data.id.ID,
        sourceEventEntity.data.content,
        sourceEventEntity.data.attachments
    )

    override suspend fun content(): QGGroup =
        sourceEventEntity.data.toGroup(bot, msgSeq)

    private fun GroupAndC2CSendBody.initRefAndMsgIdAndSeq() {
        if (messageReference == null) {
            messageReference = love.forte.simbot.qguild.model.Message.Reference(
                sourceEventEntity.data.id,
            )
        }
        if (msgId == null) {
            msgId = sourceEventEntity.data.id
        }
        if (msgSeq == null) {
            msgSeq = this@QGGroupAtMessageCreateEventImpl.msgSeq.getAndIncrement()
        }
    }

    override suspend fun reply(text: String): MessageReceipt {
        return bot.sendGroupMessage(
            openid = sourceEventEntity.data.groupOpenid,
            text = text,
            msgType = GroupMessageSendApi.MSG_TYPE_TEXT,
        ) {
            initRefAndMsgIdAndSeq()
        }
    }

    override suspend fun reply(message: Message): MessageReceipt {
        return bot.sendGroupMessage(
            openid = sourceEventEntity.data.groupOpenid,
            message = message,
        ) {
            // post
            initRefAndMsgIdAndSeq()
        }
    }

    override suspend fun reply(messageContent: MessageContent): MessageReceipt {
        return bot.sendGroupMessage(
            openid = sourceEventEntity.data.groupOpenid,
            messageContent = messageContent,
        ) {
            // post
            initRefAndMsgIdAndSeq()
        }
    }
}
