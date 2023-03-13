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

package love.forte.simbot.qguild.api.message.direct

import io.ktor.http.*
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.builtins.serializer
import love.forte.simbot.qguild.PrivateDomainOnly
import love.forte.simbot.qguild.api.QQGuildApi
import love.forte.simbot.qguild.api.RouteInfoBuilder
import love.forte.simbot.qguild.api.SimpleApiDescription

/**
 * [撤回私信](https://bot.q.qq.com/wiki/develop/api/openapi/dms/delete_dms.html)
 *
 * 用于撤回私信频道 `guild_id` 中 `message_id` 指定的私信消息。只能用于撤回机器人自己发送的私信。
 *
 * @param hidetip 是否隐藏提示小灰条，`true` 为隐藏，`false` 为显示。默认为 `false`
 *
 * @author ForteScarlet
 */
@PrivateDomainOnly
public class DeleteDmsApi private constructor(
    guildId: String,
    messageId: String,
    private val hidetip: Boolean? = null,
) : QQGuildApi<Unit>() {
    public companion object Factory : SimpleApiDescription(
        HttpMethod.Delete, "/dms/{guild_id}/messages/{message_id}"
    ) {

        /**
         * 构造 [DeleteDmsApi]
         *
         * @param hidetip 选填，是否隐藏提示小灰条，true 为隐藏，false 为显示。默认为false
         *
         */
        @JvmStatic
        @JvmOverloads
        public fun create(guildId: String, messageId: String, hidetip: Boolean? = null): DeleteDmsApi =
            DeleteDmsApi(guildId, messageId, hidetip)

    }

    override val resultDeserializer: DeserializationStrategy<Unit>
        get() = Unit.serializer()

    override val method: HttpMethod
        get() = HttpMethod.Delete

    private val path = arrayOf("dms", guildId, "messages", messageId)

    override fun route(builder: RouteInfoBuilder) {
        builder.apiPath = path
        hidetip?.also { builder.parametersAppender.append("hidetip", it) }

    }

    override val body: Any?
        get() = null
}
