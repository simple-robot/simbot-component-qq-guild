package love.forte.simbot.tencentguild.api.message

import io.ktor.http.*
import kotlinx.serialization.DeserializationStrategy
import love.forte.simbot.ID
import love.forte.simbot.tencentguild.TencentMessage
import love.forte.simbot.tencentguild.api.RouteInfoBuilder
import love.forte.simbot.tencentguild.api.TencentApi


/**
 *
 * [发送消息](https://bot.q.qq.com/wiki/develop/api/openapi/message/post_messages.html)
 *
 * - 要求操作人在该子频道具有发送消息的权限。
 * - 发送成功之后，会触发一个创建消息的事件。
 * - 被动回复消息有效期为 5 分钟
 * - 主动推送消息每日每个子频道限 2 条
 * - 发送消息接口要求机器人接口需要链接到websocket gateway 上保持在线状态
 *
 * <hr>
 *
 * [发送模板消息](https://bot.q.qq.com/wiki/develop/api/openapi/message/post_ark_messages.html)
 *
 * - 要求操作人在该子频道具有发送消息和 模板消息 的权限。
 * - 调用前需要先申请消息模板，这一步会得到一个模板id，在请求时填在ark.template_id上
 * - 发送成功之后，会触发一个创建消息的事件。
 * - 可用模板参考 [可用模板](https://bot.q.qq.com/wiki/develop/api/openapi/message/message_template.html)
 *
 *
 * @author ForteScarlet
 */
public class MessageSendApi(channelId: ID, override val body: TencentMessageForSending) :
    TencentApi<TencentMessage> {
    @JvmOverloads
    public constructor(channelId: ID, content: String, msgId: ID? = null) : this(
        channelId,
        TencentMessageForSending(content = content, msgId = msgId)
    )

    @JvmOverloads
    public constructor(channelId: ID, embed: TencentMessage.Embed, msgId: ID? = null) : this(
        channelId,
        TencentMessageForSending(embed = embed, msgId = msgId)
    )

    @JvmOverloads
    public constructor(channelId: ID, ark: TencentMessage.Ark, msgId: ID? = null) : this(
        channelId,
        TencentMessageForSending(ark = ark, msgId = msgId)
    )

    // POST /channels/{channel_id}/messages
    private val path = listOf("channels", channelId.toString(), "messages")

    override val resultDeserializer: DeserializationStrategy<out TencentMessage>
        get() = serializer

    override val method: HttpMethod
        get() = HttpMethod.Post

    override fun route(builder: RouteInfoBuilder) {
        builder.apiPath = path
    }

    public companion object {
        private val serializer = TencentMessage.serializer
    }

}