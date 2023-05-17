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
import love.forte.simbot.component.qguild.forum.QGPost
import love.forte.simbot.component.qguild.forum.QGReply
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
import love.forte.simbot.qguild.model.forum.*


/**
 * QQ频道的 [EventIntents.ForumsEvent][EventIntents.ForumsEvent] 事件。
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
    public open val guildId: ID get() = sourceEventEntity.guildId.ID

    /**
     * 子频道ID
     *
     * @see OpenForumEventData.channelId
     */
    public open val channelId: ID get() = sourceEventEntity.channelId.ID

    /**
     * 发布人ID
     *
     * @see OpenForumEventData.authorId
     */
    public open val authorId: ID get() = sourceEventEntity.authorId.ID

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
 * QQ频道的 [EventIntents.ForumsEvent][EventIntents.ForumsEvent] 中的主题事件。
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
 * QQ频道的 [EventIntents.ForumsEvent][EventIntents.ForumsEvent] 中的创建主题事件。
 *
 * 基于 API 模块中的 [ForumThreadCreate]。
 *
 * @see ForumThreadCreate
 */
@PrivateDomainOnly
public abstract class QGForumThreadCreateEvent : QGForumThreadEvent() {
    override fun toString(): String =
        "QGForumThreadCreateEvent(sourceEventEntity=$sourceEventEntity)"

    override val key: Event.Key<out QGForumThreadCreateEvent> get() = Key

    public companion object Key : BaseEventKey<QGForumThreadCreateEvent>("qg.forum_thread_create", QGForumThreadEvent) {
        override fun safeCast(value: Any): QGForumThreadCreateEvent? = doSafeCast(value)
    }
}

/**
 * QQ频道的 [EventIntents.ForumsEvent][EventIntents.ForumsEvent] 中的更新主题事件。
 *
 * 基于 API 模块中的 [ForumThreadUpdate]。
 *
 * @see ForumThreadUpdate
 */
@PrivateDomainOnly
public abstract class QGForumThreadUpdateEvent : QGForumThreadEvent() {
    override fun toString(): String =
        "QGForumThreadUpdateEvent(sourceEventEntity=$sourceEventEntity)"

    override val key: Event.Key<out QGForumThreadUpdateEvent> get() = Key

    public companion object Key : BaseEventKey<QGForumThreadUpdateEvent>("qg.forum_thread_update", QGForumThreadEvent) {
        override fun safeCast(value: Any): QGForumThreadUpdateEvent? = doSafeCast(value)
    }
}

/**
 * QQ频道的 [EventIntents.ForumsEvent][EventIntents.ForumsEvent] 中的删除主题事件。
 *
 * 基于 API 模块中的 [ForumThreadDelete]。
 *
 * @see ForumThreadDelete
 */
@PrivateDomainOnly
public abstract class QGForumThreadDeleteEvent : QGForumThreadEvent() {
    override fun toString(): String =
        "QGForumThreadDeleteEvent(sourceEventEntity=$sourceEventEntity)"

    override val key: Event.Key<out QGForumThreadDeleteEvent> get() = Key

    public companion object Key : BaseEventKey<QGForumThreadDeleteEvent>("qg.forum_thread_delete", QGForumThreadEvent) {
        override fun safeCast(value: Any): QGForumThreadDeleteEvent? = doSafeCast(value)
    }
}

/**
 * QQ频道的 [EventIntents.ForumsEvent][EventIntents.ForumsEvent] 中的帖子（评论）事件。
 *
 * 基于 API 模块中的 [ForumPostDispatch]。
 *
 * @see ForumPostDispatch
 */
@PrivateDomainOnly
public abstract class QGForumPostEvent : QGForumEvent() {
    abstract override val sourceEventEntity: Post

    /**
     * 得到此事件中基于 [sourceEventEntity] 的 [QGPost]。
     */
    public abstract val post: QGPost

    abstract override val key: Event.Key<out QGForumPostEvent>

    public companion object Key : BaseEventKey<QGForumPostEvent>("qg.forum_post", QGForumEvent) {
        override fun safeCast(value: Any): QGForumPostEvent? = doSafeCast(value)
    }
}

/**
 * QQ频道的 [EventIntents.ForumsEvent][EventIntents.ForumsEvent] 中的评论创建事件。
 *
 * 基于 API 模块中的 [ForumPostCreate]。
 *
 * @see ForumPostCreate
 */
@PrivateDomainOnly
public abstract class QGForumPostCreateEvent : QGForumPostEvent() {
    override fun toString(): String =
        "QGForumPostCreateEvent(sourceEventEntity=$sourceEventEntity)"

    override val key: Event.Key<out QGForumPostCreateEvent> get() = Key

    public companion object Key : BaseEventKey<QGForumPostCreateEvent>("qg.forum_post_create", QGForumPostEvent) {
        override fun safeCast(value: Any): QGForumPostCreateEvent? = doSafeCast(value)
    }
}

/**
 * QQ频道的 [EventIntents.ForumsEvent][EventIntents.ForumsEvent] 中的评论删除事件。
 *
 * 基于 API 模块中的 [ForumPostDelete]。
 *
 * @see ForumPostDelete
 */
@PrivateDomainOnly
public abstract class QGForumPostDeleteEvent : QGForumPostEvent() {
    override fun toString(): String =
        "QGForumPostDeleteEvent(sourceEventEntity=$sourceEventEntity)"

    override val key: Event.Key<out QGForumPostDeleteEvent> get() = Key

    public companion object Key : BaseEventKey<QGForumPostDeleteEvent>("qg.forum_post_delete", QGForumPostEvent) {
        override fun safeCast(value: Any): QGForumPostDeleteEvent? = doSafeCast(value)
    }
}


/**
 * QQ频道的 [EventIntents.ForumsEvent][EventIntents.ForumsEvent] 中的评论回复事件。
 *
 * 基于 API 模块中的 [ForumReplyDispatch]。
 *
 * @see ForumReplyDispatch
 */
@PrivateDomainOnly
public abstract class QGForumReplyEvent : QGForumEvent() {
    abstract override val sourceEventEntity: Reply

    /**
     * 得到此事件中基于 [sourceEventEntity] 的 [QGReply]。
     */
    public abstract val post: QGReply

    abstract override val key: Event.Key<out QGForumReplyEvent>

    public companion object Key : BaseEventKey<QGForumReplyEvent>("qg.forum_reply", QGForumEvent) {
        override fun safeCast(value: Any): QGForumReplyEvent? = doSafeCast(value)
    }
}

/**
 * QQ频道的 [EventIntents.ForumsEvent][EventIntents.ForumsEvent] 中的回复创建事件。
 *
 * 基于 API 模块中的 [ForumReplyCreate]。
 *
 * @see ForumReplyCreate
 */
@PrivateDomainOnly
public abstract class QGForumReplyCreateEvent : QGForumReplyEvent() {
    override fun toString(): String =
        "QGForumReplyCreateEvent(sourceEventEntity=$sourceEventEntity)"

    override val key: Event.Key<out QGForumReplyCreateEvent> get() = Key

    public companion object Key : BaseEventKey<QGForumReplyCreateEvent>("qg.forum_reply_create", QGForumReplyEvent) {
        override fun safeCast(value: Any): QGForumReplyCreateEvent? = doSafeCast(value)
    }
}

/**
 * QQ频道的 [EventIntents.ForumsEvent][EventIntents.ForumsEvent] 中的回复删除事件。
 *
 * 基于 API 模块中的 [ForumReplyDelete]。
 *
 * @see ForumReplyDelete
 */
@PrivateDomainOnly
public abstract class QGForumReplyDeleteEvent : QGForumReplyEvent() {
    override fun toString(): String =
        "QGForumReplyDeleteEvent(sourceEventEntity=$sourceEventEntity)"

    override val key: Event.Key<out QGForumReplyDeleteEvent> get() = Key

    public companion object Key : BaseEventKey<QGForumReplyDeleteEvent>("qg.forum_reply_delete", QGForumReplyEvent) {
        override fun safeCast(value: Any): QGForumReplyDeleteEvent? = doSafeCast(value)
    }
}

/**
 * QQ频道的 [EventIntents.ForumsEvent][EventIntents.ForumsEvent] 中的帖子审核通过事件。
 *
 * 基于 API 模块中的 [ForumPublishAuditResult]。
 *
 * @see ForumPublishAuditResult
 *
 */
@PrivateDomainOnly
public abstract class QGForumPublishAuditResultEvent : QGForumEvent() {
    /**
     * 得到此事件中的源数据 [AuditResult]。
     */
    abstract override val sourceEventEntity: AuditResult

    override fun toString(): String =
        "QGForumPublishAuditResultEvent(sourceEventEntity=$sourceEventEntity)"

    override val key: Event.Key<out QGForumPublishAuditResultEvent> get() = Key

    public companion object Key :
        BaseEventKey<QGForumPublishAuditResultEvent>("qg.forum_publish_audit_result", QGForumEvent) {
        override fun safeCast(value: Any): QGForumPublishAuditResultEvent? = doSafeCast(value)
    }

}
