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
import love.forte.simbot.definition.ChannelInfoContainer
import love.forte.simbot.definition.GuildInfoContainer
import love.forte.simbot.event.BaseEvent
import love.forte.simbot.event.BaseEventKey
import love.forte.simbot.event.Event
import love.forte.simbot.message.doSafeCast
import love.forte.simbot.qguild.QQGuildApiException
import love.forte.simbot.qguild.event.*


/**
 * QQ频道的 [OpenForums][EventIntents.OpenForumsEvent] 事件。
 *
 * OpenForums 相关的事件面向公域BOT，但是没有任何可用的额外信息，基本上就是只包括了频道ID、创建人ID等id信息。
 *
 * 基于 API 模块中的 [OpenForumDispatch]。
 *
 * @see OpenForumDispatch
 */
@BaseEvent
@JSTP
public abstract class QGOpenForumEvent : QGEvent<OpenForumEventData>(), GuildInfoContainer, ChannelInfoContainer {
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

    abstract override val key: Event.Key<out QGOpenForumEvent>

    public companion object Key : BaseEventKey<QGOpenForumEvent>("qg.open_forum", QGEvent) {
        override fun safeCast(value: Any): QGOpenForumEvent? = doSafeCast(value)
    }
}

/**
 * QQ频道 [OpenForums][EventIntents.OpenForumsEvent] 中的主题事件。
 *
 * 来自 API 模块的 [OpenForumThreadDispatch] 事件。
 *
 * @see OpenForumThreadDispatch
 * @see QGOpenForumEvent
 */
public abstract class QGOpenForumThreadEvent : QGOpenForumEvent() {
    /**
     * API模块的原事件内容。
     */
    abstract override val sourceEventEntity: OpenForumThreadData

    abstract override val key: Event.Key<out QGOpenForumThreadEvent>

    public companion object Key : BaseEventKey<QGOpenForumThreadEvent>("qg.open_forum_thread", QGOpenForumEvent) {
        override fun safeCast(value: Any): QGOpenForumThreadEvent? = doSafeCast(value)
    }
}

/**
 * QQ频道 [OpenForums][EventIntents.OpenForumsEvent] 中的主题创建事件。
 *
 * 来自 API 模块的 [OpenForumThreadCreate] 事件。
 *
 * @see OpenForumThreadCreate
 * @see QGOpenForumThreadEvent
 */
public abstract class QGOpenForumThreadCreateEvent : QGOpenForumThreadEvent() {
    override val key: Event.Key<out QGOpenForumThreadCreateEvent> get() = Key

    override fun toString(): String {
        return "QGOpenForumThreadCreateEvent(sourceEventEntity=$sourceEventEntity)"
    }

    public companion object Key :
        BaseEventKey<QGOpenForumThreadCreateEvent>("qg.open_forum_thread_create", QGOpenForumThreadEvent) {
        override fun safeCast(value: Any): QGOpenForumThreadCreateEvent? = doSafeCast(value)
    }
}

/**
 * QQ频道 [OpenForums][EventIntents.OpenForumsEvent] 中的主题更新事件。
 *
 * 来自 API 模块的 [OpenForumThreadUpdate] 事件。
 *
 * @see OpenForumThreadUpdate
 * @see QGOpenForumThreadEvent
 */
public abstract class QGOpenForumThreadUpdateEvent : QGOpenForumThreadEvent() {
    override val key: Event.Key<out QGOpenForumThreadUpdateEvent> get() = Key

    override fun toString(): String {
        return "QGOpenForumThreadUpdateEvent(sourceEventEntity=$sourceEventEntity)"
    }

    public companion object Key :
        BaseEventKey<QGOpenForumThreadUpdateEvent>("qg.open_forum_thread_update", QGOpenForumThreadEvent) {
        override fun safeCast(value: Any): QGOpenForumThreadUpdateEvent? = doSafeCast(value)
    }
}

/**
 * QQ频道 [OpenForums][EventIntents.OpenForumsEvent] 中的主题删除事件。
 *
 * 来自 API 模块的 [OpenForumThreadDelete] 事件。
 *
 * @see OpenForumThreadDelete
 * @see QGOpenForumThreadEvent
 */
public abstract class QGOpenForumThreadDeleteEvent : QGOpenForumThreadEvent() {
    override val key: Event.Key<out QGOpenForumThreadDeleteEvent> get() = Key

    override fun toString(): String {
        return "QGOpenForumThreadDeleteEvent(sourceEventEntity=$sourceEventEntity)"
    }

    public companion object Key :
        BaseEventKey<QGOpenForumThreadDeleteEvent>("qg.open_forum_thread_delete", QGOpenForumThreadEvent) {
        override fun safeCast(value: Any): QGOpenForumThreadDeleteEvent? = doSafeCast(value)
    }
}


/**
 * QQ频道 [OpenForums][EventIntents.OpenForumsEvent] 中的帖子（评论）事件。
 *
 * 来自 API 模块的 [OpenForumPostDispatch] 事件。
 *
 * @see OpenForumPostDispatch
 * @see QGOpenForumEvent
 */
public abstract class QGOpenForumPostEvent : QGOpenForumEvent() {
    /**
     * API模块的原事件内容。
     */
    abstract override val sourceEventEntity: OpenForumPostData

    abstract override val key: Event.Key<out QGOpenForumPostEvent>

    public companion object Key : BaseEventKey<QGOpenForumPostEvent>("qg.open_forum_post", QGOpenForumEvent) {
        override fun safeCast(value: Any): QGOpenForumPostEvent? = doSafeCast(value)
    }
}

/**
 * QQ频道 [OpenForums][EventIntents.OpenForumsEvent] 中的帖子（评论）创建事件。
 *
 * 来自 API 模块的 [OpenForumPostCreate] 事件。
 *
 * @see OpenForumPostCreate
 * @see QGOpenForumEvent
 */
public abstract class QGOpenForumPostCreateEvent : QGOpenForumPostEvent() {
    override val key: Event.Key<out QGOpenForumPostCreateEvent> get() = Key

    override fun toString(): String {
        return "QGOpenForumPostCreateEvent(sourceEventEntity=$sourceEventEntity)"
    }

    public companion object Key :
        BaseEventKey<QGOpenForumPostCreateEvent>("qg.open_forum_post_create", QGOpenForumPostEvent) {
        override fun safeCast(value: Any): QGOpenForumPostCreateEvent? = doSafeCast(value)
    }
}

/**
 * QQ频道 [OpenForums][EventIntents.OpenForumsEvent] 中的帖子（评论）删除事件。
 *
 * 来自 API 模块的 [OpenForumPostDelete] 事件。
 *
 * @see OpenForumPostDelete
 * @see QGOpenForumEvent
 */
public abstract class QGOpenForumPostDeleteEvent : QGOpenForumPostEvent() {
    override val key: Event.Key<out QGOpenForumPostDeleteEvent> get() = Key

    override fun toString(): String {
        return "QGOpenForumPostDeleteEvent(sourceEventEntity=$sourceEventEntity)"
    }

    public companion object Key :
        BaseEventKey<QGOpenForumPostDeleteEvent>("qg.open_forum_post_delete", QGOpenForumPostEvent) {
        override fun safeCast(value: Any): QGOpenForumPostDeleteEvent? = doSafeCast(value)
    }
}


/**
 * QQ频道 [OpenForums][EventIntents.OpenForumsEvent] 中的回复事件。
 *
 * 来自 API 模块的 [OpenForumReplyDispatch] 事件。
 *
 * @see OpenForumReplyDispatch
 * @see QGOpenForumEvent
 */
public abstract class QGOpenForumReplyEvent : QGOpenForumEvent() {
    /**
     * API模块的原事件内容。
     */
    abstract override val sourceEventEntity: OpenForumReplyData

    abstract override val key: Event.Key<out QGOpenForumReplyEvent>

    public companion object Key : BaseEventKey<QGOpenForumReplyEvent>("qg.open_forum_reply", QGOpenForumEvent) {
        override fun safeCast(value: Any): QGOpenForumReplyEvent? = doSafeCast(value)
    }
}

/**
 * QQ频道 [OpenForums][EventIntents.OpenForumsEvent] 中的回复创建事件。
 *
 * 来自 API 模块的 [OpenForumReplyCreate] 事件。
 *
 * @see OpenForumReplyCreate
 * @see QGOpenForumEvent
 */
public abstract class QGOpenForumReplyCreateEvent : QGOpenForumReplyEvent() {
    override val key: Event.Key<out QGOpenForumReplyCreateEvent> get() = Key

    override fun toString(): String {
        return "QGOpenForumReplyCreateEvent(sourceEventEntity=$sourceEventEntity)"
    }

    public companion object Key :
        BaseEventKey<QGOpenForumReplyCreateEvent>("qg.open_forum_reply_create", QGOpenForumReplyEvent) {
        override fun safeCast(value: Any): QGOpenForumReplyCreateEvent? = doSafeCast(value)
    }
}

/**
 * QQ频道 [OpenForums][EventIntents.OpenForumsEvent] 中的回复删除事件。
 *
 * 来自 API 模块的 [OpenForumReplyDelete] 事件。
 *
 * @see OpenForumReplyDelete
 * @see QGOpenForumEvent
 */
public abstract class QGOpenForumReplyDeleteEvent : QGOpenForumReplyEvent() {
    override val key: Event.Key<out QGOpenForumReplyDeleteEvent> get() = Key

    override fun toString(): String {
        return "QGOpenForumReplyDeleteEvent(sourceEventEntity=$sourceEventEntity)"
    }

    public companion object Key :
        BaseEventKey<QGOpenForumReplyDeleteEvent>("qg.open_forum_reply_delete", QGOpenForumReplyEvent) {
        override fun safeCast(value: Any): QGOpenForumReplyDeleteEvent? = doSafeCast(value)
    }
}
