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
import love.forte.simbot.component.qguild.QGMember
import love.forte.simbot.component.qguild.event.QGForumThreadCreateEvent
import love.forte.simbot.component.qguild.event.QGForumThreadDeleteEvent
import love.forte.simbot.component.qguild.event.QGForumThreadUpdateEvent
import love.forte.simbot.component.qguild.forum.QGForumChannel
import love.forte.simbot.component.qguild.forum.QGThread
import love.forte.simbot.component.qguild.internal.QGBotImpl
import love.forte.simbot.component.qguild.internal.forum.QGThreadImpl
import love.forte.simbot.qguild.model.forum.Thread
import love.forte.simbot.randomID


internal class QGForumThreadCreateEventImpl(
    override val bot: QGBotImpl,
    override val eventRaw: String,
    override val sourceEventEntity: Thread
) : QGForumThreadCreateEvent() {
    override val id: ID = randomID()
    override val timestamp: Timestamp = Timestamp.now()
    override val guildId: ID = sourceEventEntity.guildId.ID
    override val channelId: ID = sourceEventEntity.channelId.ID
    override val authorId: ID = sourceEventEntity.authorId.ID

    override suspend fun channel(): QGForumChannel =
        bot.forumChannel(sourceEventEntity)

    override suspend fun author(): QGMember =
        bot.author(sourceEventEntity)

    override val thread: QGThread = QGThreadImpl(bot, sourceEventEntity, null)
}


internal class QGForumThreadUpdateEventImpl(
    override val bot: QGBotImpl,
    override val eventRaw: String,
    override val sourceEventEntity: Thread
) : QGForumThreadUpdateEvent() {
    override val id: ID = randomID()
    override val timestamp: Timestamp = Timestamp.now()
    override val guildId: ID = sourceEventEntity.guildId.ID
    override val channelId: ID = sourceEventEntity.channelId.ID
    override val authorId: ID = sourceEventEntity.authorId.ID

    override suspend fun channel(): QGForumChannel =
        bot.forumChannel(sourceEventEntity)

    override suspend fun author(): QGMember =
        bot.author(sourceEventEntity)

    override val thread: QGThread = QGThreadImpl(bot, sourceEventEntity, null)
}


internal class QGForumThreadDeleteEventImpl(
    override val bot: QGBotImpl,
    override val eventRaw: String,
    override val sourceEventEntity: Thread
) : QGForumThreadDeleteEvent() {
    override val id: ID = randomID()
    override val timestamp: Timestamp = Timestamp.now()
    override val guildId: ID = sourceEventEntity.guildId.ID
    override val channelId: ID = sourceEventEntity.channelId.ID
    override val authorId: ID = sourceEventEntity.authorId.ID

    override suspend fun channel(): QGForumChannel =
        bot.forumChannel(sourceEventEntity)

    override suspend fun author(): QGMember =
        bot.author(sourceEventEntity)

    override val thread: QGThread = QGThreadImpl(bot, sourceEventEntity, null)
}

