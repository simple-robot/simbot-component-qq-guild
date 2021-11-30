package love.forte.simbot.tencentguild

import io.ktor.client.statement.*
import love.forte.simbot.tencentguild.api.TencentApi
import love.forte.simbot.tencentguild.api.request
import love.forte.simbot.tencentguild.api.requestForResponse


/**
 * 直接通过bot进行请求。
 */
public suspend inline fun <reified R> TencentApi<R>.request(bot: TencentBot): R {
    return request(
        client = bot.configuration.httpClient,
        server = bot.configuration.serverUrl,
        token = bot.ticket.botToken,
        decoder = bot.configuration.decoder,
    )
}

/**
 * 直接通过bot进行请求。
 */
public suspend fun TencentApi<*>.requestForResponse(bot: TencentBot): HttpResponse {
    return requestForResponse(
        client = bot.configuration.httpClient,
        server = bot.configuration.serverUrl,
        token = bot.ticket.botToken
    )
}