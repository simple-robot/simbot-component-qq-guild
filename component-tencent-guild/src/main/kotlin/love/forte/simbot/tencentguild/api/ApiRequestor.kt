package love.forte.simbot.tencentguild.api

import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import love.forte.simbot.tencentguild.TencentGuildApi
import love.forte.simbot.tencentguild.TencentGuildBot


public suspend inline fun <reified R : TencentApiResult> TencentApi<R>.request(bot: TencentGuildBot): R {
    val client = bot.client
    val token = bot.id.ticket.token
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

        }

    }

    // check success
    if (!resp.status.isSuccess()) {

        TODO("Not Success")
    }

    return resp.receive()
}