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
import kotlinx.serialization.builtins.serializer
import love.forte.simbot.ID
import love.forte.simbot.literal
import love.forte.simbot.tencentguild.api.RouteInfoBuilder
import love.forte.simbot.tencentguild.api.TencentApi

/**
 * [撤回私信](https://bot.q.qq.com/wiki/develop/api/openapi/dms/delete_dms.html)
 *
 * ## 接口
 * `DELETE /dms/{guild_id}/messages/{message_id}?hidetip=false`
 *
 * ## 参数
 * | 字段名 | 类型 | 描述 |
 * |------|------|-----|
 * | `hidetip` | `bool` | 选填，是否隐藏提示小灰条，true 为隐藏，false 为显示。默认为false |
 *
 * ## 功能描述
 * 用于撤回私信频道 `guild_id` 中 `message_id` 指定的私信消息。只能用于撤回机器人自己发送的私信。
 *
 * ### 注意
 * * 公域机器人暂不支持申请，仅私域机器人可用，选择私域机器人后默认开通。
 * * 注意: 开通后需要先将机器人从频道移除，然后重新添加，方可生效。
 *
 *
 * @param hidetip 是否隐藏提示小灰条，`true` 为隐藏，`false` 为显示。默认为 `false`
 *
 *
 *
 *
 * @author ForteScarlet
 */
public class DeleteDmsApi private constructor(
    guildId: ID,
    messageId: ID,
    private val hidetip: Boolean = false,
) : TencentApi<Unit>() {
    public companion object Factory {
        
        /**
         * 构造 [DeleteDmsApi]
         *
         */
        @JvmStatic
        @JvmOverloads
        public fun create(guildId: ID, messageId: ID, hidetip: Boolean = false): DeleteDmsApi =
            DeleteDmsApi(guildId, messageId, hidetip)
        
    }
    
    override val resultDeserializer: DeserializationStrategy<Unit>
        get() = Unit.serializer()
    
    override val method: HttpMethod
        get() = HttpMethod.Delete
    
    // /dms/{guild_id}/messages/{message_id}
    private val path = arrayOf("dms", guildId.literal, "messages", messageId.literal)
    
    override fun route(builder: RouteInfoBuilder) {
        builder.apiPath = path
        builder.parametersAppender.append("hidetip", hidetip)
    }
    
    override val body: Any?
        get() = null
}
