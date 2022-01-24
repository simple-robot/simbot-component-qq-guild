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

import kotlinx.serialization.json.Json
import love.forte.simbot.tencentguild.Intents
import love.forte.simbot.tencentguild.Shard
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
            shard = Shard(0..4),
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