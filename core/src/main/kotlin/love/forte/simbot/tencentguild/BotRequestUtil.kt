package love.forte.simbot.tencentguild

import io.ktor.client.*
import io.ktor.http.*
import kotlinx.serialization.json.Json
import love.forte.simbot.tencentguild.api.TencentApi
import love.forte.simbot.tencentguild.api.request


public suspend inline fun <reified R> TencentApi<R>.request(
    bot: TencentBot,
    client: HttpClient,
    server: Url = TencentGuildApi.URL,
    decoder: Json = Json
): R {
    return this.request(client = client, server = server, token = bot.ticket.botToken, decoder = decoder)
}