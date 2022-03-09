/*
 *  Copyright (c) 2022-2022 ForteScarlet <ForteScarlet@163.com>
 *
 *  本文件是 simbot-component-tencent-guild 的一部分。
 *
 *  simbot-component-tencent-guild 是自由软件：你可以再分发之和/或依照由自由软件基金会发布的 GNU 通用公共许可证修改之，无论是版本 3 许可证，还是（按你的决定）任何以后版都可以。
 *
 *  发布 simbot-component-tencent-guild 是希望它能有用，但是并无保障;甚至连可销售和符合某个特定的目的都不保证。请参看 GNU 通用公共许可证，了解详情。
 *
 *  你应该随程序获得一份 GNU 通用公共许可证的复本。如果没有，请看:
 *  https://www.gnu.org/licenses
 *  https://www.gnu.org/licenses/gpl-3.0-standalone.html
 *  https://www.gnu.org/licenses/lgpl-3.0-standalone.html
 *
 *
 */

import com.charleskorn.kaml.*
import kotlinx.serialization.json.*
import kotlinx.serialization.modules.*
import love.forte.simbot.*
import love.forte.simbot.component.tencentguild.*
import love.forte.simbot.component.tencentguild.message.*
import love.forte.simbot.message.*
import love.forte.simbot.tencentguild.*
import kotlin.test.*

/**
 *
 * @author ForteScarlet
 */
class MessageSerializerTest {

    private val module = Messages.serializersModule + TencentGuildComponent.messageSerializersModule

    private val json
        get() = Json {
            isLenient = true
            ignoreUnknownKeys = true
            serializersModule += module
        }

    private val yaml = Yaml(module)

    private val messages = At(1.ID) + Text { "Forte" } + MentionChannel(555.ID) + buildArk(114514.ID) {
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