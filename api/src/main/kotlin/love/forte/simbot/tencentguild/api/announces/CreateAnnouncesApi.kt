/*
 *  Copyright (c) 2022-2022 ForteScarlet <ForteScarlet@163.com>
 *
 *  根据 GNU LESSER GENERAL PUBLIC LICENSE 3 获得许可；
 *  除非遵守许可，否则您不得使用此文件。
 *  您可以在以下网址获取许可证副本：
 *
 *       https://www.gnu.org/licenses/lgpl-3.0-standalone.html
 *
 *   有关许可证下的权限和限制的具体语言，请参见许可证。
 */

package love.forte.simbot.tencentguild.api.announces

import io.ktor.http.*
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.ID
import love.forte.simbot.tencentguild.TencentAnnounces
import love.forte.simbot.tencentguild.api.RouteInfoBuilder
import love.forte.simbot.tencentguild.api.TencentApi


/**
 *
 * [创建子频道公告](https://bot.q.qq.com/wiki/develop/api/openapi/announces/post_channel_announces.html)
 *
 * 机器人设置消息为指定子频道公告
 *
 * @author ForteScarlet
 */
public class CreateAnnouncesApi(
    channelId: ID,
    messageId: ID
) : TencentApi<TencentAnnounces>() {

    // POST /channels/{channel_id}/announces
    private val path = listOf("channels", channelId.toString(), "announces")

    override val resultDeserializer: DeserializationStrategy<out TencentAnnounces>
        get() = TencentAnnounces.serializer

    override val method: HttpMethod
        get() = HttpMethod.Post

    override fun route(builder: RouteInfoBuilder) {
        builder.apiPath = path
    }

    override val body: Any = Body(messageId)


    @Serializable
    private data class Body(
        @SerialName("message_id")
        @Serializable(ID.AsCharSequenceIDSerializer::class)
        val messageId: ID
    )

}
