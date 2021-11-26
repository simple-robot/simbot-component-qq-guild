package test

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.test.Test

/**
 *
 * @author ForteScarlet
 */
class BotConfTest {


    // @Test
    // fun confTest() {
    //     val conf1 = tencentGuildBotConfiguration {
    //         ticket {
    //             appId = "APP_ID"
    //             appKey = "APP_KEY"
    //             token = "$appId.ii45i4ii9i98i0"
    //         }
    //     }
    //
    //     val id = TencentGuildBotID(conf1.ticket!!)
    //     println(id)
    //     val conf2 = tencentGuildBotConfiguration {
    //         ticket {
    //             appId = "APP_ID"
    //             appKey = "APP_KEY"
    //             token = "APP_ID.aaabbbcccddd"
    //         }
    //     }
    //
    //     val id2 = TencentGuildBotID(conf2.ticket!!)
    //     println(id2)
    //
    //     println(id == id2)
    //
    //     val j1 = Json.encodeToString(id)
    //     println(j1)
    //     val j2 = Json.encodeToString(id2)
    //     println(j2)
    //
    //     println(Json.decodeFromString<TencentGuildBotID>(j1))
    //     println(Json.decodeFromString<TencentGuildBotID>(j2))
    //
    // }


}