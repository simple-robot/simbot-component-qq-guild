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

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.qguild.model.Message
import love.forte.simbot.qguild.model.MessageAudited


/**
 * 消息事件
 * [`AT_MESSAGE_CREATE（intents PUBLIC_GUILD_MESSAGES）`](https://bot.q.qq.com/wiki/develop/api/gateway/message.html#at-message-create-intents-public-guild-messages)
 *
 * ## 发送时机
 * - 用户发送消息，@当前机器人或回复机器人消息时
 * - 为保障消息投递的速度，消息顺序我们虽然会尽量有序，但是并不保证是严格有序的，
 * 如开发者对消息顺序有严格有序的需求，可以自行缓冲消息事件之后，基于 [seq] 进行排序
 *
 */
@Serializable
@SerialName(EventIntents.PublicGuildMessages.AT_MESSAGE_CREATE_TYPE)
public data class AtMessageCreate(override val s: Long, @SerialName("d") override val data: Message) : Signal.Dispatch()

/**
 * 消息审核事件
 * [`MESSAGE_AUDIT_PASS（intents MESSAGE_AUDIT）`](https://bot.q.qq.com/wiki/develop/api/gateway/message.html#message-audit-pass-intents-message-audit)
 *
 * ## 发送时机
 * - 消息审核通过
 *
 */
@Serializable
@SerialName(EventIntents.MessageAudit.MESSAGE_AUDIT_PASS_TYPE)
public data class MessageAuditPass(override val s: Long, @SerialName("d") override val data: MessageAudited) : Signal.Dispatch()

/**
 * 消息审核事件
 * [`MESSAGE_AUDIT_REJECT（intents MESSAGE_AUDIT）`](https://bot.q.qq.com/wiki/develop/api/gateway/message.html#message-audit-reject-intents-message-audit)
 *
 * ## 发送时机
 * - 消息审核不通过
 *
 */
@Serializable
@SerialName(EventIntents.MessageAudit.MESSAGE_AUDIT_REJECT_TYPE)
public data class MessageAuditReject(override val s: Long, @SerialName("d") override val data: MessageAudited) : Signal.Dispatch()

/**
 * 私信消息事件
 * [`DIRECT_MESSAGE_CREATE (intents DIRECT_MESSAGE)`](https://bot.q.qq.com/wiki/develop/api/gateway/direct_message.html#direct-message-create-intents-direct-message)
 *
 * ## 发送时机
 * - 用户通过私信发消息给机器人时
 *
 */
@Serializable
@SerialName(EventIntents.DirectMessage.DIRECT_MESSAGE_CREATE_TYPE)
public data class DirectMessageCreate(override val s: Long, @SerialName("d") override val data: Message) : Signal.Dispatch()
