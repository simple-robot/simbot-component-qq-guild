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

package love.forte.simbot.component.qguild.internal.channel

import love.forte.simbot.common.id.ID
import love.forte.simbot.common.id.StringID.Companion.ID
import love.forte.simbot.component.qguild.channel.QGCategory
import love.forte.simbot.component.qguild.channel.QGNonTextChannel
import love.forte.simbot.component.qguild.guild.QGGuild
import love.forte.simbot.component.qguild.internal.bot.QGBotImpl
import love.forte.simbot.component.qguild.internal.bot.newSupervisorCoroutineContext
import love.forte.simbot.qguild.model.Channel
import kotlin.coroutines.CoroutineContext


/**
 * [QGNonTextChannel] 基本实现，用于包装那些非特殊实现的非文字子频道类型。
 *
 * [QGNonTextChannelImpl] 类型作为 [QGNonTextChannel] 实现的默认替补，且不对外公开。
 *
 * @author ForteScarlet
 */
internal class QGNonTextChannelImpl(
    private val bot: QGBotImpl,
    override val source: Channel,
    private val sourceGuild: QGGuild? = null,
    override val category: QGCategory = QGCategoryImpl(
        bot = bot,
        guildId = source.guildId.ID,
        id = source.parentId.ID,
        sourceGuild = sourceGuild,
    ),
) : QGNonTextChannel {
    override val coroutineContext: CoroutineContext = bot.newSupervisorCoroutineContext()
    override val id: ID = source.id.ID
//    private val guildId: ID get() = source.guildId.ID
//    private val ownerId: ID get() = source.ownerId.ID

//    private suspend fun owner(): QGMember = guild().member(ownerId) ?: throw NoSuchElementException("owner(id=$ownerId)")
//
//    private suspend fun guild(): QGGuild =
//        sourceGuild
//            ?: bot.guildRelation.guild(guildId)
//            ?: throw NoSuchElementException("guild(id=$guildId)")

    override fun toString(): String {
        return "QGNonTextChannel(id=$id, name=$name, category=$category)"
    }
}
