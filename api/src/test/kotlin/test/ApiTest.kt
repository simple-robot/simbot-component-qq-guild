package test

import io.ktor.client.*
import io.ktor.http.*
import kotlinx.serialization.json.Json
import love.forte.simbot.tencentguild.TencentGuildInfo
import love.forte.simbot.tencentguild.api.guild.BotGuildListApi
import love.forte.simbot.tencentguild.api.request


/**
 *
 * @author ForteScarlet
 */
class ApiTest {
    val client = HttpClient()
    val token = ""

    suspend fun run() {
        // 得到一个api请求对象
        val api = BotGuildListApi(before = null, after = null, limit = 10)

        val guildList: List<TencentGuildInfo> = api.request(
            client = client,
            server = Url("https://sandbox.api.sgroup.qq.com"), // 请求server地址
            token = token,
            decoder = Json // 可以省略，但最好给一个自定义的
        )

        guildList.forEach { guildInfo ->
            println(guildInfo)
        }

    }

}