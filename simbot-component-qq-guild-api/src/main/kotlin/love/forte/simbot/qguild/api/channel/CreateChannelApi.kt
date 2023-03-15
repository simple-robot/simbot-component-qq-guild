/*
 * Copyright (c) 2023. ForteScarlet.
 *
 * This file is part of simbot-component-qq-guild.
 *
 * simbot-component-qq-guild is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * simbot-component-qq-guild is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with simbot-component-qq-guild. If not, see <https://www.gnu.org/licenses/>.
 */

package love.forte.simbot.qguild.api.channel

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.qguild.PrivateDomainOnly
import love.forte.simbot.qguild.api.PostQQGuildApi
import love.forte.simbot.qguild.api.RouteInfoBuilder
import love.forte.simbot.qguild.api.SimplePostApiDescription
import love.forte.simbot.qguild.model.*


/**
 * [创建子频道](https://bot.q.qq.com/wiki/develop/api/openapi/channel/post_channels.html)
 *
 * 用于在 `guild_id` 指定的频道下创建一个子频道。
 *
 * - 要求操作人具有管理频道的权限，如果是机器人，则需要将机器人设置为管理员。
 * - 创建成功后，返回创建成功的子频道对象，同时会触发一个频道创建的事件通知。
 *
 * @author ForteScarlet
 */
@PrivateDomainOnly
public class CreateChannelApi private constructor(
    guildId: String,
    override val body: Body
) : PostQQGuildApi<SimpleChannel>() {
    public companion object Factory : SimplePostApiDescription(
        "/guilds/{guild_id}/channels"
    ) {
        /**
         * 构造 [CreateChannelApi]
         *
         */
        @JvmStatic
        public fun create(guildId: String, body: Body): CreateChannelApi = CreateChannelApi(guildId, body)
    }

    private val path = arrayOf("guilds", guildId, "channels")

    override val resultDeserializer: DeserializationStrategy<SimpleChannel>
        get() = SimpleChannel.serializer()

    override fun route(builder: RouteInfoBuilder) {
        builder.apiPath = path
    }

    /**
     * [CreateChannelApi] 的请求体。
     */
    @Serializable
    public data class Body(
        /**
         * 子频道名称
         */
        val name: String,
        /**
         * 子频道类型 [ChannelType]
         */
        val type: ChannelType,
        /**
         * 子频道子类型 [ChannelSubType]
         */
        @SerialName("sub_type")
        val subType: ChannelSubType,
        /**
         * 子频道排序，必填；当子频道类型为 `子频道分组（ChannelType=4）` 时，必须大于等于 `2`
         */
        val position: Int,

        /**
         * 子频道所属分组ID
         */
        @SerialName("parent_id")
        val parentId: String,

        /**
         * 子频道私密类型 [PrivateType]
         */
        @SerialName("private_type")
        val privateType: PrivateType,

        /**
         * 子频道私密类型成员 ID
         */
        @SerialName("private_user_ids")
        val privateUserIds: List<String>,

        /**
         * 子频道发言权限 [SpeakPermission]
         */
        @SerialName("speak_permission")
        val speakPermission: SpeakPermission,

        /**
         * 应用类型子频道应用 AppID，仅应用子频道需要该字段
         */
        @SerialName("application_id")
        val applicationId: String? = null
    )
}
