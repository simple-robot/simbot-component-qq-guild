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

package love.forte.simbot.component.qguild.internal.forum

import love.forte.simbot.ExperimentalSimbotApi
import love.forte.simbot.Timestamp
import love.forte.simbot.component.qguild.QGBot
import love.forte.simbot.component.qguild.QGGuild
import love.forte.simbot.component.qguild.QGMember
import love.forte.simbot.component.qguild.forum.QGForumChannel
import love.forte.simbot.component.qguild.forum.QGThread
import love.forte.simbot.component.qguild.internal.newSupervisorCoroutineContext
import love.forte.simbot.component.qguild.internal.toTimestamp
import love.forte.simbot.component.qguild.util.requestBy
import love.forte.simbot.qguild.QQGuildApiException
import love.forte.simbot.qguild.api.forum.DeleteThreadApi
import love.forte.simbot.qguild.ifNotFoundThen
import love.forte.simbot.qguild.model.forum.Thread
import kotlin.coroutines.CoroutineContext


/**
 *
 * @author ForteScarlet
 */
internal class QGThreadImpl(
    override val bot: QGBot,
    private val sourceChannel: QGForumChannel,
    override val source: Thread
) : QGThread {
    override val coroutineContext: CoroutineContext = sourceChannel.newSupervisorCoroutineContext()

    override suspend fun guild(): QGGuild = sourceChannel.guild()

    override suspend fun author(): QGMember =
        guild().member(authorId) ?: throw NoSuchElementException("author(id=$authorId)")

    @OptIn(ExperimentalSimbotApi::class)
    override val dateTime: Timestamp
        get() = source.threadInfo.dateTime.toTimestamp()

    override suspend fun channel(): QGForumChannel = sourceChannel

    override suspend fun delete(): Boolean {
        return try {
            DeleteThreadApi.create(source.channelId, source.threadInfo.threadId).requestBy(bot)
            true
        } catch (apiEx: QQGuildApiException) {
            apiEx.ifNotFoundThen { false }
        }
    }

    override fun toString(): String {
        return "QGThreadImpl(id=${source.threadInfo.threadId}, title=$title, channelId=${source.channelId})"
    }
}
