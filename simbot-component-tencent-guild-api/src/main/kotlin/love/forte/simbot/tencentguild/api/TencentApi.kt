/*
 *  Copyright (c) 2022-2022 ForteScarlet <ForteScarlet@163.com>
 *
 *  本文件是 simbot-component-tencent-guild 的一部分。
 *
 *  simbot-component-tencent-guild 是自由软件：你可以再分发之和/或依照由自由软件基金会发布的 GNU 通用公共许可证修改之，无论是版本 3 许可证，还是（按你的决定）任何以后版都可以。
 *
 *  发布 simbot-component-tencent-guild 是希望它能有用，但是并无保障;甚至连可销售和符合某个特定的目的都不保证。请参看 GNU 通用公共许可证，了解详情。
 *
 *  你应该随程序获得一份 GNU 通用公共许可证的复本。如果没有，请看:
 *  https://www.gnu.org/licenses
 *  https://www.gnu.org/licenses/gpl-3.0-standalone.html
 *  https://www.gnu.org/licenses/lgpl-3.0-standalone.html
 *
 *
 */

package love.forte.simbot.tencentguild.api

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
import love.forte.simbot.tencentguild.ErrInfo
import love.forte.simbot.tencentguild.err


/**
 * 表示为一个腾讯频道的API。
 *
 * 通过 [doRequest] 发起一次请求。
 *
 * @see TencentCacheableApi
 */
public abstract class TencentApi<out R> {
    
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
     * @throws love.forte.simbot.tencentguild.TencentApiException 请求过程中出现了错误。
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


private suspend fun TencentApi<*>.requestForResponse(client: HttpClient, server: Url, token: String): HttpResponse {
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
            appendPathSegments(routeBuilder.apiPath)
            routeBuilder.contentType?.let {
                headers {
                    this[HttpHeaders.ContentType] = it.toString()
                }
            }
        }
    }
}


private suspend fun <R> TencentApi<R>.decodeFromHttpResponseViaString(
    decoder: StringFormat, response: HttpResponse,
): R {
    val remainingText = response.bodyAsText()
    
    logger.trace("resp: {}", remainingText)
    
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

public abstract class TencentApiWithoutResult : TencentApi<Unit>() {
    override val resultDeserializer: DeserializationStrategy<Unit>
        get() = Unit.serializer()
}

public abstract class GetTencentApi<R> : TencentApi<R>() {
    override val method: HttpMethod
        get() = HttpMethod.Get
    
    override val body: Any?
        get() = null
}
