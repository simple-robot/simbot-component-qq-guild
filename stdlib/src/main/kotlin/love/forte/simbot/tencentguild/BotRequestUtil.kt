@file:JvmName("BotRequestUtil")
package love.forte.simbot.tencentguild

import io.ktor.client.call.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import love.forte.simbot.Api4J
import love.forte.simbot.tencentguild.api.TencentApi
import love.forte.simbot.tencentguild.api.decodeFromHttpResponseViaString
import love.forte.simbot.tencentguild.api.request
import love.forte.simbot.tencentguild.api.requestForResponse

@OptIn(InternalSrTcgApi::class)
@Api4J
public fun <R> doRequest(bot: TencentBot, api: TencentApi<R>): R = runBlocking {
    val resp = api.requestForResponse(bot)

    val s = resp.status
    if (!s.isSuccess()) {
        val info = resp.receive<ErrInfo>()
        // throw err
        info.err { s }
    }


    // decode
    api.decodeFromHttpResponseViaString(bot.configuration.decoder, resp)
}


/**
 * 直接通过bot进行请求。
 */
@JvmSynthetic
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
@JvmSynthetic
public suspend fun TencentApi<*>.requestForResponse(bot: TencentBot): HttpResponse {
    return requestForResponse(
        client = bot.configuration.httpClient,
        server = bot.configuration.serverUrl,
        token = bot.ticket.botToken
    )
}