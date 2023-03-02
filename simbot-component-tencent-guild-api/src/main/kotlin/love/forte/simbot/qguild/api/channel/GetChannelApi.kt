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

package love.forte.simbot.qguild.api.channel

import kotlinx.serialization.DeserializationStrategy
import love.forte.simbot.qguild.api.GetTencentApi
import love.forte.simbot.qguild.api.RouteInfoBuilder
import love.forte.simbot.qguild.model.SimpleChannel

/**
 *
 * [获取子频道信息](https://bot.q.qq.com/wiki/develop/api/openapi/channel/get_channel.html)
 *
 * @author ForteScarlet
 */
public class GetChannelApi private constructor(channelId: String) : GetTencentApi<SimpleChannel>() {

    public companion object Factory {

        /**
         * 构造 [GetChannelApi]
         */
        @JvmStatic
        public fun create(channelId: String): GetChannelApi = GetChannelApi(channelId)
    }

    // GET /channels/{channel_id}
    private val path = arrayOf("channels", channelId)

    override val resultDeserializer: DeserializationStrategy<SimpleChannel>
        get() = SimpleChannel.serializer()

    override fun route(builder: RouteInfoBuilder) {
        builder.apiPath = path
    }
}
