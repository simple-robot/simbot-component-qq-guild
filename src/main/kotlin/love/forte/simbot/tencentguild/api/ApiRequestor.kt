package love.forte.simbot.tencentguild.api

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import love.forte.simbot.tencentguild.ErrInfo
import love.forte.simbot.tencentguild.TencentGuildApi
import love.forte.simbot.tencentguild.err


/**
 *
 */
public suspend inline fun <reified R> TencentApi<R>.request(client: HttpClient, token: String): R {
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


    return resp.receive()
}