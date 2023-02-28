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

package test

import kotlinx.serialization.json.Json
import love.forte.simbot.ID
import love.forte.simbot.tencentguild.buildArk
import love.forte.simbot.tencentguild.model.Message
import kotlin.test.Test


/**
 *
 * @author ForteScarlet
 */
class ArkBuilderTest {

    @Test
    fun test() {

        val ark = buildArk(0.ID) {
            // 增加kvs
            kvs {
                kv("Key1", "value") // kv
                kv("Key2", "value2") {
                    obj() // empty obj -> { "objKv": [] }
                    obj {
                        kv("objKey1", "valueA")
                        kv("objKey2", "valueB")
                    }
                }
            }
        }

        val j = Json {
            isLenient = true
            prettyPrint = true
        }

        val jsonStr = j.encodeToString(Message.Ark.serializer(), ark)

        println(jsonStr)
    }

    @Test
    fun decodeTest() {
        val jsonStr = """{
        "template_id": 1,
        "kv": [
            {
                "key": "#DESC#",
                "value": "机器人订阅消息"
            },
            {
                "key": "#PROMPT#",
                "value": "XX机器人"
            },
            {
                "key": "#TITLE#",
                "value": "XX机器人消息"
            },
            {
                "key": "#META_URL#",
                "value": "http://domain.com/"
            },
            {
                "key": "#META_LIST#",
                "obj": [
                    {
                        "obj_kv": [
                            {
                                "key": "name",
                                "value": "aaa"
                            },
                            {
                                "key": "age",
                                "value": "3"
                            }
                        ]
                    },
                    {
                        "obj_kv": [
                            {
                                "key": "name",
                                "value": "bbb"
                            },
                            {
                                "key": "age",
                                "value": "4"
                            }
                        ]
                    }
                ]
            }
        ]
    }"""

        val j = Json {
            isLenient = true
        }

        val ark = j.decodeFromString(Message.Ark.serializer(), jsonStr)
        val ark2 = j.decodeFromString(Message.Ark.serializer(), jsonStr)

        println(ark)
        println(ark2)

        println(ark == ark2)
    }

}
