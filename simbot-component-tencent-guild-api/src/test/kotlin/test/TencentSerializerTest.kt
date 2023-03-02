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

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.plus
import love.forte.simbot.qguild.QGuildApi
import love.forte.simbot.qguild.TencentMemberInfo
import love.forte.simbot.qguild.api.role.GuildRoleList

/**
 *
 * @author ForteScarlet
 */

class TencentSerializerTest {
    private val j = Json {
        serializersModule += QGuildApi.serializersModule
        classDiscriminator = "_t_"
        ignoreUnknownKeys = true
        isLenient = true
    }

    //@Test
    fun serializer1() {
        val memberJsonStr = """
            {
            "guild_id": 1145141919810,
            "user": {
              "id": 1149159218666,
              "username": "ForteScarlet",
              "avatar": "https://123.jpg",
              "bot": true
            },
            "nick": "ForteScarlet",
            "roles": [1, 55, 67, 255],
            "joined_at": "2021-01-01T11:11:11.111Z"
            }
        """.trimIndent()

        val member: TencentMemberInfo = j.decodeFromString(TencentMemberInfo.serializer, memberJsonStr)
        println(member)
        println(member::class)
    }

    //@Test
    fun serializer2() {
        val rolesJsonStr = """
            {
              "guild_id": 123123123123,
              "roles": [
              {
                "id": 1,
                "name": "管理员",
                "color": 112233,
                "hoist": 0,
                "number": 500,
                "member_limit": 9999
              },
              {
                "id": 4,
                "name": "管理员2",
                "color": 112233,
                "hoist": 0,
                "number": 500,
                "member_limit": 9999  
              }
          ],
          "role_num_limit": 5
            }
        """.trimIndent()

        val list = j.decodeFromString<GuildRoleList>(rolesJsonStr)
        println(list)
        for (role in list.roles) {
            println(role)
        }

    }
}
