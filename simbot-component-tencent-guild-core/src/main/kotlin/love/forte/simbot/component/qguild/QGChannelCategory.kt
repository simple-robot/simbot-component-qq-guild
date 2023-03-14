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

package love.forte.simbot.component.qguild

import love.forte.plugin.suspendtrans.annotation.JvmAsync
import love.forte.plugin.suspendtrans.annotation.JvmBlocking
import love.forte.simbot.ID
import love.forte.simbot.definition.BotContainer
import love.forte.simbot.definition.Category
import love.forte.simbot.definition.GuildInfoContainer
import love.forte.simbot.qguild.model.ChannelSubType
import love.forte.simbot.qguild.model.ChannelType
import love.forte.simbot.qguild.model.Channel as QGuildChannel

/**
 *
 * 当一个频道的 [TencentChannel.source.channelType][QGuildChannel.type] 的值等于 [ChannelType.CATEGORY] 时，
 * 此频道代表为一个分组。
 *
 * @author ForteScarlet
 */
public interface QGChannelCategory : Category, BotContainer, GuildInfoContainer {
    override val bot: QGGuildBot

    override val id: ID
    override val name: String

    //// just like model Channel

    public val guildId: ID
    public val ownerId: ID
    public val channelType: ChannelType
    public val channelSubType: ChannelSubType
    public val position: Int
    public val parentId: String

    /**
     * 获取此分类所属的频道服务器。
     */
    @JvmBlocking(asProperty = true, suffix = "")
    @JvmAsync(asProperty = true)
    override suspend fun guild(): QGGuild
}
