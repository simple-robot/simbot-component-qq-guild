package love.forte.simbot.tencentguild.api.message

import io.ktor.http.*
import love.forte.simbot.tencentguild.PrivateDomainOnly
import love.forte.simbot.tencentguild.api.RouteInfoBuilder
import love.forte.simbot.tencentguild.api.TencentApiWithoutResult

/**
 * [撤回消息](https://bot.q.qq.com/wiki/develop/api/openapi/message/delete_message.html)
 *
 * 用于撤回子频道 `channel_id` 下的消息 `message_id`。
 *
 * - 管理员可以撤回普通成员的消息。
 * - 频道主可以撤回所有人的消息。
 *
 * @author ForteScarlet
 */
@PrivateDomainOnly
public class DeleteMessageApi private constructor(
    channelId: String, messageId: String, private val hidetip: Boolean? = null
) : TencentApiWithoutResult() {
    public companion object Factory {

        /**
         * 构建 [DeleteMessageApi]
         *
         * @param hidetip 选填，是否隐藏提示小灰条，true 为隐藏，false 为显示。默认为fals
         */
        @JvmStatic
        @JvmOverloads
        public fun create(channelId: String, messageId: String, hidetip: Boolean? = null): DeleteMessageApi =
            DeleteMessageApi(channelId, messageId, hidetip)
    }

    override val method: HttpMethod
        get() = HttpMethod.Delete

    // DELETE /channels/{channel_id}/messages/{message_id}?hidetip=false
    private val path = arrayOf("channels", channelId, "messages", messageId)

    override fun route(builder: RouteInfoBuilder) {
        builder.apiPath = path
        hidetip?.also { builder.parametersAppender.append("hidetip", it) }
    }

    override val body: Any?
        get() = null
}

