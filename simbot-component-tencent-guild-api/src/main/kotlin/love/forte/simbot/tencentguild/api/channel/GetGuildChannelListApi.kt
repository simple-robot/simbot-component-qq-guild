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

package love.forte.simbot.tencentguild.api.channel

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.builtins.ListSerializer
import love.forte.simbot.tencentguild.api.GetTencentApi
import love.forte.simbot.tencentguild.api.RouteInfoBuilder
import love.forte.simbot.tencentguild.model.Channel


/**
 *
 * [获取子频道列表](https://bot.q.qq.com/wiki/develop/api/openapi/channel/get_channels.html)
 *
 * 用于获取 `guild_id` 指定的频道下的子频道列表。
 *
 * @author ForteScarlet
 */
public class GetGuildChannelListApi private constructor(guildId: String) : GetTencentApi<List<Channel>>() {
    public companion object Factory {
        private val serializer = ListSerializer(Channel.serializer())

        /**
         * 构造 [GetGuildChannelListApi]
         */
        @JvmStatic
        public fun create(guildId: String): GetGuildChannelListApi = GetGuildChannelListApi(guildId)
    }

    // GET /guilds/{guild_id}/channels
    private val path = arrayOf("guilds", guildId, "channels")

    override val resultDeserializer: DeserializationStrategy<List<Channel>>
        get() = serializer

    override fun route(builder: RouteInfoBuilder) {
        builder.apiPath = path
    }

}
