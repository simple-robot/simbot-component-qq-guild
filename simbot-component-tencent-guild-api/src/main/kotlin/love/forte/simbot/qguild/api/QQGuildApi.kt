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
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.client.utils.*
import io.ktor.http.*
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.StringFormat
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import love.forte.simbot.qguild.ErrInfo
import love.forte.simbot.qguild.err
import org.slf4j.LoggerFactory

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
     * @throws Exception see [HttpClient.request], 可能会抛出任何ktor请求过程中的异常。
     * @throws love.forte.simbot.qguild.QQGuildApiException 请求过程中出现了错误。
     * @see ErrInfo
     */
    @JvmSynthetic
    public open suspend fun doRequest(
        client: HttpClient,
        server: Url,
        token: String,
        decoder: StringFormat = Json,
    ): R {
        val resp = requestForResponse(client, server, token)

        checkStatus(resp) { resp.status }
        
        // decode
        return decodeFromHttpResponseViaString(decoder, resp)
    }
}


private suspend fun QQGuildApi<*>.requestForResponse(client: HttpClient, server: Url, token: String): HttpResponse {
    val api = this


    return client.request {
        method = api.method
        
        headers {
            this[HttpHeaders.Authorization] = token
        }
        
        url {
            // route builder
            val routeBuilder = RouteInfoBuilder { name, value ->
                parameters.append(name, value.toString())
            }
            
            api.route(routeBuilder)
            setBody(api.body ?: EmptyContent)
            
            protocol = server.protocol
            host = server.host
            routeBuilder.apiPath?.let { apiPath -> appendPathSegments(components = apiPath) }
            routeBuilder.contentType?.let {
                headers {
                    this[HttpHeaders.ContentType] = it.toString()
                }
            }
        }

        apiLogger.debug("[{} /{}] =====> server {}, body: {}", method.value, url.encodedPath, url.host, api.body)
    }.also { resp ->
        val traceId = resp.headers[TRACE_ID_HEAD]
        apiLogger.debug("[{} {}] <===== status: {}, traceID: {}", method.value, resp.request.url.encodedPath, resp.status, traceId)
    }
}


private suspend fun <R> QQGuildApi<R>.decodeFromHttpResponseViaString(
    decoder: StringFormat, response: HttpResponse,
): R {
    val remainingText = response.bodyAsText()

    apiLogger.debug("[{} {}] <===== body: {}", method.value, response.request.url.encodedPath, remainingText)

    return decoder.decodeFromString(resultDeserializer, remainingText)
}

private suspend inline fun checkStatus(resp: HttpResponse, status: () -> HttpStatusCode) {
    val s = status()
    if (!s.isSuccess()) {
        val info = resp.body<ErrInfo>()
        // throw err
        info.err { s }
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
