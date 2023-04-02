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
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.StringFormat
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.StructureKind
import kotlinx.serialization.json.Json
import love.forte.simbot.logger.LoggerFactory
import love.forte.simbot.logger.isDebugEnabled
import love.forte.simbot.qguild.ErrInfo
import love.forte.simbot.qguild.InternalApi
import love.forte.simbot.qguild.QQGuild
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
 * _仅内部平台实现用_
 *
 * @see QQGuildApi
 */
@InternalApi
public expect abstract class PlatformQQGuildApi<out R>() {

    /**
     * 使用此api发起一次请求，并得到预期中的结果。
     *
     * @param client 用于本次http请求的client。默认使用 [QQGuildApi.DefaultHttpClient]。
     * @param server 请求目标服务器。See also: [QQGuild.URL]、[QQGuild.SANDBOX_URL]。
     * @param token 用于本次请求鉴权的token。
     * @param decoder 用于本次请求结果的反序列化器。不出意外的话应该是 [Json] 序列化器，默认使用 [QQGuildApi.DefaultJsonDecoder]。
     *
     * @throws Exception see [HttpClient.request], 可能会抛出任何ktor请求过程中的异常。
     * @throws love.forte.simbot.qguild.QQGuildApiException 请求过程中出现了错误（http状态码 !in 200 .. 300）
     *
     * @see ErrInfo
     */
    @JvmSynthetic
    public abstract suspend fun doRequest(
        client: HttpClient = QQGuildApi.DefaultHttpClient,
        server: Url,
        token: String,
        decoder: StringFormat = QQGuildApi.DefaultJsonDecoder,
    ): R

    /**
     * 使用此api发起一次请求，并得到响应结果的字符串。
     *
     * @param client 用于本次http请求的client。默认使用 [QQGuildApi.DefaultHttpClient]。
     * @param server 请求目标服务器。See also: [QQGuild.URL]、[QQGuild.SANDBOX_URL]。
     * @param token 用于本次请求鉴权的token。
     *
     * @throws Exception see [HttpClient.request], 可能会抛出任何ktor请求过程中的异常。
     * @throws love.forte.simbot.qguild.QQGuildApiException 请求过程中出现了错误（http状态码 !in 200 .. 300）
     *
     * @see ErrInfo
     */
    @JvmSynthetic
    public abstract suspend fun doRequestRaw(
        client: HttpClient = QQGuildApi.DefaultHttpClient,
        server: Url,
        token: String,
    ): String

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
 */
public abstract class QQGuildApi<out R> : PlatformQQGuildApi<R>() {

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
     * 当通过 [doRequest] 得到成功结果后进行的操作。
     */
    public open fun post(resp: @UnsafeVariance R) {}

    /**
     * 使用此api发起一次请求，并得到预期中的结果。
     *
     *
     * @param client 用于本次http请求的client。默认使用 [QQGuildApi.DefaultHttpClient]。
     * @param server 请求目标服务器。See also: [QQGuild.URL]、[QQGuild.SANDBOX_URL]。
     * @param token 用于本次请求鉴权的token。
     * @param decoder 用于本次请求结果的反序列化器。不出意外的话应该是 [Json] 序列化器，默认使用 [QQGuildApi.DefaultJsonDecoder]。
     *
     * @throws Exception see [HttpClient.request], 可能会抛出任何ktor请求过程中的异常。
     * @throws love.forte.simbot.qguild.QQGuildApiException 请求过程中出现了错误。
     *
     * @see ErrInfo
     */
    @JvmSynthetic
    public override suspend fun doRequest(
        client: HttpClient,
        server: Url,
        token: String,
        decoder: StringFormat,
    ): R {
        val text = doRequestRaw(client, server, token)

        return decodeResponse(decoder, text)
    }


    /**
     * 使用此api发起一次请求，并得到响应结果的字符串。
     *
     * @see ErrInfo
     *
     * @param client 用于本次http请求的client。默认使用 [QQGuildApi.DefaultHttpClient]。
     * @param server 请求目标服务器。See also: [QQGuild.URL]、[QQGuild.SANDBOX_URL]。
     * @param token 用于本次请求鉴权的token。
     *
     * @throws Exception see [HttpClient.request], 可能会抛出任何ktor请求过程中的异常。
     * @throws love.forte.simbot.qguild.QQGuildApiException 请求过程中出现了错误（http状态码 !in 200 .. 300）
     */
    @JvmSynthetic
    override suspend fun doRequestRaw(client: HttpClient, server: Url, token: String): String {
        val resp = request0(client, server, token)

        val text = resp.bodyAsText()

        if (apiLogger.isDebugEnabled) {
            val traceId = resp.headers[TRACE_ID_HEAD]
            apiLogger.debug(
                "[{} {}] <===== status: {}, body: {}, traceID: {}",
                method.logName,
                resp.request.url.encodedPath,
                resp.status,
                text,
                traceId
            )
        }

        checkStatus(text, DefaultErrInfoDecoder, resp.status)

        if (text.isEmpty() && resp.status.isSuccess()) {
            return "{}"
        }

        return text
    }

    public companion object {

        /**
         * 可以使用的默认 [HttpClient]。
         *
         * 通过 [`HttpClient()`][HttpClient] 构建懒加载并没有任何额外配置。
         *
         * JVM平台下需要添加可用的引擎依赖到classpath中以支持自动加载。
         *
         */
        public val DefaultHttpClient: HttpClient by lazy {
            HttpClient()
        }

        private val DefaultErrInfoDecoder = Json {
            isLenient = true
            ignoreUnknownKeys = true
            allowSpecialFloatingPointValues = true
            allowStructuredMapKeys = true
            prettyPrint = false
            useArrayPolymorphism = false
        }

        /**
         * 部分API中默认使用的Json序列化器。
         *
         * ```kotlin
         * Json {
         *     isLenient = true
         *     ignoreUnknownKeys = true
         *     allowSpecialFloatingPointValues = true
         *     allowStructuredMapKeys = true
         *     prettyPrint = false
         *     useArrayPolymorphism = false
         * }
         * ```
         *
         */
        @InternalApi
        public val DefaultJsonDecoder: Json = Json(DefaultErrInfoDecoder) {}
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


@Suppress("UNCHECKED_CAST")
@OptIn(ExperimentalSerializationApi::class)
private fun <R> QQGuildApi<R>.decodeResponse(
    decoder: StringFormat, remainingText: String
): R {
    if (resultDeserializer === Unit.serializer()) {
        return Unit as R
    }

    if (remainingText.isEmpty() && resultDeserializer.descriptor.kind is StructureKind.OBJECT) {
        return decoder.decodeFromString(resultDeserializer, "{}")
    }

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
