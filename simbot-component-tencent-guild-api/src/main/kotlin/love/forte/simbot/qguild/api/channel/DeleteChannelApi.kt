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

import io.ktor.http.*
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.builtins.serializer
import love.forte.simbot.qguild.PrivateDomainOnly
import love.forte.simbot.qguild.api.RouteInfoBuilder
import love.forte.simbot.qguild.api.TencentApi


/**
 * [删除子频道](https://bot.q.qq.com/wiki/develop/api/openapi/channel/delete_channel.html)
 *
 * 用于删除 `channel_id` 指定的子频道。
 *
 * - 要求操作人具有 `管理子频道` 的权限，如果是机器人，则需要将机器人设置为管理员。
 * - 修改成功后，会触发子频道删除事件。
 *
 * ### 注意
 * 子频道的删除是无法撤回的，一旦删除，将无法恢复。
 *
 * @author ForteScarlet
 */
@PrivateDomainOnly
public class DeleteChannelApi private constructor(channelId: String) : TencentApi<Unit>() {
    public companion object Factory {

        /**
         * 构造 [DeleteChannelApi].
         *
         */
        @JvmStatic
        public fun create(channelId: String): DeleteChannelApi = DeleteChannelApi(channelId)

    }

    // DELETE /channels/{channel_id}
    private val path = arrayOf("channels", channelId)

    override val resultDeserializer: DeserializationStrategy<Unit>
        get() = Unit.serializer()

    override val method: HttpMethod
        get() = HttpMethod.Delete

    override fun route(builder: RouteInfoBuilder) {
        builder.apiPath = path
    }

    override val body: Any?
        get() = null
}
