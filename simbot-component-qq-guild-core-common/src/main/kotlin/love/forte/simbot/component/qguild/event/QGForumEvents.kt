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

package love.forte.simbot.component.qguild.event

import love.forte.simbot.ID
import love.forte.simbot.JSTP
import love.forte.simbot.component.qguild.QGGuild
import love.forte.simbot.component.qguild.QGMember
import love.forte.simbot.component.qguild.forum.QGForumChannel
import love.forte.simbot.component.qguild.forum.QGThread
import love.forte.simbot.definition.ChannelInfoContainer
import love.forte.simbot.definition.GuildInfoContainer
import love.forte.simbot.event.BaseEvent
import love.forte.simbot.event.BaseEventKey
import love.forte.simbot.event.Event
import love.forte.simbot.message.doSafeCast
import love.forte.simbot.qguild.PrivateDomainOnly
import love.forte.simbot.qguild.QQGuildApiException
import love.forte.simbot.qguild.event.*
import love.forte.simbot.qguild.model.forum.ForumSourceInfo
import love.forte.simbot.qguild.model.forum.Thread


/**
 * QQ频道的 [Forums][EventIntents.ForumsEvent] 事件。
 *
 * Forums 相关的事件面向 **私域BOT**，请注意确认bot类型。
 *
 * 基于 API 模块中的 [ForumDispatch]。
 *
 * @see ForumDispatch
 */
@BaseEvent
@JSTP
@PrivateDomainOnly
public abstract class QGForumEvent : QGEvent<ForumSourceInfo>(), GuildInfoContainer, ChannelInfoContainer {
    /**
     * 频道ID
     *
     * @see OpenForumEventData.guildId
     */
    public abstract val guildId: ID

    /**
     * 子频道ID
     *
     * @see OpenForumEventData.channelId
     */
    public abstract val channelId: ID

    /**
     * 发布人ID
     *
     * @see OpenForumEventData.authorId
     */
    public abstract val authorId: ID


    /**
     * 得到本次事件 [guildId] 对应的频道。
     *
     * @throws QQGuildApiException API请求过程出现异常
     * @throws NoSuchElementException 对应子频道已不存在
     */
    override suspend fun guild(): QGGuild =
        bot.guild(guildId) ?: throw NoSuchElementException("guild(id=$guildId)")

    /**
     * 得到本次事件 [channelId] 对应的论坛子频道。
     *
     * @throws QQGuildApiException API请求过程出现异常
     * @throws IllegalStateException 对应子频道已经不再是论坛类型
     * @throws NoSuchElementException 对应子频道已不存在
     */
    abstract override suspend fun channel(): QGForumChannel

    /**
     * 得到本次事件 [authorId] 对应的频道中成员。
     *
     * @throws QQGuildApiException API请求过程出现异常
     * @throws NoSuchElementException 对应成员已不存在
     */
    public abstract suspend fun author(): QGMember

    abstract override val key: Event.Key<out QGForumEvent>

    public companion object Key : BaseEventKey<QGForumEvent>("qg.forum", QGEvent) {
        override fun safeCast(value: Any): QGForumEvent? = doSafeCast(value)
    }
}

/**
 * QQ频道的 [Forums][EventIntents.ForumsEvent] 中的主题事件。
 *
 * 基于 API 模块中的 [ForumThreadDispatch]。
 *
 * @see ForumThreadDispatch
 */
@PrivateDomainOnly
public abstract class QGForumThreadEvent : QGForumEvent() {
    abstract override val sourceEventEntity: Thread

    /**
     * 得到此事件中基于 [sourceEventEntity] 的 [QGThread]。
     */
    public abstract val thread: QGThread

    abstract override val key: Event.Key<out QGForumThreadEvent>

    public companion object Key : BaseEventKey<QGForumThreadEvent>("qg.forum_thread", QGForumEvent) {
        override fun safeCast(value: Any): QGForumThreadEvent? = doSafeCast(value)
    }
}

/**
 * QQ频道的 [Forums][EventIntents.ForumsEvent] 中的创建主题事件。
 *
 * 基于 API 模块中的 [ForumThreadCreate]。
 *
 * @see ForumThreadCreate
 */
public abstract class QGForumThreadCreateEvent : QGForumThreadEvent() {
    override val key: Event.Key<out QGForumThreadCreateEvent> get() = Key

    public companion object Key : BaseEventKey<QGForumThreadCreateEvent>("qg.forum_thread_create", QGForumThreadEvent) {
        override fun safeCast(value: Any): QGForumThreadCreateEvent? = doSafeCast(value)
    }
}

/**
 * QQ频道的 [Forums][EventIntents.ForumsEvent] 中的更新主题事件。
 *
 * 基于 API 模块中的 [ForumThreadUpdate]。
 *
 * @see ForumThreadUpdate
 */
public abstract class QGForumThreadUpdateEvent : QGForumThreadEvent() {
    override val key: Event.Key<out QGForumThreadUpdateEvent> get() = Key

    public companion object Key : BaseEventKey<QGForumThreadUpdateEvent>("qg.forum_thread_update", QGForumThreadEvent) {
        override fun safeCast(value: Any): QGForumThreadUpdateEvent? = doSafeCast(value)
    }
}

/**
 * QQ频道的 [Forums][EventIntents.ForumsEvent] 中的删除主题事件。
 *
 * 基于 API 模块中的 [ForumThreadDelete]。
 *
 * @see ForumThreadDelete
 */
public abstract class QGForumThreadDeleteEvent : QGForumThreadEvent() {
    override val key: Event.Key<out QGForumThreadDeleteEvent> get() = Key

    public companion object Key : BaseEventKey<QGForumThreadDeleteEvent>("qg.forum_thread_delete", QGForumThreadEvent) {
        override fun safeCast(value: Any): QGForumThreadDeleteEvent? = doSafeCast(value)
    }
}



// TODO
