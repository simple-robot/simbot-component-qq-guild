package love.forte.simbot.tencentguild.api.announces

import io.ktor.http.*
import love.forte.simbot.ID
import love.forte.simbot.tencentguild.api.RouteInfoBuilder
import love.forte.simbot.tencentguild.api.TencentApiWithoutResult


/**
 *
 * [删除子频道公告](https://bot.q.qq.com/wiki/develop/api/openapi/announces/delete_channel_announces.html)
 *
 * 机器人删除指定子频道公告
 *
 * @author ForteScarlet
 */
public class DeleteAnnouncesApi(
    channelId: ID,
    messageId: ID
) : TencentApiWithoutResult {

    // DELETE /channels/{channel_id}/announces/{message_id}
    private val path = listOf("channels", channelId.toString(), "announces", messageId.toString())

    override val method: HttpMethod
        get() = HttpMethod.Delete

    override fun route(builder: RouteInfoBuilder) {
        builder.apiPath = path
    }

    override val body: Any? = null
}
