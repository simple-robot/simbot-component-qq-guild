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

package love.forte.simbot.component.tencentguild.internal

import love.forte.simbot.ID
import love.forte.simbot.component.tencentguild.TencentChannelCategory
import love.forte.simbot.component.tencentguild.TencentGuild
import love.forte.simbot.component.tencentguild.TencentGuildComponentGuildBot
import love.forte.simbot.qguild.ChannelSubType
import love.forte.simbot.qguild.ChannelType
import love.forte.simbot.qguild.TencentChannelInfo
import love.forte.simbot.qguild.model.ChannelSubType
import love.forte.simbot.qguild.model.ChannelType

/**
 *
 * @author ForteScarlet
 */
internal class TencentChannelCategoryImpl(
    private val baseBot: TencentGuildComponentBotImpl,
    private val _guild: TencentGuildImpl,
    @Volatile internal var channel: TencentChannelInfo,
) : TencentChannelCategory {
    override val bot: TencentGuildComponentGuildBot
        get() = _guild.bot
    override val id: ID
        get() = channel.id
    override val name: String
        get() = channel.name
    override val guildId: ID
        get() = channel.guildId
    override val channelType: ChannelType
        get() = channel.channelType
    override val channelSubType: ChannelSubType
        get() = channel.channelSubType
    override val position: Int
        get() = channel.position
    override val parentId: String
        get() = channel.parentId
    override val ownerId: ID
        get() = channel.ownerId
    
    override suspend fun guild(): TencentGuild = _guild
}
