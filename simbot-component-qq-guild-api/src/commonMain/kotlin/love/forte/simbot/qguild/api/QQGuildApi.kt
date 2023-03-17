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

package love.forte.simbot.qguild.api

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.client.utils.*
import io.ktor.http.*
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.StringFormat
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import love.forte.simbot.logger.LoggerFactory
import love.forte.simbot.qguild.ErrInfo
import love.forte.simbot.qguild.QQGuildApiException
import kotlin.jvm.JvmSynthetic

private val apiLogger = LoggerFactory.getLogger("love.forte.simbot.qguild.api")

/**
 * [有关 traceID](https://bot.q.qq.com/wiki/develop/api/openapi/error/error.html#%E6%9C%89%E5%85%B3-traceid)
 *
 * 在 openapi 的返回 http 头上，有一个 X-Tps-trace-ID 自定义头部，是平台的链路追踪 ID，
 * 如果开发者有无法自己定位的问题，需要找平台协助的时候，可以提取这个 ID，提交给平台方。
 *
 * 方便查询相关日志。
 *
 */
private const val TRACE_ID_HEAD = "X-Tps-trace-ID"


/**
 * 用于多平台实现的最小目标。
 *
 * 在JVM平台和JS平台中分别提供对应的 blocking/async 兼容函数。
 * 但是不应追加新的抽象函数。
 *
 * @suppress
 */
public expect abstract class BaseQQGuildApi<out R>() {

    /**
     * 使用此api发起一次请求，并得到预期中的结果。如果返回了代表错误的响应值
     *
     * @see ErrInfo
     *
     * @throws Exception see [HttpClient.request], 可能会抛出任何ktor请求过程中的异常。
     * @throws love.forte.simbot.qguild.QQGuildApiException 请求过程中出现了错误。
     */
    @JvmSynthetic
    public abstract suspend fun doRequest(
        client: HttpClient,
        server: Url,
        token: String,
        decoder: StringFormat = Json,
    ): R

    public companion object
}


/**
 * 表示为一个QQ频道的API。
 *
 * 通过 [doRequest] 发起一次请求。
 *
 * ### 日志
 *
 * [QQGuildApi] 在进行请求的过程中会通过名称 `love.forte.simbot.qguild.api`
 * 输出 `DEBUG` 日志，其中：
 * ```
 * [   GET /foo/bar] =====> api.xxx.com, body: xxx
 * ```
 * 向右箭头的日志代表 `request` 相关的信息，
 * ```
 * [   GET /foo/bar] <===== status: xxx, body: xxx
 * ```
 * 向左的日志则代表 `response` 相关的信息。
 *
 * 如果希望关闭日志中 `request method` 的染色，添加JVM参数
 * ```
 * -Dsimbot.qguild.api.logger.color.enable=false
 * ```
 *
 *
 *
 * @see QQGuildCacheableApi
 */
public abstract class QQGuildApi<out R> : BaseQQGuildApi<R>() {

    /**
     * 得到响应值的反序列化器.
     */
    public abstract val resultDeserializer: DeserializationStrategy<R>


    /**
     * 此api请求方式
     */
    public abstract val method: HttpMethod


    /**
     * 此请求对应的api路由路径以及路径参数。
     * 例如：`/guild/list`
     */
    public abstract fun route(builder: RouteInfoBuilder)


    /**
     * 此次请求所发送的数据。为null则代表没有参数。
     */
    public abstract val body: Any?


    /**
     * Do something after resp.
     */
    public open fun post(resp: @UnsafeVariance R) {}

    /**
     * 使用此api发起一次请求，并得到预期中的结果。如果返回了代表错误的响应值
     *
     * @see ErrInfo
     *
     * @throws Exception see [HttpClient.request], 可能会抛出任何ktor请求过程中的异常。
     * @throws love.forte.simbot.qguild.QQGuildApiException 请求过程中出现了错误。
     */
    @JvmSynthetic
    public override suspend fun doRequest(
        client: HttpClient,
        server: Url,
        token: String,
        decoder: StringFormat,
    ): R {
        val resp = request0(client, server, token)

        val text = resp.bodyAsText()

        val traceId = resp.headers[TRACE_ID_HEAD]
        apiLogger.debug(
            "[{} {}] <===== status: {}, body: {}, traceID: {}",
            method.logName,
            resp.request.url.encodedPath,
            resp.status,
            text,
            traceId
        )

        checkStatus(text, decoder, resp.status)

        // decode
        return decodeResponse(decoder, text)
    }
}


private suspend fun QQGuildApi<*>.request0(client: HttpClient, server: Url, token: String): HttpResponse {
    val api = this

    return client.request {
        method = api.method

        headers {
            this[HttpHeaders.Authorization] = token
        }


        url {
            takeFrom(server)

            // route builder
            val routeBuilder = RouteInfoBuilder { name, value ->
                parameters.append(name, value.toString())
            }

            api.route(routeBuilder)
            setBody(api.body ?: EmptyContent)

            routeBuilder.apiPath?.let { apiPath -> appendPathSegments(components = apiPath) }
            routeBuilder.contentType?.let {
                headers {
                    this[HttpHeaders.ContentType] = it.toString()
                }
            }
        }

        apiLogger.debug("[{} /{}] =====> {}, body: {}", method.logName, url.encodedPath, url.host, api.body)
    }
}


private fun <R> QQGuildApi<R>.decodeResponse(
    decoder: StringFormat, remainingText: String
): R {
    return decoder.decodeFromString(resultDeserializer, remainingText)
}

private fun checkStatus(
    remainingText: String,
    decoder: StringFormat,
    status: HttpStatusCode
) {
    if (!status.isSuccess()) {
        val info = decoder.decodeFromString(ErrInfo.serializer(), remainingText)
        // throw err
        throw QQGuildApiException(info, status.value, status.description)
    }
}

public abstract class QQGuildApiWithoutResult : QQGuildApi<Unit>() {
    override val resultDeserializer: DeserializationStrategy<Unit>
        get() = Unit.serializer()
}

public abstract class GetQQGuildApi<R> : QQGuildApi<R>() {
    override val method: HttpMethod
        get() = HttpMethod.Get

    override val body: Any?
        get() = null
}

public abstract class PostQQGuildApi<R> : QQGuildApi<R>() {
    override val method: HttpMethod
        get() = HttpMethod.Post

}

internal fun HttpMethod.defaultForLogName(): String = when (this) {
    HttpMethod.Get -> "   GET"
    HttpMethod.Post -> "  POST"
    HttpMethod.Put -> "   PUT"
    HttpMethod.Patch -> " PATCH"
    HttpMethod.Delete -> "DELETE"
    else -> value
}

/**
 * 日志对齐
 */
internal expect val HttpMethod.logName: String
