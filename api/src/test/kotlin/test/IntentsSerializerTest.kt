package test

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import love.forte.simbot.tencentguild.EventSignals
import love.forte.simbot.tencentguild.Intents
import kotlin.test.Test


/**
 *
 * @author ForteScarlet
 */
class IntentsSerializerTest {

    @Test
    fun test() {
        val i1 = EventSignals.Guilds.intents
        val i2 = EventSignals.AudioAction.intents

        println(i1 + i2)

        val j = Json.encodeToString(B.serializer(), B(i1 + i2))

        println(j)

    }

}

@Serializable
private data class B(val intents: Intents)