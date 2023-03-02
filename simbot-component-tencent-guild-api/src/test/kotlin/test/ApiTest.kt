/*
 * Copyright (c) 2022-2023. ForteScarlet.
 *
 * This file is part of simbot-component-tencent-guild.
 *
 * simbot-component-tencent-guild is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * simbot-component-tencent-guild is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with simbot-component-tencent-guild. If not, see <https://www.gnu.org/licenses/>.
 */

package test

import io.ktor.client.*
import io.ktor.http.*
import kotlinx.serialization.json.Json
import love.forte.simbot.qguild.api.request
import love.forte.simbot.qguild.api.user.GetBotGuildListApi


/**
 *
 * @author ForteScarlet
 */
class ApiTest {
    val client = HttpClient()
    val token = ""
    
    suspend fun run() {
        // 得到一个api请求对象
        val api = GetBotGuildListApi.create(before = null, after = null, limit = 10)
        
        val guildList = api.request(
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
