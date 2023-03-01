/*
 * Copyright (c) 2022-2023. ForteScarlet.
 *
 * This file is part of simbot-component-tencent-guild.
 *
 * simbot-component-tencent-guild is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * simbot-component-tencent-guild is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with simbot-component-tencent-guild. If not, see <https://www.gnu.org/licenses/>.
 */

package love.forte.simbot.tencentguild.api.message.direct

import io.ktor.http.*
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.ID
import love.forte.simbot.literal
import love.forte.simbot.tencentguild.DirectMessageSession
import love.forte.simbot.tencentguild.api.RouteInfoBuilder
import love.forte.simbot.tencentguild.api.TencentApi

/**
 * [创建私信会话](https://bot.q.qq.com/wiki/develop/api/openapi/dms/post_dms.html)
 * ## 接口
 * `POST /users/@me/dms`
 *
 * ## 功能描述
 * 用于机器人和在同一个频道内的成员创建私信会话。
 *
 * 机器人和用户存在共同频道才能创建私信会话。
 * 创建成功后，返回创建成功的频道 `id` ，子频道 `id` 和创建时间。
 *
 * ## 参数
 * | 字段名 | 类型 | 描述 |
 * |-----|-----|-----|
 * | `recipient_id` | `string` | 接收者 id |
 * | `source_guild_id` | `string` | 源频道 id |
 *
 *
 * @author ForteScarlet
 */
public class CreateDmsApi private constructor(
    recipientId: ID,
    sourceGuildId: ID,
) : TencentApi<DirectMessageSession>() {
    public companion object Factory {
        // POST /users/@me/dms
        private val path = arrayOf("users", "@me", "dms")
    
        /**
         * 构造 [CreateDmsApi].
         *
         */
        @JvmStatic
        public fun create(recipientId: ID, sourceGuildId: ID): CreateDmsApi = CreateDmsApi(recipientId, sourceGuildId)
    }
    
    override val resultDeserializer: DeserializationStrategy<DirectMessageSession>
        get() = DirectMessageSession.serializer
    
    override val method: HttpMethod
        get() = HttpMethod.Post
    
    override fun route(builder: RouteInfoBuilder) {
        builder.apiPath = path
        builder.contentType
    }
    
    private val _body = Body(recipientId.literal, sourceGuildId.literal)
    
    override val body: Any get() = _body
    
    @Serializable
    private data class Body(
        @SerialName("recipient_id") private val recipientId: String,
        @SerialName("source_guild_id") private val sourceGuildId: String,
    )
}
