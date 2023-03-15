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

package love.forte.simbot.qguild.api

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.client.utils.*
import io.ktor.http.*
import kotlinx.coroutines.*
import kotlinx.coroutines.future.asCompletableFuture
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.StringFormat
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import love.forte.simbot.Api4J
import love.forte.simbot.qguild.ErrInfo
import love.forte.simbot.qguild.QQGuildApiException
import org.slf4j.LoggerFactory
import java.util.concurrent.CompletableFuture
import kotlin.concurrent.thread

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
public abstract class QQGuildApi<out R> {

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
    public open suspend fun doRequest(
        client: HttpClient,
        server: Url,
        token: String,
        decoder: StringFormat = Json,
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

        checkStatus(text, decoder) { resp.status }

        // decode
        return decodeResponse(decoder, text)
    }

    /**
     * 使用此api发起一次请求，并得到预期中的结果。如果返回了代表错误的响应值。
     *
     * 为Java服务的阻塞API，内部使用 [runBlocking] 阻塞并等待 [doRequest] 的结果。
     * 其中，阻塞代码块上下文的调度器为 [Dispatchers.IO]。
     *
     * @see ErrInfo
     *
     * @throws Exception see [HttpClient.request], 可能会抛出任何ktor请求过程中的异常。
     * @throws love.forte.simbot.qguild.QQGuildApiException 请求过程中出现了错误。
     */
    @Api4J
    public fun doRequestBlocking(
        client: HttpClient,
        server: Url,
        token: String,
        decoder: StringFormat = Json,
    ): R = runBlocking(Dispatchers.IO) {
        doRequest(client, server, token, decoder)
    }


    /**
     * 使用此api发起一次请求，并得到预期中的结果。如果返回了代表错误的响应值。
     *
     * 为Java服务的异步API，内部使用 [CoroutineScope.async] 异步执行 [doRequest] 并返回
     * [CompletableFuture] 结果。
     * 其中，[CoroutineScope] 使用的为内部懒加载的默认作用域，此作用域使用的调度器为 [Dispatchers.IO]。
     *
     * @see ErrInfo
     *
     * @throws Exception see [HttpClient.request], 可能会抛出任何ktor请求过程中的异常。
     * @throws love.forte.simbot.qguild.QQGuildApiException 请求过程中出现了错误。
     */
    @Api4J
    public fun doRequestAsync(
        client: HttpClient,
        server: Url,
        token: String,
        decoder: StringFormat = Json,
    ): CompletableFuture<out R> {
        return DEFAULT_ASYNC_SCOPE.async { doRequest(client, server, token, decoder) }.asCompletableFuture()
    }


    public companion object {
        private val DEFAULT_ASYNC_SCOPE by lazy {
            val job = SupervisorJob()
            CoroutineScope(Dispatchers.IO + CoroutineName("Api-Async-Scope") + job).also { scope ->
                Runtime.getRuntime().addShutdownHook(thread(start = false) {
                    scope.cancel()
                })
            }
        }
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

private inline fun checkStatus(
    remainingText: String,
    decoder: StringFormat,
    status: () -> HttpStatusCode
) {
    val s = status()
    if (!s.isSuccess()) {
        val info = decoder.decodeFromString(ErrInfo.serializer(), remainingText)
        // throw err
        throw QQGuildApiException(info, s.value, s.description)
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

// 急切初始化
private val logNameProcessor: HttpMethod.() -> String = run {
    // default true
    val enable = System.getProperty("simbot.qguild.api.logger.color.enable")?.toBoolean() ?: true

    // 以 DELETE 为最长标准
    // API基本不存在 HEAD OPTIONS

    if (enable) {
        {
            when (this) {
                /*
                    GET        绿色
                    POST       蓝色
                    PUT, PATCH 紫色
                    DELETE     红色
                 */
                HttpMethod.Get ->     "\u001B[32m   GET\u001B[0m"
                HttpMethod.Post ->    "\u001B[34m  POST\u001B[0m"
                HttpMethod.Put ->     "\u001B[35m   PUT\u001B[0m"
                HttpMethod.Patch ->   "\u001B[35m PATCH\u001B[0m"
                HttpMethod.Delete ->  "\u001B[31mDELETE\u001B[0m"
                else -> value
            }
        }
    } else {
        {
            when (this) {
                HttpMethod.Get ->     "   GET"
                HttpMethod.Post ->    "  POST"
                HttpMethod.Put ->     "   PUT"
                HttpMethod.Patch ->   " PATCH"
                HttpMethod.Delete ->  "DELETE"
                else -> value
            }
        }
    }
}

/**
 * 日志对齐
 */
private val HttpMethod.logName: String
    get() = logNameProcessor()
