package test

import kotlinx.serialization.json.Json
import love.forte.simbot.tencentguild.Intents
import love.forte.simbot.tencentguild.Shared
import love.forte.simbot.tencentguild.Signal
import kotlin.test.Test


/**
 *
 * @author ForteScarlet
 */
class RangeSerializerTest {

    private val json = Json

    @Test
    fun test() {

        val id = Signal.Identify(Signal.Identify.Data(
            token = "token",
            intents = Intents(153) + Intents(224) + Intents(810),
            shard = Shared(0..4),
            properties = Signal.Identify.Data.Prop(
                os = "windows",
                browser = "chrome",
                device = "abc"
        ))
        )

        println(id)
        val idJson = json.encodeToString(Signal.Identify.serializer(), id)
        println(idJson)
        println(json.decodeFromString(Signal.Identify.serializer(), idJson))

    }


}