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
import love.forte.simbot.component.qguild.event.*
import love.forte.simbot.component.qguild.forum.QGForumChannel
import love.forte.simbot.component.qguild.internal.QGBotImpl
import love.forte.simbot.component.qguild.internal.forum.asForumChannel
import love.forte.simbot.component.qguild.internal.utils.getValue
import love.forte.simbot.component.qguild.internal.utils.nowTimeMillis
import love.forte.simbot.qguild.event.OpenForumPostData
import love.forte.simbot.qguild.event.OpenForumReplyData
import love.forte.simbot.qguild.event.OpenForumThreadData
import love.forte.simbot.qguild.model.forum.ForumSourceInfo
import love.forte.simbot.randomID


internal suspend fun QGBotImpl.forumChannel(data: ForumSourceInfo): QGForumChannel {
    val channel = channel(data.channelId, null)
        ?: throw NoSuchElementException("channel(id=${data.channelId})")

    return channel.asForumChannel(channel.source)
}

internal suspend fun QGBotImpl.author(data: ForumSourceInfo): QGMember =
    member(data.guildId, data.authorId, null)
        ?: throw NoSuchElementException("member(id=${data.authorId})")

internal class QGOpenForumThreadCreateEventImpl(
    override val bot: QGBotImpl,
    override val eventRaw: String,
    override val sourceEventEntity: OpenForumThreadData,
) : QGOpenForumThreadCreateEvent() {
    override val id: ID = randomID()
    override val timestamp: Timestamp by nowTimeMillis

    override suspend fun channel(): QGForumChannel = bot.forumChannel(sourceEventEntity)
    override suspend fun author(): QGMember = bot.author(sourceEventEntity)
}

internal class QGOpenForumThreadUpdateEventImpl(
    override val bot: QGBotImpl,
    override val eventRaw: String,
    override val sourceEventEntity: OpenForumThreadData,
) : QGOpenForumThreadUpdateEvent() {
    override val id: ID = randomID()
    override val timestamp: Timestamp by nowTimeMillis

    override suspend fun channel(): QGForumChannel = bot.forumChannel(sourceEventEntity)
    override suspend fun author(): QGMember = bot.author(sourceEventEntity)
}

internal class QGOpenForumThreadDeleteEventImpl(
    override val bot: QGBotImpl,
    override val eventRaw: String,
    override val sourceEventEntity: OpenForumThreadData,
) : QGOpenForumThreadDeleteEvent() {
    override val id: ID = randomID()
    override val timestamp: Timestamp by nowTimeMillis

    override suspend fun channel(): QGForumChannel = bot.forumChannel(sourceEventEntity)
    override suspend fun author(): QGMember = bot.author(sourceEventEntity)
}


internal class QGOpenForumPostCreateEventImpl(
    override val bot: QGBotImpl,
    override val eventRaw: String,
    override val sourceEventEntity: OpenForumPostData,
) : QGOpenForumPostCreateEvent() {
    override val id: ID = randomID()
    override val timestamp: Timestamp by nowTimeMillis

    override suspend fun channel(): QGForumChannel = bot.forumChannel(sourceEventEntity)
    override suspend fun author(): QGMember = bot.author(sourceEventEntity)
}

internal class QGOpenForumPostDeleteEventImpl(
    override val bot: QGBotImpl,
    override val eventRaw: String,
    override val sourceEventEntity: OpenForumPostData,
) : QGOpenForumPostDeleteEvent() {
    override val id: ID = randomID()
    override val timestamp: Timestamp by nowTimeMillis

    override suspend fun channel(): QGForumChannel = bot.forumChannel(sourceEventEntity)
    override suspend fun author(): QGMember = bot.author(sourceEventEntity)
}

internal class QGOpenForumReplyCreateEventImpl(
    override val bot: QGBotImpl,
    override val eventRaw: String,
    override val sourceEventEntity: OpenForumReplyData,
) : QGOpenForumReplyCreateEvent() {
    override val id: ID = randomID()
    override val timestamp: Timestamp by nowTimeMillis

    override suspend fun channel(): QGForumChannel = bot.forumChannel(sourceEventEntity)
    override suspend fun author(): QGMember = bot.author(sourceEventEntity)
}

internal class QGOpenForumReplyDeleteEventImpl(
    override val bot: QGBotImpl,
    override val eventRaw: String,
    override val sourceEventEntity: OpenForumReplyData,
) : QGOpenForumReplyDeleteEvent() {
    override val id: ID = randomID()
    override val timestamp: Timestamp by nowTimeMillis

    override suspend fun channel(): QGForumChannel = bot.forumChannel(sourceEventEntity)
    override suspend fun author(): QGMember = bot.author(sourceEventEntity)
}
