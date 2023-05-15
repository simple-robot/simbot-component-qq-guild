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

package love.forte.simbot.qguild.event

import love.forte.simbot.qguild.PrivateDomainOnly
import love.forte.simbot.qguild.model.forum.AuditResult
import love.forte.simbot.qguild.model.forum.Post
import love.forte.simbot.qguild.model.forum.Reply
import love.forte.simbot.qguild.model.forum.Thread


/**
 * [论坛事件(ForumEvent)](https://bot.q.qq.com/wiki/develop/api/gateway/forum.html#forum-event-intents-forum-event)
 *
 * ### 发送时机
 * 用户在话题子频道内发帖、评论、回复评论时产生该事件
 *
 * ### 主题事件
 * - FORUM_THREAD_CREATE
 * - FORUM_THREAD_UPDATE
 * - FORUM_THREAD_DELETE
 *
 * 事件内容为 [Thread] 对象
 *
 * ### 帖子事件
 * - FORUM_POST_CREATE
 * - FORUM_POST_DELETE
 *
 * 事件内容为 [Post] 对象
 *
 * ### 回复事件
 * - FORUM_REPLY_CREATE
 * - FORUM_REPLY_DELETE
 *
 * 事件内容为 [Reply] 对象
 *
 * ### 帖子审核事件
 * - FORUM_PUBLISH_AUDIT_RESULT
 *
 * 事件内容为 [AuditResult] 对象
 *
 * @see EventIntents.ForumsEvent
 *
 */
@PrivateDomainOnly
public sealed class ForumDispatch : Signal.Dispatch() {
    /**
     * 事件内容。
     *
     * 具体的事件内容类型由 [ForumDispatch] 的实现类型决定。
     *
     */
    public abstract override val data: Any
}

// TODO
