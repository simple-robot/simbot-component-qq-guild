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

import com.charleskorn.kaml.Yaml
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.plus
import love.forte.simbot.Components
import love.forte.simbot.ID
import love.forte.simbot.component.tencentguild.message.MentionChannel
import love.forte.simbot.component.tencentguild.message.toMessage
import love.forte.simbot.message.At
import love.forte.simbot.message.Messages
import love.forte.simbot.message.Text
import love.forte.simbot.message.plus
import love.forte.simbot.tencentguild.buildArk
import kotlin.test.Test

/**
 *
 * @author ForteScarlet
 */
class MessageSerializerTest {
    init {
        Components
    }
    val module = Messages.serializersModule

    val json get() = Json {
        isLenient = true
        ignoreUnknownKeys = true
        serializersModule += module
    }

    val yaml = Yaml(module)

    val messages = At(1.ID) + Text { "Forte" } + MentionChannel(555.ID) + buildArk(114514.ID) {
        kvs {
            kv("Key", "value-145")
            kv("KK2") {
                obj()
                obj {
                    kv("O-K", "O-V-2")
                    kv("F-F", "O-V-3")
                }
            }
        }
    }.toMessage()

    @Test
    fun test1() {
        println(messages)
        val jsonStr = json.encodeToString(Messages.serializer, messages)
        println(jsonStr)
        val m2 = json.decodeFromString(Messages.serializer, jsonStr)
        println(m2)
        m2.forEach { m -> println(m) }
        println()
        println()
        println()
    }


    @Test
    fun test2() {
        val yamlStr = yaml.encodeToString(Messages.serializer, messages)
        println(yamlStr)
        val m2 = yaml.decodeFromString(Messages.serializer, yamlStr)
        println(m2)
        m2.forEach { m -> println(m) }
        println()
        println()
        println()

    }

}