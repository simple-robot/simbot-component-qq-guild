package test

import kotlinx.serialization.json.Json
import love.forte.simbot.tencentguild.IntRangeSerializer
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
            intents = 153,
            shard = 0..4,
            properties = Signal.Identify.Data.Prop(
                os = "windows",
                browser = "chrome",
                device = "abc"
        )))

        println(id)
        println(json.encodeToString(Signal.Identify.serializer(), id))

    }


}