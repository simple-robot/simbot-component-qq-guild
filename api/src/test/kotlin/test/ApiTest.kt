/*
 *  Copyright (c) 2022-2022 ForteScarlet <ForteScarlet@163.com>
 *
 *  根据 GNU LESSER GENERAL PUBLIC LICENSE 3 获得许可；
 *  除非遵守许可，否则您不得使用此文件。
 *  您可以在以下网址获取许可证副本：
 *
 *       https://www.gnu.org/licenses/lgpl-3.0-standalone.html
 *
 *   有关许可证下的权限和限制的具体语言，请参见许可证。
 */

package test

import io.ktor.client.*
import io.ktor.http.*
import kotlinx.serialization.json.Json
import love.forte.simbot.tencentguild.TencentGuildInfo
import love.forte.simbot.tencentguild.api.guild.GetBotGuildListApi
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
        val api = GetBotGuildListApi(before = null, after = null, limit = 10)

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