/*
 * Copyright (c) 2022-2023. ForteScarlet.
 *
 * This file is part of simbot-component-tencent-guild.
 *
 * simbot-component-tencent-guild is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * simbot-component-tencent-guild is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with simbot-component-tencent-guild. If not, see <https://www.gnu.org/licenses/>.
 */

import com.charleskorn.kaml.Yaml
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.plus
import love.forte.simbot.ID
import love.forte.simbot.component.qguild.QQGuildComponent
import love.forte.simbot.component.qguild.message.TcgMentionChannel
import love.forte.simbot.component.qguild.message.toMessage
import love.forte.simbot.message.At
import love.forte.simbot.message.Messages
import love.forte.simbot.message.Text
import love.forte.simbot.message.plus
import love.forte.simbot.qguild.buildArk
import kotlin.test.Test

/**
 *
 * @author ForteScarlet
 */
class MessageSerializerTest {

    private val module = Messages.serializersModule + QQGuildComponent.messageSerializersModule

    private val json
        get() = Json {
            isLenient = true
            ignoreUnknownKeys = true
            serializersModule += module
        }

    private val yaml = Yaml(module)

    private val messages = At(1.ID) + Text { "Forte" } + TcgMentionChannel(555.ID) + buildArk(114514.ID) {
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
