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

import love.forte.simbot.ID
import love.forte.simbot.Timestamp
import love.forte.simbot.component.qguild.QGBot
import love.forte.simbot.component.qguild.QGGuild
import love.forte.simbot.component.qguild.QGMember
import love.forte.simbot.component.qguild.QGObjectiveContainer
import love.forte.simbot.definition.BotContainer
import love.forte.simbot.definition.ChannelInfoContainer
import love.forte.simbot.definition.GuildInfoContainer
import love.forte.simbot.definition.IDContainer
import love.forte.simbot.qguild.QQGuildApiException
import love.forte.simbot.qguild.model.forum.Reply


/**
 * 在 [QGForumReplyEvent] 中获取到的回复信息。
 *
 * @author ForteScarlet
 */
public interface QGReply : QGObjectiveContainer<Reply>,
    QGForumInfoContainer,
    IDContainer, BotContainer, GuildInfoContainer,
    ChannelInfoContainer {

    /**
     * 当前bot
     */
    override val bot: QGBot

    /**
     * 回复信息的源信息
     */
    override val source: Reply

    /**
     * 此回复的ID
     */
    override val id: ID get() = source.replyInfo.postId.ID

    /**
     * 频道ID
     */
    override val guildId: ID get() = source.guildId.ID

    /**
     * 子频道ID
     */
    override val channelId: ID get() = source.channelId.ID

    /**
     * 作者ID
     */
    override val authorId: ID get() = source.authorId.ID

    /**
     * 此回复对应的主题帖ID
     */
    public val threadId: ID get() = source.replyInfo.threadId.ID

    /**
     * 此回复对应的评论ID
     */
    public val postId: ID get() = source.replyInfo.postId.ID

    /**
     * 内容
     */
    public val content: String get() = source.replyInfo.content

    /**
     * 评论时间
     */
    public val datetime: Timestamp

    /**
     * 此评论所属的频道服务器
     *
     * @throws QQGuildApiException API请求产生异常
     * @throws NoSuchElementException 此频道已不存在
     */
    override suspend fun guild(): QGGuild

    /**
     * 此评论所属的论坛子频道
     *
     * @throws QQGuildApiException API请求产生异常
     * @throws IllegalStateException 此子频道已不再是论坛类型
     * @throws NoSuchElementException 此子频道已不存在
     */
    override suspend fun channel(): QGForumChannel

    /**
     * 频道中的回复者。
     *
     * @throws QQGuildApiException API请求产生异常
     * @throws NoSuchElementException 此成员已不存在
     */
    public suspend fun author(): QGMember

    /**
     * 此回复所属的主题
     *
     * @throws QQGuildApiException API请求产生异常
     * @throws NoSuchElementException 所属主题已不存在
     */
    public suspend fun thread(): QGThread

    // No API supported
//    /**
//     * 此回复所属的评论
//     *
//     * @throws QQGuildApiException API请求产生异常
//     * @throws NoSuchElementException 所属评论已不存在
//     */
//    public suspend fun post(): QGPost
}
