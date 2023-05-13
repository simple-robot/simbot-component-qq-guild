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
import love.forte.simbot.action.DeleteSupport
import love.forte.simbot.component.qguild.JST
import love.forte.simbot.component.qguild.JSTP
import love.forte.simbot.component.qguild.QGBot
import love.forte.simbot.component.qguild.QGObjectiveContainer
import love.forte.simbot.definition.BotContainer
import love.forte.simbot.definition.ChannelInfoContainer
import love.forte.simbot.qguild.QQGuildApiException
import love.forte.simbot.qguild.model.forum.Thread


/**
 *
 * 一个在 [QGForumChannel] 中的主题帖。
 *
 * @author ForteScarlet
 */
public interface QGThread : CoroutineScope, BotContainer, ChannelInfoContainer, QGObjectiveContainer<Thread>, DeleteSupport {
    /**
     * 主题帖信息的源类型。
     */
    override val source: Thread

    /**
     * 所属BOT
     */
    override val bot: QGBot

    // TODO info

    /**
     * 这个帖子所属的子频道。
     */
    @JSTP
    override suspend fun channel(): QGForumChannel

    /**
     * 删除此帖。
     *
     * @return 当API请求成功得到 `true`，当API请求响应 404 得到 `false`，其他情况抛出原异常。
     * @throws QQGuildApiException API请求产生的异常
     */
    @JST
    override suspend fun delete(): Boolean
}
