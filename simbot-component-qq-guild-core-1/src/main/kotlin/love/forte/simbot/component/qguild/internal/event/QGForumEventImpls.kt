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
import love.forte.simbot.component.qguild.forum.QGPost
import love.forte.simbot.component.qguild.forum.QGReply
import love.forte.simbot.component.qguild.forum.QGThread
import love.forte.simbot.component.qguild.internal.QGBotImpl
import love.forte.simbot.component.qguild.internal.forum.QGPostImpl
import love.forte.simbot.component.qguild.internal.forum.QGReplyImpl
import love.forte.simbot.component.qguild.internal.forum.QGThreadImpl
import love.forte.simbot.delegate.getValue
import love.forte.simbot.delegate.stringID
import love.forte.simbot.delegate.timestamp
import love.forte.simbot.qguild.model.forum.AuditResult
import love.forte.simbot.qguild.model.forum.Post
import love.forte.simbot.qguild.model.forum.Reply
import love.forte.simbot.qguild.model.forum.Thread

internal class QGForumThreadCreateEventImpl(
    override val bot: QGBotImpl,
    override val eventRaw: String,
    override val sourceEventEntity: Thread
) : QGForumThreadCreateEvent() {
    override val id: ID by stringID { random }
    override val timestamp: Timestamp by timestamp { now }

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
    override val id: ID by stringID { random }
    override val timestamp: Timestamp by timestamp { now }

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
    override val id: ID by stringID { random }
    override val timestamp: Timestamp by timestamp { now }

    override suspend fun channel(): QGForumChannel =
        bot.forumChannel(sourceEventEntity)

    override suspend fun author(): QGMember =
        bot.author(sourceEventEntity)

    override val thread: QGThread = QGThreadImpl(bot, sourceEventEntity, null)
}


internal class QGForumPostCreateEventImpl(
    override val bot: QGBotImpl,
    override val eventRaw: String,
    override val sourceEventEntity: Post
) : QGForumPostCreateEvent() {
    override val id: ID by stringID { random }
    override val timestamp: Timestamp by timestamp { now }

    override suspend fun channel(): QGForumChannel =
        bot.forumChannel(sourceEventEntity)

    override suspend fun author(): QGMember =
        bot.author(sourceEventEntity)

    override val post: QGPost = QGPostImpl(bot, sourceEventEntity, null)
}

internal class QGForumPostDeleteEventImpl(
    override val bot: QGBotImpl,
    override val eventRaw: String,
    override val sourceEventEntity: Post
) : QGForumPostDeleteEvent() {
    override val id: ID by stringID { random }
    override val timestamp: Timestamp by timestamp { now }

    override suspend fun channel(): QGForumChannel =
        bot.forumChannel(sourceEventEntity)

    override suspend fun author(): QGMember =
        bot.author(sourceEventEntity)

    override val post: QGPost = QGPostImpl(bot, sourceEventEntity, null)
}

internal class QGForumReplyCreateEventImpl(
    override val bot: QGBotImpl,
    override val eventRaw: String,
    override val sourceEventEntity: Reply
) : QGForumReplyCreateEvent() {
    override val id: ID by stringID { random }
    override val timestamp: Timestamp by timestamp { now }

    override suspend fun channel(): QGForumChannel =
        bot.forumChannel(sourceEventEntity)

    override suspend fun author(): QGMember =
        bot.author(sourceEventEntity)

    override val post: QGReply = QGReplyImpl(bot, sourceEventEntity, null)
}

internal class QGForumReplyDeleteEventImpl(
    override val bot: QGBotImpl,
    override val eventRaw: String,
    override val sourceEventEntity: Reply
) : QGForumReplyDeleteEvent() {
    override val id: ID by stringID { random }
    override val timestamp: Timestamp by timestamp { now }

    override suspend fun channel(): QGForumChannel =
        bot.forumChannel(sourceEventEntity)

    override suspend fun author(): QGMember =
        bot.author(sourceEventEntity)

    override val post: QGReply = QGReplyImpl(bot, sourceEventEntity, null)
}

internal class QGForumPublishAuditResultEventImpl(
    override val bot: QGBotImpl,
    override val eventRaw: String,
    override val sourceEventEntity: AuditResult,
) : QGForumPublishAuditResultEvent() {
    override val id: ID by stringID { random }
    override val timestamp: Timestamp by timestamp { now }

    override suspend fun channel(): QGForumChannel =
        bot.forumChannel(sourceEventEntity)

    override suspend fun author(): QGMember =
        bot.author(sourceEventEntity)
}
