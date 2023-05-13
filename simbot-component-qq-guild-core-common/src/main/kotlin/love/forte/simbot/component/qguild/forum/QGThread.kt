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
import love.forte.simbot.Timestamp
import love.forte.simbot.action.DeleteSupport
import love.forte.simbot.component.qguild.*
import love.forte.simbot.definition.BotContainer
import love.forte.simbot.definition.ChannelInfoContainer
import love.forte.simbot.definition.GuildInfoContainer
import love.forte.simbot.definition.IDContainer
import love.forte.simbot.qguild.QQGuildApiException
import love.forte.simbot.qguild.model.forum.Thread


/**
 *
 * 一个在 [QGForumChannel] 中的主题帖。
 *
 * @author ForteScarlet
 */
public interface QGThread : CoroutineScope, IDContainer, BotContainer, ChannelInfoContainer, QGObjectiveContainer<Thread>, DeleteSupport, GuildInfoContainer {
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
     * 帖子ID
     */
    override val id: ID get() = source.threadInfo.threadId.ID

    /**
     * 频道ID
     */
    public val guildId: ID get() = source.guildId.ID

    /**
     * 子频道ID
     */
    public val channelId: ID get() = source.channelId.ID

    /**
     * 作者ID
     */
    public val authorId: ID get() = source.authorId.ID

    /**
     * 依据 [guildId] 寻找所属频道
     *
     * @throws QQGuildApiException api请求异常
     * @throws NoSuchElementException 获取时目标已不存在
     */
    @JSTP
    override suspend fun guild(): QGGuild

    /**
     * 这个帖子所属的子频道。
     */
    @JSTP
    override suspend fun channel(): QGForumChannel

    /**
     * 依据 [authorId] 寻找作者信息
     *
     * @throws QQGuildApiException api请求异常
     * @throws NoSuchElementException 获取时目标已不存在
     */
    @JSTP
    public suspend fun author(): QGMember

    /**
     * 帖子标题
     */
    public val title: String get() = source.threadInfo.title

    /**
     * 帖子内容
     */
    public val content: String get() = source.threadInfo.content

    /**
     * 帖子发表时间
     */
    public val dateTime: Timestamp

    /**
     * 删除此帖。
     *
     * @return 当API请求成功得到 `true`，当API请求响应 404 得到 `false`，其他情况抛出原异常。
     * @throws QQGuildApiException API请求产生的异常
     */
    @JST
    override suspend fun delete(): Boolean
}
