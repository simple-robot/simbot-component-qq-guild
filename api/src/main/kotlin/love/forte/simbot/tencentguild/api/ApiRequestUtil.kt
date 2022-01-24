/*
 *  Copyright (c) 2022-2022 ForteScarlet <ForteScarlet@163.com>
 *
 *  根据 GNU LESSER GENERAL PUBLIC LICENSE 3 获得许可；
 *  除非遵守许可，否则您不得使用此文件。
 *  您可以在以下网址获取许可证副本：
 *
 *       https://www.gnu.org/licenses/lgpl-3.0-standalone.html
 *
 *   有关许可证下的权限和限制的具体语言，请参见许可证。
 */

@file:JvmName("ApiRequestUtil")

package love.forte.simbot.tencentguild.api

import io.ktor.client.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.StringFormat
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonBuilder
import love.forte.simbot.Api4J
import love.forte.simbot.LoggerFactory
import love.forte.simbot.tencentguild.InternalSrTcgApi
import java.util.function.Consumer

internal val logger = LoggerFactory.getLogger("love.forte.simbot.tencentguild.api.request")

// @JvmSynthetic
// public suspend fun TencentApi<*>.requestForResponse(
//     client: HttpClient,
//     server: Url,
//     token: String,
// ): HttpResponse {
//     val api = this
//
//     return client.request {
//         method = api.method
//
//         headers {
//             this[HttpHeaders.Authorization] = token
//         }
//
//         url {
//             // route builder
//             val routeBuilder = RouteInfoBuilder { name, value ->
//                 parameters.append(name, value.toString())
//             }
//
//             api.route(routeBuilder)
//             api.body?.also { b -> body = b }
//
//             protocol = server.protocol
//             host = server.host
//             path(routeBuilder.apiPath)
//             routeBuilder.contentType?.let {
//                 headers {
//                     this[HttpHeaders.ContentType] = it.toString()
//                 }
//             }
//             // val contentType = routeBuilder.contentType
//             // if (contentType != null) {
//             //     contentType(contentType)
//             // }
//         }
//
//     }
// }

/**
 *
 * 通过提供的参数，对此api进行请求并得到最终结果。
 *
 * @param client 提供一个 http client
 * @param server 提供一个目标服务器路径
 * @param token 提供一个 token.
 * @param decoder 如果有，提供一个decoder用于通过 [TencentApi.resultDeserializer] 进行反序列化，而不是通过 [client] 进行。
 *
 * @throws love.forte.simbot.tencentguild.TencentApiException 如果响应码不在 200..300 范围内。
 */
@JvmSynthetic
public suspend fun <R> TencentApi<R>.request(
    client: HttpClient,
    server: Url,
    token: String,
    decoder: StringFormat = defaultJson
): R {
    return this.doRequest(client, server, token, decoder)
}


/**
 * for Java
 */
@OptIn(InternalSrTcgApi::class)
@Api4J
@JvmOverloads
public fun <R> doRequest(
    api: TencentApi<R>,
    client: HttpClient,
    server: String,
    token: String,
    decoder: StringFormat = defaultJson
): R = runBlocking {
    api.request(client, Url(server), token, decoder)
}

@OptIn(Api4J::class)
private val defaultJson = newJson()

@Api4J
public fun newHttpClient(): HttpClient = HttpClient()

@Api4J
@JvmOverloads
public fun newJson(
    build: Consumer<JsonBuilder> = Consumer {
        it.apply {
            isLenient = true
            ignoreUnknownKeys = true
        }
    }
): Json = Json {
    build.accept(this)
}