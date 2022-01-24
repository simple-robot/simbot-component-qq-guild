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