package love.forte.simbot.qguild.samples.api

import io.ktor.client.*
import kotlinx.serialization.json.Json
import love.forte.simbot.qguild.QQGuild
import love.forte.simbot.qguild.api.request
import love.forte.simbot.qguild.api.user.GetBotGuildListApi


/**
 * 以 [GetBotGuildListApi] 为例请求API的示例。
 *
 */
suspend fun apiRequestSample(
    client: HttpClient,
) {
    val api = GetBotGuildListApi.create(limit = 50)
    val json = Json {
        isLenient = true
        ignoreUnknownKeys = true
    }
    val guildList = api.request(
        client = client,
        server = QQGuild.SANDBOX_URL, // 沙箱环境
        token = "Bot xxx",
        decoder = json
    )

    println(guildList)
}
