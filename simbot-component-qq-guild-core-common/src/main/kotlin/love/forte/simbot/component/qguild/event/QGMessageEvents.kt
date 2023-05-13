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

import love.forte.simbot.Timestamp
import love.forte.simbot.component.qguild.JSTP
import love.forte.simbot.component.qguild.QGMember
import love.forte.simbot.component.qguild.QGTextChannel
import love.forte.simbot.component.qguild.message.QGMessageReceipt
import love.forte.simbot.component.qguild.message.QGReceiveMessageContent
import love.forte.simbot.definition.Objective
import love.forte.simbot.event.*
import love.forte.simbot.message.doSafeCast
import love.forte.simbot.qguild.QQGuildApiException
import love.forte.simbot.qguild.model.Message
import love.forte.simbot.qguild.model.Message as QGSourceMessage


/**
 * [消息事件](https://bot.q.qq.com/wiki/develop/api/gateway/message.html#at-message-create-intents-public-guild-messages)
 * 的组件事件封装。
 *
 * [QGMessageEvent] 作为一个基础事件类型，本身不提供过多的类型约束，参考更具体的实现类型。
 * - [QGAtMessageCreateEvent]
 *
 *
 * @see QGAtMessageCreateEvent
 *
 * @author ForteScarlet
 */
@BaseEvent
public sealed class QGMessageEvent : QGEvent<QGSourceMessage>(), MessageEvent {
    /**
     * 标准库接收到的原始事件内容。
     */
    abstract override val sourceEventEntity: Message

    /**
     * 消息发生（收到）的时间
     */
    abstract override val timestamp: Timestamp

    /**
     * 接收到的消息内容。
     */
    abstract override val messageContent: QGReceiveMessageContent

    /**
     * 基于当前事件进行消息回复。
     *
     * 对于公域bot来说，也理应这么回复。[reply] 会默认根据当前事件为当前可能产生的所有消息体填充回复的目标（`msg_id` 或 `event_id`），
     * 并且与 `send` 相比会默认追加一个 [引用效果][Message.messageReference] （如果 [message] 中存在 [自定义reference][love.forte.simbot.component.qguild.message.QGReference]
     * 则在其影响范围内不会自动填充）。
     *
     * @throws QQGuildApiException 请求失败，例如无权限
     */
    abstract override suspend fun reply(message: love.forte.simbot.message.Message): QGMessageReceipt

    /**
     * 接收到消息的本体。实现子类型来决定具体类型。
     *
     * @throws QQGuildApiException 请求失败，例如无权限
     * @throws NoSuchElementException 没有找到结果
     */
    abstract override suspend fun source(): Objective

    abstract override val key: Event.Key<out QGMessageEvent>

    public companion object Key : BaseEventKey<QGMessageEvent>(
        "qg.message", QGEvent, MessageEvent
    ) {
        override fun safeCast(value: Any): QGMessageEvent? = doSafeCast(value)
    }
}


/**
 * [AT_MESSAGE_CREATE](https://bot.q.qq.com/wiki/develop/api/gateway/message.html#at-message-create-intents-public-guild-messages)
 *
 * 新的@了bot的消息被创建，也就是公域的at消息事件。
 *
 * 发送时机
 *  * - 用户发送消息，@当前机器人或回复机器人消息时
 *
 * @author ForteScarlet
 */
@JSTP
public abstract class QGAtMessageCreateEvent : QGMessageEvent(), ChannelMessageEvent {

    /**
     * 发送消息的用户
     *
     * @throws QQGuildApiException 请求失败，例如无权限
     * @throws NoSuchElementException 没有找到结果
     */
    abstract override suspend fun author(): QGMember


    /**
     * 接收到消息的子频道。
     *
     * @throws QQGuildApiException 请求失败，例如无权限
     * @throws NoSuchElementException 没有找到结果
     */
    abstract override suspend fun channel(): QGTextChannel

    /**
     * 接收到消息的子频道。同 [channel]
     *
     * @throws QQGuildApiException 请求失败，例如无权限
     * @throws NoSuchElementException 没有找到结果
     */
    override suspend fun source(): QGTextChannel = channel()

    /**
     * 接收到消息的子频道。同 [channel]
     *
     * @throws QQGuildApiException 请求失败，例如无权限
     * @throws NoSuchElementException 没有找到结果
     */
    override suspend fun organization(): QGTextChannel = channel()

    override val key: Event.Key<out QGAtMessageCreateEvent> get() = Key

    public companion object Key : BaseEventKey<QGAtMessageCreateEvent>(
        "qg.at_message_create", QGMessageEvent, ChannelMessageEvent
    ) {
        override fun safeCast(value: Any): QGAtMessageCreateEvent? = doSafeCast(value)
    }
}
