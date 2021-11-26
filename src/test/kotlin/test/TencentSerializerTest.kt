package test

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.plus
import love.forte.simbot.tencentguild.TencentGuildApi
import love.forte.simbot.tencentguild.TencentMemberInfo
import kotlin.test.Test

/**
 *
 * @author ForteScarlet
 */

class TencentSerializerTest {

    @Test
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
            "joined_at": 119988752231134
            }
        """.trimIndent()

        val j = Json {
            serializersModule += TencentGuildApi.serializersModule
            classDiscriminator = "_t_"
            ignoreUnknownKeys = true
        }

        val member = j.decodeFromString<TencentMemberInfo>(memberJsonStr)
        println(member)
        println(member::class)



    }
}