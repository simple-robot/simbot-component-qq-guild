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

package love.forte.simbot.qguild.api

import love.forte.simbot.qguild.ErrInfo
import love.forte.simbot.qguild.QQGuildApiException
import love.forte.simbot.qguild.model.MessageAuditedId


/**
 *
 * @author ForteScarlet
 */
public class MessageAuditedException : QQGuildApiException {
    // TODO

    public val messageAuditedId: MessageAuditedId

    internal constructor(messageAuditedId: MessageAuditedId, value: Int, description: String) : super(
        value,
        description
    ) {
        this.messageAuditedId = messageAuditedId
    }

    internal constructor(
        messageAuditedId: MessageAuditedId,
        info: ErrInfo?,
        value: Int,
        description: String
    ) : super(info, value, description) {
        this.messageAuditedId = messageAuditedId
    }
}
