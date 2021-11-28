package test

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import love.forte.simbot.Timestamp
import love.forte.simbot.tencentguild.TimestampISO8601Serializer
import love.forte.simbot.toTimestamp
import java.time.Instant
import kotlin.test.Test


/**
 *
 * @author ForteScarlet
 */
class TimeSerializerTest {

    @Test
    fun test() {
        val j1 = Json.encodeToString(A.serializer(), A(Instant.now().toTimestamp()))
        val j2 = Json.encodeToString(A.serializer(), A(Timestamp.NotSupport))

        println(j1)
        println(j2)

        println(Json.decodeFromString(A.serializer(), j1))
        println(Json.decodeFromString(A.serializer(), j2))

    }

}

@Serializable
data class A(@Serializable(TimestampISO8601Serializer::class) val timestamp: Timestamp)