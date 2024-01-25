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

import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import love.forte.simbot.ID
import love.forte.simbot.component.qguild.QGBot
import love.forte.simbot.component.qguild.QGGuild
import love.forte.simbot.component.qguild.forum.QGForumChannel
import love.forte.simbot.component.qguild.forum.QGForums
import love.forte.simbot.component.qguild.internal.QGGuildImpl
import love.forte.simbot.component.qguild.internal.newSupervisorCoroutineContext
import love.forte.simbot.qguild.model.ChannelType
import love.forte.simbot.utils.item.Items
import love.forte.simbot.utils.item.effectedItemsByFlow
import kotlin.coroutines.CoroutineContext


/**
 *
 * @author ForteScarlet
 */
internal class QGForumsImpl(
    private val sourceGuild: QGGuildImpl,
) : QGForums {
    override val coroutineContext: CoroutineContext = sourceGuild.newSupervisorCoroutineContext()
    override val bot: QGBot get() = sourceGuild.baseBot

    override suspend fun guild(): QGGuild = sourceGuild

    override val forumChannels: Items<QGForumChannel>
        get() = effectedItemsByFlow {
            sourceGuild.baseBot.channelFlowWithCategoryId(sourceGuild.source.id, sourceGuild)
                .filter { (info, _) ->
                    info.type == ChannelType.FORUM
                }.map { (info, category) ->
                    QGForumChannelImpl(
                        bot = sourceGuild.bot,
                        source = info,
                        sourceGuild = sourceGuild.baseBot.checkIfTransmitCacheable(sourceGuild),
                        category = category
                    )
                }
        }

    override suspend fun forumChannel(id: ID): QGForumChannel? {
        val channel = sourceGuild.channel(id) ?: return null

        return channel.asForumChannel(channel.source)
    }
}
