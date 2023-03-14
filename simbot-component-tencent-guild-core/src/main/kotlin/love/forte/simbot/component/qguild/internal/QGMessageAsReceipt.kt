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

package love.forte.simbot.component.qguild.internal

import love.forte.simbot.ID
import love.forte.simbot.message.MessageReceipt
import love.forte.simbot.message.SingleMessageReceipt
import love.forte.simbot.qguild.model.Message

/**
 * QQ频道中消息发送后的回执。
 * @author ForteScarlet
 */
public interface QGMessageReceipt : MessageReceipt {
    /**
     * QQ频道消息发送api发送消息后得到的回执，也就是消息对象。
     */
    public val messageResult: Message

    /**
     * 是否发送成功。
     * 能得到此类型即说明消息已发送成功，始终为 `true`。
     */
    override val isSuccess: Boolean
        get() = true


    /**
     * 消息暂时不支持撤回。
     */
    override suspend fun delete(): Boolean = false
}



private class QGMessageAsReceipt(override val messageResult: Message) : QGMessageReceipt, SingleMessageReceipt() {
    override val id: ID = messageResult.id.ID


}

internal fun Message.asReceipt(): QGMessageReceipt = QGMessageAsReceipt(this)

