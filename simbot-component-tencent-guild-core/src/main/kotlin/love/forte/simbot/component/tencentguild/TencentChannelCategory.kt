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

package love.forte.simbot.component.tencentguild

import love.forte.plugin.suspendtrans.annotation.JvmAsync
import love.forte.plugin.suspendtrans.annotation.JvmBlocking
import love.forte.simbot.definition.BotContainer
import love.forte.simbot.definition.Category
import love.forte.simbot.definition.GuildInfoContainer
import love.forte.simbot.qguild.CHANNEL_TYPE_GROUPING
import love.forte.simbot.qguild.TencentChannelInfo

/**
 *
 * 当一个频道的 [TencentChannel.source.channelType][TencentChannelInfo.channelType] 的值等于 [CHANNEL_TYPE_GROUPING] 时，
 * 此频道代表为一个分组。
 *
 * @author ForteScarlet
 */
public interface TencentChannelCategory : Category, TencentChannelInfo, BotContainer, GuildInfoContainer {
    override val bot: TencentGuildComponentGuildBot
    
    /**
     * 获取此分类所属的频道服务器。
     */
    @JvmBlocking(asProperty = true, suffix = "")
    @JvmAsync(asProperty = true)
    override suspend fun guild(): TencentGuild
}
