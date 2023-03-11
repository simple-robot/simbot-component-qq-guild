/*
 * Copyright (c) 2023. ForteScarlet.
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
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import love.forte.simbot.qguild.api.GetTencentApi
import love.forte.simbot.qguild.api.RouteInfoBuilder
import love.forte.simbot.qguild.api.SimpleGetApiDescription


/**
 * [获取在线成员数](https://bot.q.qq.com/wiki/develop/api/openapi/channel/get_online_nums.html)
 *
 * 用于查询音视频/直播子频道 channel_id 的在线成员数。
 *
 * @author ForteScarlet
 */
public class GetChannelOnlineNumsApi(channelId: String) : GetTencentApi<Int>() {
    public companion object Factory : SimpleGetApiDescription(
        "/channels/{channel_id}/online_nums"
    ) {

        /**
         * 构造 [GetChannelOnlineNumsApi]
         */
        @JvmStatic
        public fun create(channelId: String): GetChannelOnlineNumsApi = GetChannelOnlineNumsApi(channelId)
    }

    private val path = arrayOf("channels", channelId, "online_nums")

    override val resultDeserializer: DeserializationStrategy<Int>
        get() = OnlineNumsToIntDeserializationStrategy

    override fun route(builder: RouteInfoBuilder) {
        builder.apiPath = path
    }
}


private object OnlineNumsToIntDeserializationStrategy : DeserializationStrategy<Int> {
    @Serializable
    private data class OnlineNumsResult(@SerialName("online_nums") val onlineNums: Int)

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("OnlineNums", PrimitiveKind.INT)

    override fun deserialize(decoder: Decoder): Int {
        val decoded = OnlineNumsResult.serializer().deserialize(decoder)
        return decoded.onlineNums
    }
}
