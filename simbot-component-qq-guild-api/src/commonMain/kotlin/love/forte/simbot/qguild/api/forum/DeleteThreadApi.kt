/*
 * Copyright (c) 2023. ForteScarlet.
 *
 * This file is part of simbot-component-qq-guild.
 *
 * simbot-component-qq-guild is free software: you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * simbot-component-qq-guild is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with simbot-component-qq-guild.
 * If not, see <https://www.gnu.org/licenses/>.
 */

package love.forte.simbot.qguild.api.forum

import io.ktor.http.*
import love.forte.simbot.qguild.PrivateDomainOnly
import love.forte.simbot.qguild.api.QQGuildApiWithoutResult
import love.forte.simbot.qguild.api.RouteInfoBuilder
import love.forte.simbot.qguild.api.SimpleApiDescription
import kotlin.jvm.JvmStatic

/**
 * [删除帖子](https://bot.q.qq.com/wiki/develop/api/openapi/forum/delete_thread.html)
 *
 * 该接口用于删除指定子频道下的某个帖子。
 *
 * @author ForteScarlet
 */
@PrivateDomainOnly
public class DeleteThreadApi(channelId: String, threadId: String) : QQGuildApiWithoutResult() {
    public companion object Factory :
        SimpleApiDescription(HttpMethod.Delete, "/channels/{channel_id}/threads/{thread_id}") {

        /**
         * 构造 [DeleteThreadApi].
         *
         * @param channelId 频道ID
         * @param threadId 帖子ID
         *
         */
        @JvmStatic
        public fun create(channelId: String, threadId: String): DeleteThreadApi =
            DeleteThreadApi(channelId, threadId)

    }

    override val method: HttpMethod
        get() = HttpMethod.Delete

    private val path = arrayOf("channels", channelId, "threads", threadId)

    override fun route(builder: RouteInfoBuilder) {
        builder.apiPath = path
    }

    override val body: Any? get() = null
}
