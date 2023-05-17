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

package love.forte.simbot.component.qguild.forum

import kotlinx.coroutines.CoroutineScope
import love.forte.simbot.ID
import love.forte.simbot.JST
import love.forte.simbot.component.qguild.JSTP
import love.forte.simbot.component.qguild.QGBot
import love.forte.simbot.component.qguild.QGGuild
import love.forte.simbot.definition.BotContainer
import love.forte.simbot.definition.GuildInfoContainer
import love.forte.simbot.qguild.QQGuildApiException
import love.forte.simbot.qguild.model.ChannelType
import love.forte.simbot.utils.item.Items


/**
 *
 * QQ频道中对帖子子频道的操作器。
 *
 * @author ForteScarlet
 */
public interface QGForums : CoroutineScope, BotContainer, GuildInfoContainer {

    /**
     * 所属 [QGBot].
     */
    override val bot: QGBot

    /**
     * 所属 [QGGuild].
     */
    @JSTP
    override suspend fun guild(): QGGuild

    /**
     * 得到当前 guild 中的所有 **帖子类型** [ChannelType.FORUM] 的子频道实例。
     *
     * 得到的数据集是 [QGGuild.channels] 的子集。
     *
     * @see QGForumChannel
     *
     */
    public val forumChannels: Items<QGForumChannel>

    /**
     * 根据ID寻找匹配的
     *
     * @throws QQGuildApiException API请求过程中出现的异常
     * @throws IllegalStateException 当目标子频道类型不是 [ChannelType.FORUM] 时
     */
    @JST(blockingBaseName = "getForumChannel", blockingSuffix = "", asyncBaseName = "getForumChannel")
    public suspend fun forumChannel(id: ID): QGForumChannel?

}
