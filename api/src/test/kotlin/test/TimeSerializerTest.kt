/*
 *  Copyright (c) 2022-2022 ForteScarlet <ForteScarlet@163.com>
 *
 *  根据 GNU LESSER GENERAL PUBLIC LICENSE 3 获得许可；
 *  除非遵守许可，否则您不得使用此文件。
 *  您可以在以下网址获取许可证副本：
 *
 *       https://www.gnu.org/licenses/lgpl-3.0-standalone.html
 *
 *   有关许可证下的权限和限制的具体语言，请参见许可证。
 */

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