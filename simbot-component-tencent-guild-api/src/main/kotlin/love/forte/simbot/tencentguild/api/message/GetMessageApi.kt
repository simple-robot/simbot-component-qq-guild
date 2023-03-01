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

package love.forte.simbot.tencentguild.api.message

import kotlinx.serialization.DeserializationStrategy
import love.forte.simbot.tencentguild.api.GetTencentApi
import love.forte.simbot.tencentguild.api.RouteInfoBuilder
import love.forte.simbot.tencentguild.model.Message


/**
 * [获取指定消息](https://bot.q.qq.com/wiki/develop/api/openapi/message/get_message_of_id.html)
 *
 * 用于获取子频道 `channel_id` 下的消息 `message_id` 的详情。
 *
 * @author ForteScarlet
 */
public class GetMessageApi private constructor(channelId: String, messageId: String) : GetTencentApi<Message>() {
    public companion object Factory {
        /**
         * 构造 [GetMessageApi]
         */
        @JvmStatic
        public fun create(channelId: String, messageId: String): GetMessageApi = GetMessageApi(channelId, messageId)
    }

    // GET /channels/{channel_id}/messages/{message_id}
    private val path = arrayOf("channels", channelId, "messages", messageId)

    override val resultDeserializer: DeserializationStrategy<Message>
        get() = Message.serializer()

    override fun route(builder: RouteInfoBuilder) {
        builder.apiPath = path
    }
}
