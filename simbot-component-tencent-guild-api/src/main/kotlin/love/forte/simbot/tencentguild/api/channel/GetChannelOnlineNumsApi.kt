package love.forte.simbot.tencentguild.api.channel

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import love.forte.simbot.tencentguild.api.GetTencentApi
import love.forte.simbot.tencentguild.api.RouteInfoBuilder


/**
 * [获取在线成员数](https://bot.q.qq.com/wiki/develop/api/openapi/channel/get_online_nums.html)
 *
 * 用于查询音视频/直播子频道 channel_id 的在线成员数。
 *
 * @author ForteScarlet
 */
public class GetChannelOnlineNumsApi(channelId: String) : GetTencentApi<Int>() {
    public companion object Factory {

        /**
         * 构造 [GetChannelOnlineNumsApi]
         */
        @JvmStatic
        public fun create(channelId: String): GetChannelOnlineNumsApi = GetChannelOnlineNumsApi(channelId)
    }

    // GET /channels/{channel_id}/online_nums
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
