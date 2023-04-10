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

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import love.forte.simbot.ID
import love.forte.simbot.component.qguild.QGChannelCategory
import love.forte.simbot.component.qguild.QGChannelCategoryId
import love.forte.simbot.component.qguild.QGGuild
import love.forte.simbot.qguild.model.Channel as QGuildChannel


internal class QGChannelCategoryIdImpl(
    override val bot: QGGuildBotImpl,
    private val guildId: ID,
    override val id: ID,
    private val sourceGuild: QGGuild? = null
) : QGChannelCategoryId {

    @Volatile
    private lateinit var _category: QGChannelCategory
    private val initLock = Mutex()

    override suspend fun resolve(): QGChannelCategory {
        return if (::_category.isInitialized) _category else initLock.withLock {
            if (::_category.isInitialized) _category else {
                guild().category(id)?.also {
                    _category = it
                }
                    ?: throw NoSuchElementException("category(id=$id)")
            }
        }
    }

    override suspend fun guild(): QGGuild =
        sourceGuild
            ?: bot.guild(guildId)
            ?: throw NoSuchElementException("guild(id=$guildId)")

    override fun toString(): String {
        return "QGChannelCategoryIdImpl(id=$id, name=$name, guild=$guildId)"
    }
}

internal class QGChannelCategoryImpl(
    override val bot: QGGuildBotImpl,
    override val source: QGuildChannel,
    private val sourceGuild: QGGuild? = null
) : QGChannelCategory {

    override suspend fun guild(): QGGuild =
        sourceGuild
            ?: bot.guild(guildId)
            ?: throw NoSuchElementException("guild(id=$guildId)")

    override suspend fun resolve(): QGChannelCategory = this

    override val id: ID = source.id.ID
    override val ownerId: ID = source.ownerId.ID
    override val guildId: ID = source.guildId.ID


    override fun toString(): String {
        return "QGChannelCategoryImpl(id=$id, name=$name, source=$source)"
    }
}

