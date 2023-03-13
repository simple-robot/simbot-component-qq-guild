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
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.qguild.PrivateDomainOnly
import love.forte.simbot.qguild.api.QQGuildApi
import love.forte.simbot.qguild.api.RouteInfoBuilder
import love.forte.simbot.qguild.api.SimpleApiDescription
import love.forte.simbot.qguild.model.PrivateType
import love.forte.simbot.qguild.model.SimpleChannel
import love.forte.simbot.qguild.model.SpeakPermission


/**
 * [修改子频道](https://bot.q.qq.com/wiki/develop/api/openapi/channel/patch_channel.html)
 *
 * 用于修改 `channel_id` 指定的子频道的信息。
 *
 * - 要求操作人具有 `管理子频道` 的权限，如果是机器人，则需要将机器人设置为管理员。
 * - 修改成功后，会触发子频道更新事件。
 *
 * @author ForteScarlet
 */
@PrivateDomainOnly
public class ModifyChannelApi private constructor(
    channelId: String, override val body: Body
) : QQGuildApi<SimpleChannel>() {
    public companion object Factory : SimpleApiDescription(
        HttpMethod.Patch, "/channels/{channel_id}"
    ) {

        /**
         * 构造 [ModifyChannelApi].
         *
         */
        @JvmStatic
        public fun create(channelId: String, body: Body): ModifyChannelApi = ModifyChannelApi(channelId, body)

    }

    private val path = arrayOf("channels", channelId)

    override val resultDeserializer: DeserializationStrategy<SimpleChannel>
        get() = SimpleChannel.serializer()

    override val method: HttpMethod
        get() = HttpMethod.Patch

    override fun route(builder: RouteInfoBuilder) {
        builder.apiPath = path
    }

    /**
     * 用于 [ModifyChannelApi] 的请求体。
     *
     * 需要修改哪个字段，就传递哪个字段即可。
     *
     */
    @Serializable
    public data class Body(
        /**
         * 子频道名
         */
        val name: String? = null,
        /**
         * 排序
         */
        val position: Int? = null,
        /**
         * 分组 id
         */
        @SerialName("parent_id") val parentId: String? = null,
        /**
         * 子频道私密类型 [PrivateType]
         */
        @SerialName("private_type") val privateType: PrivateType? = null,
        /**
         * 子频道发言权限 [SpeakPermission]
         */
        @SerialName("speak_permission") val speakPermission: SpeakPermission? = null
    )
}
