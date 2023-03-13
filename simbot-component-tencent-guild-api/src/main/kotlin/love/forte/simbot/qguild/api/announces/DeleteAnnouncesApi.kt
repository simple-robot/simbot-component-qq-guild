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

package love.forte.simbot.qguild.api.announces

import io.ktor.http.*
import love.forte.simbot.qguild.api.QQGuildApiWithoutResult
import love.forte.simbot.qguild.api.RouteInfoBuilder
import love.forte.simbot.qguild.api.SimpleApiDescription


/**
 *
 * [删除子频道公告](https://bot.q.qq.com/wiki/develop/api/openapi/announces/delete_channel_announces.html)
 *
 * 机器人删除指定子频道公告
 *
 * @author ForteScarlet
 */
public class DeleteAnnouncesApi private constructor(
    channelId: String,
    messageId: String,
) : QQGuildApiWithoutResult() {

    public companion object Factory : SimpleApiDescription(
        HttpMethod.Delete, "/channels/{channel_id}/announces/{message_id}"
    ) {

        /**
         * 构造 [DeleteAnnouncesApi]
         */
        @JvmStatic
        public fun create(channelId: String, messageId: String): DeleteAnnouncesApi =
            DeleteAnnouncesApi(channelId, messageId)
    }

    private val path = arrayOf("channels", channelId, "announces", messageId)

    override val method: HttpMethod
        get() = HttpMethod.Delete

    override fun route(builder: RouteInfoBuilder) {
        builder.apiPath = path
    }

    override val body: Any? = null
}
