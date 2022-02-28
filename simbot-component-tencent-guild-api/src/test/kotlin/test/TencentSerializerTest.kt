/*
 *  Copyright (c) 2022-2022 ForteScarlet <ForteScarlet@163.com>
 *
 *  本文件是 simbot-component-tencent-guild 的一部分。
 *
 *  simbot-component-tencent-guild 是自由软件：你可以再分发之和/或依照由自由软件基金会发布的 GNU 通用公共许可证修改之，无论是版本 3 许可证，还是（按你的决定）任何以后版都可以。
 *
 *  发布 simbot-component-tencent-guild 是希望它能有用，但是并无保障;甚至连可销售和符合某个特定的目的都不保证。请参看 GNU 通用公共许可证，了解详情。
 *
 *  你应该随程序获得一份 GNU 通用公共许可证的复本。如果没有，请看:
 *  https://www.gnu.org/licenses
 *  https://www.gnu.org/licenses/gpl-3.0-standalone.html
 *  https://www.gnu.org/licenses/lgpl-3.0-standalone.html
 *
 *
 */

package test

import kotlinx.serialization.*
import kotlinx.serialization.json.*
import kotlinx.serialization.modules.*
import love.forte.simbot.tencentguild.*
import love.forte.simbot.tencentguild.api.role.*

/**
 *
 * @author ForteScarlet
 */

class TencentSerializerTest {
    private val j = Json {
        serializersModule += TencentGuildApi.serializersModule
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