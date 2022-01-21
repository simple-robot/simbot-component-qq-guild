@file:JvmName("BotRequestUtil")

package love.forte.simbot.tencentguild

import kotlinx.coroutines.runBlocking
import love.forte.simbot.Api4J
import love.forte.simbot.tencentguild.api.TencentApi
import love.forte.simbot.tencentguild.api.request



/**
 * 直接通过bot进行请求。
 *
 * @throws love.forte.simbot.tencentguild.TencentApiException 如果返回状态码不在 200..300之间。
 */
@JvmSynthetic
public suspend fun <R> TencentApi<R>.request(bot: TencentBot): R {
    return request(
        client = bot.configuration.httpClient,
        server = bot.configuration.serverUrl,
        token = bot.ticket.botToken,
        decoder = bot.configuration.decoder,
    )
}


@OptIn(InternalSrTcgApi::class)
@Api4J
public fun <R> doRequest(bot: TencentBot, api: TencentApi<R>): R = runBlocking {
    api.request(bot)
}