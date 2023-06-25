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

package love.forte.simbot.qguild.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.qguild.api.MessageAuditedException

// TODO
// see https://bot.q.qq.com/wiki/develop/api/openapi/message/post_messages.html
// 详见错误码。
//
//其中推送、回复消息的 code 错误码 304023、304024 会在 响应数据包 data 中返回 MessageAudit 审核消息的信息

/**
 *
 * @see MessageAuditedException
 */
@Serializable
public data class MessageAudit(@SerialName("message_audit") val messageAudit: MessageAuditedId)


// Only id?

@Serializable
public data class MessageAuditedId(@SerialName("audit_id") val auditId: String)
