package love.forte.simbot.tencentguild.api.message

import kotlinx.serialization.DeserializationStrategy
import love.forte.simbot.ID
import love.forte.simbot.tencentguild.TencentMessage
import love.forte.simbot.tencentguild.api.GetTencentApi
import love.forte.simbot.tencentguild.api.RouteInfoBuilder


/**
 * [获取指定消息](https://bot.q.qq.com/wiki/develop/api/openapi/message/get_message_of_id.html)
 *
 * @author ForteScarlet
 */
public class GetMessageApi(channelId: ID, messageId: ID) : GetTencentApi<TencentMessage>() {
    // GET /channels/{channel_id}/messages/{message_id}
    private val path = listOf("channels", channelId.toString(), "messages", messageId.toString())

    override val resultDeserializer: DeserializationStrategy<out TencentMessage>
        get() = TencentMessage.serializer

    override fun route(builder: RouteInfoBuilder) {
        builder.apiPath = path
    }
}