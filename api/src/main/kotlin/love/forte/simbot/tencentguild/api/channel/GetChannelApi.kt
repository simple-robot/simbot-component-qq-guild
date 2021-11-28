package love.forte.simbot.tencentguild.api.channel

import kotlinx.serialization.DeserializationStrategy
import love.forte.simbot.ID
import love.forte.simbot.tencentguild.TencentChannelInfo
import love.forte.simbot.tencentguild.api.GetTencentApi
import love.forte.simbot.tencentguild.api.RouteInfoBuilder

/**
 *
 * [获取子频道信息](https://bot.q.qq.com/wiki/develop/api/openapi/channel/get_channel.html)
 *
 * @author ForteScarlet
 */
public class GetChannelApi(channelId: ID) : GetTencentApi<TencentChannelInfo> {
    // GET /channels/{channel_id}
    private val path = listOf("channels", channelId.toString())

    override val resultDeserializer: DeserializationStrategy<out TencentChannelInfo>
        get() = TencentChannelInfo.serializer

    override fun route(builder: RouteInfoBuilder) {
        builder.apiPath = path
    }
}