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
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.qguild.ApiModel
import love.forte.simbot.qguild.api.QQGuildApi
import love.forte.simbot.qguild.api.RouteInfoBuilder
import love.forte.simbot.qguild.api.SimpleApiDescription
import kotlin.jvm.JvmStatic

/**
 * [发表帖子](https://bot.q.qq.com/wiki/develop/api/openapi/forum/put_thread.html)
 *
 *
 * @author ForteScarlet
 */
public class PublishThreadApi private constructor(
    channelId: String, private val _body: Body
) : QQGuildApi<ThreadPublishResult>() {
    public companion object Factory : SimpleApiDescription(HttpMethod.Put, "/channels/{channel_id}/threads") {

        /**
         * 构造 [PublishThreadApi]
         *
         * @param channelId 频道ID
         * @param title 帖子标题
         * @param content 帖子内容
         * @param format 帖子文本格式
         */
        @JvmStatic
        public fun create(channelId: String, title: String, content: String, format: Int): PublishThreadApi =
            PublishThreadApi(channelId, Body(title, content, format))

        /**
         * 构造 [PublishThreadApi]
         *
         * @param channelId 频道ID
         * @param title 帖子标题
         * @param content 帖子内容
         * @param format 帖子文本格式。see [Format]
         */
        @JvmStatic
        public fun create(channelId: String, title: String, content: String, format: Format): PublishThreadApi =
            create(channelId, title, content, format.value)

        /**
         * 构造 [PublishThreadApi]，并使用 [Format.TEXT] 文本格式。
         *
         * @param channelId 频道ID
         * @param title 帖子标题
         * @param content 帖子内容
         *
         * @see create
         */
        @JvmStatic
        public fun createText(channelId: String, title: String, content: String): PublishThreadApi =
            create(channelId, title, content, Format.TEXT)

        /**
         * 构造 [PublishThreadApi]，并使用 [Format.HTML] 文本格式。
         *
         * @param channelId 频道ID
         * @param title 帖子标题
         * @param content 帖子内容
         *
         * @see create
         */
        @JvmStatic
        public fun createHtml(channelId: String, title: String, content: String): PublishThreadApi =
            create(channelId, title, content, Format.HTML)

        /**
         * 构造 [PublishThreadApi]，并使用 [Format.MARKDOWN] 文本格式。
         *
         * @param channelId 频道ID
         * @param title 帖子标题
         * @param content 帖子内容
         *
         * @see create
         */
        @JvmStatic
        public fun createMarkdown(channelId: String, title: String, content: String): PublishThreadApi =
            create(channelId, title, content, Format.MARKDOWN)

        /**
         * 构造 [PublishThreadApi]，并使用 [Format.JSON] 文本格式。
         *
         * @param channelId 频道ID
         * @param title 帖子标题
         * @param content 帖子内容
         *
         * @see create
         */
        @JvmStatic
        public fun createJSON(channelId: String, title: String, content: String): PublishThreadApi =
            create(channelId, title, content, Format.JSON)

    }

    override val method: HttpMethod
        get() = HttpMethod.Put

    private val path = arrayOf("channels", channelId, "threads")

    override val resultDeserializer: DeserializationStrategy<ThreadPublishResult>
        get() = ThreadPublishResult.serializer()

    override fun route(builder: RouteInfoBuilder) {
        builder.apiPath = path
    }

    override val body: Any get() = _body

    @Serializable
    private data class Body(val title: String, val content: String, val format: Int)


    /**
     * 帖子文本格式
     *
     * 参考 [文档](https://bot.q.qq.com/wiki/develop/api/openapi/forum/put_thread.html)
     *
     */
    public enum class Format(public val value: Int) {
        /**
         * 普通文本
         */
        TEXT(1),

        /**
         * HTML
         */
        HTML(2),

        /**
         * Markdown
         */
        MARKDOWN(3),

        /**
         * JSON（content参数可参照RichText结构）
         */
        JSON(4),
    }

}


/**
 * [PublishThreadApi] 响应体。
 *
 * @property taskId 帖子任务ID
 * @property createTime 发帖时间戳，单位：秒
 *
 */
@ApiModel
@Serializable
public data class ThreadPublishResult(
    @SerialName("task_id") val taskId: String,
    @SerialName("create_time") val createTime: String
)
