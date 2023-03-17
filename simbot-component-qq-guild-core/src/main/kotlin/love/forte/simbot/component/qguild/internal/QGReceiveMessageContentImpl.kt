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
import love.forte.simbot.component.qguild.message.MessageParsers
import love.forte.simbot.component.qguild.message.QGReceiveMessageContent
import love.forte.simbot.message.Messages
import love.forte.simbot.qguild.model.Message

/**
 *
 * @author ForteScarlet
 */
internal class QGReceiveMessageContentImpl(override val sourceMessage: Message) : QGReceiveMessageContent() {

    override val messageId: ID = sourceMessage.id.ID

    override val messages: Messages by lazy(LazyThreadSafetyMode.PUBLICATION) { MessageParsers.parse(sourceMessage).messages }

    override val plainText: String by lazy(LazyThreadSafetyMode.PUBLICATION) {
        var content = sourceMessage.content
        // TODO pref it
        //  <@user_id> 或者 <@!user_id>
        //  @everyone
        //  <#channel_id>
        //  <emoji:id>

        for (mention in sourceMessage.mentions) {
            val target = "<@!${mention.id}>"
            content = content.replaceFirst(target, "")
        }

        if (sourceMessage.mentionEveryone) {
            content = content.replaceFirst("@everyone", "")
        }

        // TODO 解析 #子频道 标签

        content
    }

    override fun toString(): String {
        return "QGReceiveMessageContentImpl(messageId=$messageId, sourceMessage=$sourceMessage)"
    }


    override fun equals(other: Any?): Boolean {
        if (other !is QGReceiveMessageContent) return false
        if (other === this) return true
        return messageId == other.messageId
    }

    override fun hashCode(): Int = messageId.hashCode()
}
