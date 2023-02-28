/*
 *  Copyright (c) 2022-2022 ForteScarlet <ForteScarlet@163.com>
 *
 *  本文件是 simbot-component-tencent-guild 的一部分。
 *
 *  simbot-component-tencent-guild 是自由软件：你可以再分发之和/或依照由自由软件基金会发布的 GNU 通用公共许可证修改之，无论是版本 3 许可证，还是（按你的决定）任何以后版都可以。
 *
 *  发布 simbot-component-tencent-guild 是希望它能有用，但是并无保障;甚至连可销售和符合某个特定的目的都不保证。请参看 GNU 通用公共许可证，了解详情。
 *
 *  你应该随程序获得一份 GNU 通用公共许可证的复本。如果没有，请看:
 *  https://www.gnu.org/licenses
 *  https://www.gnu.org/licenses/gpl-3.0-standalone.html
 *  https://www.gnu.org/licenses/lgpl-3.0-standalone.html
 *
 *
 */

package love.forte.simbot.tencentguild.api.announces

import io.ktor.http.*
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.tencentguild.api.PostTencentApi
import love.forte.simbot.tencentguild.api.RouteInfoBuilder
import love.forte.simbot.tencentguild.model.Announces


/**
 *
 * 机器人设置消息为指定子频道公告。
 *
 * [创建子频道公告](https://bot.q.qq.com/wiki/develop/api/openapi/announces/post_channel_announces.html)
 *
 *
 * @author ForteScarlet
 */
public class CreateAnnouncesApi private constructor(
    channelId: String,
    messageId: String,
) : PostTencentApi<Announces>() {

    public companion object Factory {

        /**
         * 构建 [CreateAnnouncesApi]
         * @param channelId 频道ID
         * @param messageId 消息ID
         */
        @JvmStatic
        public fun create(channelId: String, messageId: String): CreateAnnouncesApi =
            CreateAnnouncesApi(channelId, messageId)

    }


    // POST /channels/{channel_id}/announces
    private val path = arrayOf("channels", channelId, "announces")

    override val resultDeserializer: DeserializationStrategy<Announces>
        get() = Announces.serializer()

    override fun route(builder: RouteInfoBuilder) {
        builder.apiPath = path
    }

    override val body: Any = Body(messageId)


    @Serializable
    private data class Body(
        @SerialName("message_id") val messageId: String,
    )


}
