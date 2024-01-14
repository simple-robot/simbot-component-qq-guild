/*
 * Copyright (c) 2023-2024. ForteScarlet.
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
import love.forte.simbot.common.time.Timestamp
import love.forte.simbot.component.qguild.QGGuild
import love.forte.simbot.component.qguild.QGMember
import love.forte.simbot.component.qguild.channel.QGForumChannel
import love.forte.simbot.component.qguild.forum.QGReply
import love.forte.simbot.component.qguild.forum.QGThread
import love.forte.simbot.component.qguild.internal.QGGuildImpl
import love.forte.simbot.component.qguild.internal.bot.QGBotImpl
import love.forte.simbot.component.qguild.internal.channel.QGForumChannelImpl
import love.forte.simbot.component.qguild.internal.channel.asForumChannel
import love.forte.simbot.component.qguild.internal.guild.QGGuildImpl
import love.forte.simbot.component.qguild.internal.toTimestamp
import love.forte.simbot.component.qguild.util.requestBy
import love.forte.simbot.qguild.QQGuildApiException
import love.forte.simbot.qguild.api.forum.GetThreadApi
import love.forte.simbot.qguild.ifNotFoundThenNoSuch
import love.forte.simbot.qguild.model.forum.Reply
import love.forte.simbot.qguild.stdlib.requestBy

/**
 *
 * @author ForteScarlet
 */
internal class QGReplyImpl(
    override val bot: QGBotImpl,
    override val source: Reply,
    private val sourceChannel: QGForumChannelImpl?
) : QGReply {
    @OptIn(ExperimentalSimbotApi::class)
    override val datetime: Timestamp = source.replyInfo.dateTime.toTimestamp()

    override suspend fun guild(): QGGuild =
        sourceChannel?.guild()
            ?: bot.queryGuild(source.guildId)
            ?: throw NoSuchElementException("guild(id=${source.guildId})")


    override suspend fun channel(): QGForumChannel =
        sourceChannel
            ?: (bot.channel(source.channelId, null)
                ?: throw NoSuchElementException("channel(id=${source.channelId})")).asForumChannel()


    override suspend fun author(): QGMember =
        bot.member(source.guildId, source.authorId, sourceChannel?.sourceGuild as? QGGuildImpl) ?: throw NoSuchElementException("author(id=$authorId)")

    override suspend fun thread(): QGThread {
        val (thread) = try {
            GetThreadApi.create(source.channelId, source.replyInfo.threadId).requestBy(bot)
        } catch (apiEx: QQGuildApiException) {
            apiEx.ifNotFoundThenNoSuch { "thread(id=${source.replyInfo.threadId}) by reply(id=${source.replyInfo.replyId})" }
        }

        return QGThreadImpl(bot, thread, sourceChannel)
    }
}
