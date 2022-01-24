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
import love.forte.simbot.ID
import love.forte.simbot.tencentguild.TencentMessage
import love.forte.simbot.tencentguild.buildArk
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

        val jsonStr = j.encodeToString(TencentMessage.Ark.serializer(), ark)

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

        val ark = j.decodeFromString(TencentMessage.Ark.serializer(), jsonStr)
        val ark2 = j.decodeFromString(TencentMessage.Ark.serializer(), jsonStr)

        println(ark)
        println(ark2)

        println(ark == ark2)
    }

}