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