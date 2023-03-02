/*
 * Copyright (c) 2022-2023. ForteScarlet.
 *
 * This file is part of simbot-component-tencent-guild.
 *
 * simbot-component-tencent-guild is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * simbot-component-tencent-guild is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with simbot-component-tencent-guild. If not, see <https://www.gnu.org/licenses/>.
 */

package love.forte.simbot.component.tencentguild.internal

import love.forte.simbot.ID
import love.forte.simbot.component.tencentguild.message.TencentReceiveMessageContent
import love.forte.simbot.message.Messages
import love.forte.simbot.qguild.model.Message

/**
 *
 * @author ForteScarlet
 */

/**
 *
 * @author ForteScarlet
 */
internal class TencentReceiveMessageContentImpl(sourceMessage: Message) : TencentReceiveMessageContent() {

    override val messageId: ID = sourceMessage.id

    override val messages: Messages by lazy(LazyThreadSafetyMode.NONE) { MessageParsers.parse(sourceMessage) }
    

    override val plainText: String by lazy(LazyThreadSafetyMode.NONE) {
        var content = sourceMessage.content

        for (mention in sourceMessage.mentions) {
            val target = "<@!${mention.id}>"
            content = content.replaceFirst(target, "")
        }

        if (sourceMessage.mentionEveryone) {
            content = content.replaceFirst("@everyone", "")
        }

        content
    }

}
