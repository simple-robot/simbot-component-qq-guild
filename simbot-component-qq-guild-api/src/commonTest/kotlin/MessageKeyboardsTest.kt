package test

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import love.forte.simbot.qguild.QQGuild
import love.forte.simbot.qguild.api.message.GroupAndC2CSendBody
import love.forte.simbot.qguild.api.message.group.GroupMessageSendApi
import love.forte.simbot.qguild.api.message.user.UserMessageSendApi
import love.forte.simbot.qguild.model.MessageKeyboard
import love.forte.simbot.qguild.model.MessageKeyboards
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class MessageKeyboardsTest {

    @Test
    fun dslBuildTest() {
        val keyboards = MessageKeyboards {
            content {
                row {
                    button {
                        renderData("确认", visitedLabel = "已确认", style = 1)
                        action {
                            type = 1
                            data = "confirm"
                            unsupportTips = "当前客户端暂不支持"
                            permissionAllAccessible()
                        }
                    }
                }
                row {
                    addButton(MessageKeyboard.create("template-id"))
                }
            }
        }

        assertEquals(2, keyboards.content.rows.size)
        assertEquals("确认", keyboards.content.rows[0].buttons.single().renderData?.label)
        assertEquals("confirm", keyboards.content.rows[0].buttons.single().action?.data)
        assertEquals(MessageKeyboard.ActionPermission.AllAccessible, keyboards.content.rows[0].buttons.single().action?.permission)
        assertEquals("template-id", keyboards.content.rows[1].buttons.single().id)
    }

    @Test
    fun parseMultiRowKeyboardTest() {
        val keyboards = MessageKeyboards.parse(
            """
            {
              "content": {
                "rows": [
                  {
                    "buttons": [
                      {
                        "render_data": {
                          "label": "确认",
                          "visitedLabel": "已确认",
                          "style": 1
                        },
                        "action": {
                          "permission": {
                            "type": 2
                          },
                          "data": "confirm",
                          "reply": true,
                          "enter": false,
                          "anchor": 1,
                          "unsupport_tips": "当前客户端暂不支持",
                          "type": 1
                        }
                      }
                    ]
                  },
                  {
                    "buttons": [
                      {
                        "id": "template-id"
                      }
                    ]
                  }
                ]
              }
            }
            """.trimIndent()
        )

        assertEquals(2, keyboards.content.rows.size)
        assertEquals("确认", keyboards.content.rows[0].buttons.single().renderData?.label)
        assertEquals(MessageKeyboard.ActionPermission(type = 2), keyboards.content.rows[0].buttons.single().action?.permission)
        assertEquals("template-id", keyboards.content.rows[1].buttons.single().id)
    }

    @Test
    fun groupAndC2CBodySerializesKeyboardsAsKeyboardFieldTest() {
        val body = GroupAndC2CSendBody.create("markdown", GroupAndC2CSendBody.MSG_TYPE_MARKDOWN) {
            keyboards = MessageKeyboards.create(MessageKeyboard.create("template-id"))
        }

        val json = QQGuild.DefaultJson.encodeToString(body)
        val tree = QQGuild.DefaultJson.parseToJsonElement(json).jsonObject

        assertEquals(JsonPrimitive("markdown"), tree["content"])
        assertEquals(JsonPrimitive(GroupAndC2CSendBody.MSG_TYPE_MARKDOWN), tree["msg_type"])
        val keyboard = assertNotNull(tree["keyboard"] as? JsonObject)
        assertEquals(
            JsonPrimitive("template-id"),
            keyboard["content"]?.jsonObject
                ?.get("rows")?.jsonArray
                ?.single()?.jsonObject
                ?.get("buttons")?.jsonArray
                ?.single()?.jsonObject
                ?.get("id")
        )
    }

    @Test
    fun createMarkdownWithKeyboardsTest() {
        val keyboards = MessageKeyboards.create(MessageKeyboard.create("template-id"))

        val groupBody = GroupMessageSendApi
            .createMarkdown("group-id", "markdown", keyboards)
            .body as GroupAndC2CSendBody
        val userBody = UserMessageSendApi
            .createMarkdown("openid", "markdown", keyboards)
            .body as GroupAndC2CSendBody

        assertEquals("template-id", groupBody.keyboards?.content?.rows?.single()?.buttons?.single()?.id)
        assertEquals("template-id", userBody.keyboards?.content?.rows?.single()?.buttons?.single()?.id)
    }
}
