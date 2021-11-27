package love.forte.simbot.tencentguild.api

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.utils.io.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.StringFormat
import kotlinx.serialization.descriptors.StructureKind
import love.forte.simbot.tencentguild.ErrInfo
import love.forte.simbot.tencentguild.TencentGuildApi
import love.forte.simbot.tencentguild.err




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
    
    val resp = client.request<HttpResponse> {
        method = api.method

        headers {
            this[HttpHeaders.Authorization] = "Bot $token"
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
            contentType(routeBuilder.contentType)

        }

    }

    // check status
    val status = resp.status
    if (!status.isSuccess()) {
        val info = resp.receive<ErrInfo>()
        // throw err
        info.err { status }
    }

    if (decoder == null) {
        return resp.receive()
    }

    return api.decodeFromByteReadChannel(decoder, resp)
}

@OptIn(ExperimentalSerializationApi::class)
public suspend inline fun <reified R> TencentApi<R>.decodeFromByteReadChannel(
    decoder: StringFormat,
    response: HttpResponse
): R {
    val resultKind = resultDeserializer.descriptor.kind
    if (resultKind == StructureKind.OBJECT) {
        val objInstance = R::class.objectInstance
        if (objInstance != null) return objInstance
    }

    val remainingText = response.content.readRemaining().readText()
    return decoder.decodeFromString(resultDeserializer, remainingText)
}