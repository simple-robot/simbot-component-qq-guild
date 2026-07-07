/*
 * Copyright (c) 2025-2026. ForteScarlet.
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

package love.forte.simbot.component.qguild.message

import kotlinx.serialization.Serializable
import love.forte.simbot.logger.LoggerFactory
import love.forte.simbot.message.Message
import love.forte.simbot.message.Messages
import love.forte.simbot.qguild.model.MessageKeyboards

/**
 *
 * markdown 消息内含有的按钮信息
 *
 * @since 4.2.0
 *
 * @author ForteScarlet
 */
@Serializable
public data class QGKeyboards /* TODO internal */ constructor(
    public val keyboards: MessageKeyboards
) : QGMessageElement {
}

internal object KeyboardsParser : SendingMessageParser {
    internal val logger = LoggerFactory.getLogger("love.forte.simbot.component.qguild.message.KeyboardsParser")

    override suspend fun invoke(
        index: Int,
        element: Message.Element,
        messages: Messages?,
        builderContext: SendingMessageParser.BuilderContext
    ) {
        // 频道相关的消息发送不支持 keyboards
        if (element is QGKeyboards) {
            logger.warn("Keyboards message is not yet supported for sending to channel.")
        }
    }

    override suspend fun invoke(
        index: Int,
        element: Message.Element,
        messages: Messages?,
        builderContext: SendingMessageParser.GroupAndC2CBuilderContext
    ) {
        if (element is QGKeyboards) {
            val keyboards = element.keyboards
            val builder = builderContext.builderOrNew {
                it.keyboards == null
            }
            builder.keyboards = keyboards
        }
    }
}