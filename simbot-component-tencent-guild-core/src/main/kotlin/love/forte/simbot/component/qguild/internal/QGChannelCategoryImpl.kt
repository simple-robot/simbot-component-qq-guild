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
import love.forte.simbot.component.qguild.QGGuild
import love.forte.simbot.component.qguild.QGGuildBot
import love.forte.simbot.qguild.model.ChannelSubType
import love.forte.simbot.qguild.model.ChannelType
import love.forte.simbot.qguild.model.Channel as QGuildChannel

/**
 *
 * @author ForteScarlet
 */
internal class QGChannelCategoryImpl(
    private val baseBot: QGBotImpl,
    internal val source: QGuildChannel,
    private val _guild: QGGuildImpl,
) : QGChannelCategory {
    override val bot: QGGuildBot get() = _guild.bot
    override val id: ID = source.id.ID
    override val guildId: ID = source.guildId.ID
    override val ownerId: ID = source.ownerId.ID
    override val name: String get() = source.name
    override val channelType: ChannelType get() = source.type
    override val channelSubType: ChannelSubType get() = source.subType
    override val position: Int get() = source.position
    override val parentId: String get() = source.parentId

    override suspend fun guild(): QGGuild = _guild


    internal fun update(channel: QGuildChannel): QGChannelCategoryImpl =
        QGChannelCategoryImpl(baseBot, channel, _guild)
}

