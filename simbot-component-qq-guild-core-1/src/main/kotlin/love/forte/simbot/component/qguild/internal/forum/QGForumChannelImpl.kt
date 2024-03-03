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

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import love.forte.simbot.ID
import love.forte.simbot.component.qguild.QGChannel
import love.forte.simbot.component.qguild.QGChannelCategoryId
import love.forte.simbot.component.qguild.QGGuild
import love.forte.simbot.component.qguild.QGMember
import love.forte.simbot.component.qguild.forum.QGForumChannel
import love.forte.simbot.component.qguild.forum.QGThread
import love.forte.simbot.component.qguild.forum.QGThreadCreator
import love.forte.simbot.component.qguild.internal.QGChannelCategoryIdImpl
import love.forte.simbot.component.qguild.internal.QGGuildBotImpl
import love.forte.simbot.component.qguild.internal.newSupervisorCoroutineContext
import love.forte.simbot.component.qguild.util.requestBy
import love.forte.simbot.literal
import love.forte.simbot.qguild.QQGuildApiException
import love.forte.simbot.qguild.api.forum.GetThreadApi
import love.forte.simbot.qguild.api.forum.GetThreadListApi
import love.forte.simbot.qguild.ifNotFoundThenNull
import love.forte.simbot.qguild.model.Channel
import love.forte.simbot.qguild.model.ChannelType
import love.forte.simbot.qguild.model.forum.Thread
import love.forte.simbot.utils.item.Items
import love.forte.simbot.utils.item.effectedItemsByFlow
import kotlin.coroutines.CoroutineContext


/**
 *
 * @author ForteScarlet
 */
internal class QGForumChannelImpl(
    override val bot: QGGuildBotImpl,
    override val source: Channel,
    internal val sourceGuild: QGGuild?,
    override val category: QGChannelCategoryId = QGChannelCategoryIdImpl(
        bot = bot,
        guildId = source.guildId.ID,
        id = source.parentId.ID,
        sourceGuild = sourceGuild,
    ),
) : QGForumChannel {
    override val coroutineContext: CoroutineContext = bot.newSupervisorCoroutineContext()
    override val id: ID = source.id.ID
    override val guildId: ID = source.guildId.ID
    override val ownerId: ID = source.ownerId.ID

    override suspend fun owner(): QGMember =
        guild().member(ownerId) ?: throw NoSuchElementException("owner(id=$ownerId)")

    override suspend fun guild(): QGGuild =
        sourceGuild ?: bot.guild(guildId) ?: throw NoSuchElementException("guild(id=$guildId)")

    private fun threadFlow(): Flow<Thread> = flow {
        GetThreadListApi.create(source.id).requestBy(bot).threads.forEach {
            emit(it)
        }
    }

    override val threads: Items<QGThread>
        get() = effectedItemsByFlow {
            threadFlow().map { it.toQGThread() }
        }

    override suspend fun thread(id: ID): QGThread? {
        return try {
            GetThreadApi.create(source.id, id.literal).requestBy(bot).thread.toQGThread()
        } catch (apiEx: QQGuildApiException) {
            apiEx.ifNotFoundThenNull()
        }
    }

    private fun Thread.toQGThread(): QGThread = QGThreadImpl(bot.bot, this, this@QGForumChannelImpl)

    override fun threadCreator(): QGThreadCreator = QGThreadCreatorImpl(this)

    override fun toString(): String {
        return "QGForumChannelImpl(id=$id, name=$name, category=$category)"
    }
}

/**
 * @throws IllegalStateException Can't as [QGForumChannel]
 */
internal fun QGChannel.asForumChannel(source: Channel = this.source): QGForumChannel =
    this as? QGForumChannel
        ?: throw IllegalStateException("The type of channel(id=${source.id}, name=${source.name}) in guild(id=${source.guildId}) is not category (${ChannelType.CATEGORY}), but ${source.type}")

