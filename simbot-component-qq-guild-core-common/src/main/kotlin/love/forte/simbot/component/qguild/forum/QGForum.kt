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

import love.forte.simbot.definition.BotContainer
import love.forte.simbot.definition.GuildInfoContainer
import love.forte.simbot.qguild.model.ChannelType
import love.forte.simbot.qguild.model.Channel as QGSourceChannel

/**
 * 一个帖子类型的频道。
 *
 * [QGForum] 基于 [子频道][QGSourceChannel] 类型，
 * 提供有关帖子的相关功能，包括查询帖子、发帖、删贴等。
 *
 * [QGForum] 内包含一个类型为 [ChannelType.FORUM] 的频道信息。
 *
 * 需要注意的是，[QGForum] 只会在构建时检测频道类型，当一个 [QGForum] 被持有，
 * 而在此期间此频道类型发生了改变，那么后续继续调用API则可能会引发 [IllegalStateException] 异常。
 *
 * @author ForteScarlet
 */
public interface QGForum : BotContainer, GuildInfoContainer {

    /**
     * 表示此帖子频道的源频道。
     */
    public val source: QGSourceChannel

    // TODO 查、发、删

}
