/*
 * Copyright (c) 2022-2023. ForteScarlet.
 *
 * This file is part of simbot-component-tencent-guild.
 *
 * simbot-component-tencent-guild is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * simbot-component-tencent-guild is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with simbot-component-tencent-guild. If not, see <https://www.gnu.org/licenses/>.
 */

package love.forte.simbot.component.qguild.internal

import love.forte.simbot.ID
import love.forte.simbot.component.qguild.QGChannelCategory
import love.forte.simbot.component.qguild.QGChannelCategoryId
import love.forte.simbot.component.qguild.QGGuild
import love.forte.simbot.qguild.model.Channel as QGuildChannel


internal open class QGChannelCategoryIdImpl(
    private val _guild: QGGuildImpl,
    override val id: ID,
) : QGChannelCategoryId {
    override val bot: QGGuildBotImpl
        get() = _guild.bot

    override suspend fun category(): QGChannelCategory {
        return _guild.category(id)
            ?: throw NoSuchElementException("Category(id=$id)")
    }

    override suspend fun guild(): QGGuild = _guild

    override fun toString(): String {
        return "QGChannelCategoryIdImpl(id=$id, name=$name, guild=$_guild)"
    }
}


/**
 *
 * @author ForteScarlet
 */
internal class QGChannelCategoryImpl private constructor(
    internal val source: QGuildChannel,
    private val _guild: QGGuildImpl,
    override val id: ID,
    override val guildId: ID,
    override val ownerId: ID,
) : QGChannelCategoryIdImpl(_guild, id), QGChannelCategory {
    internal constructor(
        source: QGuildChannel,
        guild: QGGuildImpl,
    ) : this(
        source, guild,
        source.id.ID,
        source.guildId.ID,
        source.ownerId.ID,
    )

    override val name: String get() = source.name
    override val position: Int get() = source.position

    override fun toString(): String {
        return "QGChannelCategoryImpl(id=$id, name=$name, source=$source, guild=$_guild)"
    }

//    internal fun update(newChannel: QGuildChannel): QGChannelCategoryImpl =
//        QGChannelCategoryImpl(baseBot, newChannel, _guild, id, guildId, ownerId)

}

