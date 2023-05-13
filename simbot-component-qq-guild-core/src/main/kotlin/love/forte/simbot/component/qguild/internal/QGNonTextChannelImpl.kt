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

package love.forte.simbot.component.qguild.internal

import love.forte.simbot.ID
import love.forte.simbot.component.qguild.QGChannelCategoryId
import love.forte.simbot.component.qguild.QGGuild
import love.forte.simbot.component.qguild.QGMember
import love.forte.simbot.component.qguild.QGNonTextChannel
import love.forte.simbot.qguild.InternalApi
import love.forte.simbot.qguild.model.Channel
import kotlin.coroutines.CoroutineContext


/**
 * [QGNonTextChannel] 基本实现，用于包装那些非特殊实现的非文字子频道类型。
 *
 * [QGNonTextChannelImpl] 类型作为 [QGNonTextChannel] 实现的默认替补，且不对外公开。
 *
 * @author ForteScarlet
 */
@InternalApi
internal class QGNonTextChannelImpl(
    override val bot: QGGuildBotImpl,
    override val source: Channel,
    private val sourceGuild: QGGuild? = null,
    override val category: QGChannelCategoryId = QGChannelCategoryIdImpl(
        bot = bot,
        guildId = source.guildId.ID,
        id = source.parentId.ID,
        sourceGuild = sourceGuild,
    ),
) : QGNonTextChannel {
    override val coroutineContext: CoroutineContext = bot.newSupervisorCoroutineContext()
    override val id: ID = source.id.ID
    override val guildId: ID = source.guildId.ID
    override val ownerId: ID = source.ownerId.ID

    override suspend fun owner(): QGMember = guild().member(ownerId) ?: throw NoSuchElementException("owner(id=$ownerId)")

    override suspend fun guild(): QGGuild =
        sourceGuild
            ?: bot.guild(guildId)
            ?: throw NoSuchElementException("guild(id=$guildId)")

    override fun toString(): String {
        return "QGNonTextChannelImpl(id=$id, name=$name, category=$category)"
    }
}
