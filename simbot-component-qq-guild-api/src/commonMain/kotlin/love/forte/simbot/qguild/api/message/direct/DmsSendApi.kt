/*
 * Copyright (c) 2022-2023. ForteScarlet.
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

package love.forte.simbot.qguild.api.message.direct

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.StringFormat
import love.forte.simbot.qguild.ErrInfo
import love.forte.simbot.qguild.QQGuildApiException
import love.forte.simbot.qguild.api.*
import love.forte.simbot.qguild.api.message.MessageSendApi
import love.forte.simbot.qguild.api.message.toRealBody
import love.forte.simbot.qguild.model.Message
import kotlin.jvm.JvmStatic
import kotlin.jvm.JvmSynthetic

/**
 * [发送私信](https://bot.q.qq.com/wiki/develop/api/openapi/dms/post_dms_messages.html)
 * ## 接口
 * `POST /dms/{guild_id}/messages`
 *
 * ## 功能描述
 * 用于发送私信消息，前提是已经创建了私信会话。
 *
 * * 私信的 `guild_id` 在创建私信会话时以及私信消息事件中获取。
 * * 私信场景下，每个机器人每天可以对一个用户发 `2` 条主动消息。
 * * 私信场景下，每个机器人每天累计可以发 `200` 条主动消息。
 * * 私信场景下，被动消息没有条数限制。
 *
 * ## 参数
 * 和 [发送消息][MessageSendApi] 参数一致。
 *
 * ## 返回
 * 和 [发送消息][MessageSendApi] 返回一致。
 *
 *
 * @see MessageSendApi
 *
 * @author ForteScarlet
 */
public class DmsSendApi private constructor(
    guildId: String,
    body: MessageSendApi.Body, // TencentMessageForSending || MultiPartFormDataContent
) : PostQQGuildApi<Message>() {
    public companion object Factory : SimplePostApiDescription(
        "/channels/{channel_id}/messages"
    ) {

        /**
         * 构造 [CreateDmsApi]
         */
        @JvmStatic
        public fun create(guildId: String, body: MessageSendApi.Body): DmsSendApi = DmsSendApi(guildId, body)
    }

    override val body: Any = body.toRealBody(MessageSendApi.defaultJson)

    private val path = arrayOf("dms", guildId, "messages")

    override val resultDeserializer: DeserializationStrategy<Message>
        get() = Message.serializer()

    override val method: HttpMethod
        get() = HttpMethod.Post

    override fun route(builder: RouteInfoBuilder) {
        builder.apiPath = path
        if (body is MultiPartFormDataContent) {
            builder.contentType = ContentType.MultiPart.FormData
        }
    }

    /**
     * 使用当前API发送消息
     *
     * @throws MessageAuditedException 当响应状态为表示消息审核的  `304023`、`304024` 时
     * @throws Exception see [HttpClient.request], 可能会抛出任何ktor请求过程中的异常。
     * @throws love.forte.simbot.qguild.QQGuildApiException 请求过程中出现了错误（http状态码 !in 200 .. 300）
     */
    override suspend fun doRequest(client: HttpClient, server: Url, token: String, decoder: StringFormat): Message {
        return super.doRequest(client, server, token, decoder)
    }

    /**
     * 使用当前API发送消息
     *
     *
     * @throws MessageAuditedException 当响应状态为表示消息审核的 `304023`、`304024` 时
     * @throws Exception see [HttpClient.request], 可能会抛出任何ktor请求过程中的异常。
     * @throws love.forte.simbot.qguild.QQGuildApiException 请求过程中出现了错误（http状态码 !in 200 .. 300）
     */
    override suspend fun doRequestRaw(client: HttpClient, server: Url, token: String): String {
        val resp: HttpResponse
        val text = requestForText(client, server, token) { resp = it }

        checkStatus(text, DefaultErrInfoDecoder, resp.status)

        if (text.isEmpty() && resp.status.isSuccess()) {
            return "{}"
        }

        if (resp.status == HttpStatusCode.Accepted) {
            // decode as error data
            val errorInfo = DefaultErrInfoDecoder.decodeFromString(ErrInfo.serializer(), text)
            // maybe audited
            if (MessageAuditedException.isAuditResultCode(errorInfo.code)) {
                throw MessageAuditedException(
                    DefaultErrInfoDecoder.decodeFromJsonElement(MessageAudit.serializer(), errorInfo.data).messageAudit,
                    errorInfo,
                    resp.status.value,
                    resp.status.description
                )
            }

            throw QQGuildApiException(errorInfo, resp.status.value, resp.status.description)
        }

        return text
    }

}

/**
 * 构造 [CreateDmsApi]
 *
 * ```kotlin
 * val api = DmsSendApi.create(guildId) {
 *    // body builder
 * }
 * ```
 */
@JvmSynthetic
public inline fun DmsSendApi.Factory.create(
    guildId: String,
    builder: MessageSendApi.Body.Builder.() -> Unit
): DmsSendApi =
    create(guildId, MessageSendApi.Body.invoke(builder))
