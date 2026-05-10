package test

import love.forte.simbot.qguild.model.MessageKeyboard
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class MessageKeyboardParseTest {

    @Test
    fun parseTemplateKeyboardTest() {
        val keyboard = MessageKeyboard.parse(
            """
            {
              "id": "template-id"
            }
            """.trimIndent()
        )

        assertEquals("template-id", keyboard.id)
        assertNull(keyboard.renderData)
        assertNull(keyboard.action)
    }

    @Test
    fun parseCustomKeyboardWithUnknownKeysTest() {
        val keyboard = MessageKeyboard.parse(
            """
            {
              "render_data": {
                "label": "立即查看",
                "visitedLabel": "已查看",
                "style": 1,
                "ignored": true
              },
              "action": {
                "permission": {
                  "type": 0,
                  "specify_user_ids": ["1001", "1002"],
                  "specify_role_ids": ["2001"],
                  "ignored": "permission-extra"
                },
                "data": "payload",
                "reply": true,
                "enter": false,
                "anchor": 1,
                "unsupport_tips": "当前客户端暂不支持",
                "type": 1,
                "ignored": "action-extra"
              },
              "ignored": "root-extra"
            }
            """.trimIndent()
        )

        assertNull(keyboard.id)
        assertEquals(
            MessageKeyboard.RenderData(
                label = "立即查看",
                visitedLabel = "已查看",
                style = 1
            ),
            keyboard.renderData
        )
        assertEquals(
            MessageKeyboard.Action(
                permission = MessageKeyboard.ActionPermission(
                    type = 0,
                    specifyUserIds = listOf("1001", "1002"),
                    specifyRoleIds = listOf("2001")
                ),
                data = "payload",
                reply = true,
                enter = false,
                anchor = 1,
                unsupportTips = "当前客户端暂不支持",
                type = 1
            ),
            keyboard.action
        )
    }
}
