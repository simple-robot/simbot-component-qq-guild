@file:JvmName("ApiRequestUtil")

package love.forte.simbot.tencentguild.api

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.utils.io.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.StringFormat
import kotlinx.serialization.descriptors.StructureKind
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonBuilder
import love.forte.simbot.Api4J
import love.forte.simbot.tencentguild.ErrInfo
import love.forte.simbot.tencentguild.err
import java.util.function.Consumer


public suspend fun TencentApi<*>.requestForResponse(
    client: HttpClient,
    server: Url,
    token: String,
): HttpResponse {
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
            api.body?.also { b -> body = b }

            protocol = server.protocol
            host = server.host
            path(routeBuilder.apiPath)
            routeBuilder.contentType?.let {
                headers {
                    this[HttpHeaders.ContentType] = it.toString()
                }
            }
            // val contentType = routeBuilder.contentType
            // if (contentType != null) {
            //     contentType(contentType)
            // }
        }

    }
}

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
public suspend inline fun <reified R> TencentApi<R>.request(
    client: HttpClient,
    server: Url,
    token: String,
    decoder: StringFormat? = null
): R {
    val api = this
    val resp = requestForResponse(client, server, token)

    // check status
    checkStatus(resp) { resp.status }

    if (decoder == null) {
        return resp.receive()
    }

    return api.decodeFromHttpResponse(decoder, resp)
}

@PublishedApi
internal suspend inline fun checkStatus(resp: HttpResponse, status: () -> HttpStatusCode) {
    val s = status()
    if (!s.isSuccess()) {
        val info = resp.receive<ErrInfo>()
        // throw err
        info.err { s }
    }
}


@PublishedApi
@OptIn(ExperimentalSerializationApi::class)
internal suspend inline fun <reified R> TencentApi<R>.decodeFromHttpResponse(
    decoder: StringFormat,
    response: HttpResponse
): R {
    val resultKind = resultDeserializer.descriptor.kind
    if (resultKind == StructureKind.OBJECT) {
        val objInstance = R::class.objectInstance
        if (objInstance != null) return objInstance
    }

    return decodeFromHttpResponseViaString(decoder, response)
}


@PublishedApi
internal suspend fun <R> TencentApi<R>.decodeFromHttpResponseViaString(
    decoder: StringFormat,
    response: HttpResponse
): R {
    val remainingText = response.content.readRemaining().readText()

    println("resp: $remainingText")

    return decoder.decodeFromString(resultDeserializer, remainingText)
}


/**
 * for Java
 */
@Api4J
@JvmOverloads
public fun <R> doRequest(
    api: TencentApi<R>,
    client: HttpClient,
    server: String,
    token: String,
    decoder: StringFormat = Json
): R = runBlocking {
    val resp = api.requestForResponse(client, Url(server), token)

    checkStatus(resp) { resp.status }

    // decode
    api.decodeFromHttpResponseViaString(decoder, resp)
}


@Api4J
public fun newHttpClient(): HttpClient = HttpClient()

@Api4J
@JvmOverloads
public fun newJson(build: Consumer<JsonBuilder> = Consumer {
    it.apply {
        isLenient = true
        ignoreUnknownKeys = true
    }
}): Json = Json {
    build.accept(this)
}