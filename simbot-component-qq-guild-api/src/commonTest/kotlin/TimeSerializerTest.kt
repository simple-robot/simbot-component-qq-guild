package test

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlin.test.Test


class TimeSerializerTest {

    @Test
    fun instantSerializerTest() {
        val json = """
            { "t1":"2021-05-20T15:14:58+08:00", "t2":"2021-05-20T15:14:58Z" }
        """.trimIndent()

        val v = Json.decodeFromString(Times.serializer(), json)
        println(v)
    }

}

@Serializable
private data class Times(val t1: Instant, val t2: Instant)
