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
 */
public suspend inline fun <reified R> TencentApi<R>.request(
    client: HttpClient,
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

            protocol = TencentGuildApi.URL.protocol
            host = TencentGuildApi.URL.host
            path(routeBuilder.apiPath)
            contentType(routeBuilder.contentType)

        }

    }

    // check success
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