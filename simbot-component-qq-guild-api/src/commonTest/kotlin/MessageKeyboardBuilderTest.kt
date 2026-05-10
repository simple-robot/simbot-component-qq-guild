package test

import love.forte.simbot.qguild.model.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class MessageKeyboardBuilderTest {

    @Test
    fun dslBuildTest() {
        val keyboard = buildMessageKeyboard {
            renderData {
                label = "立即查看"
                visitedLabel = "已查看"
                style = 1
            }
            action {
                type = 1
                data = "payload"
                unsupportTips = "当前客户端暂不支持"
                reply = true
                enter = false
                anchor = 1
                permission {
                    type = 0
                    addSpecifyUserId("1001")
                    addSpecifyUserIds("1002", "1003")
                }
            }
        }

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
                    specifyUserIds = listOf("1001", "1002", "1003")
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

    @Test
    fun chainBuildAndFromTest() {
        val source = MessageKeyboardBuilder("template-id")
            .renderData("按钮", style = 1)
            .action(
                data = "command",
                unsupportTips = "unsupport",
                permission = MessageKeyboard.ActionPermission(type = 2)
            )
            .build()

        val copied = MessageKeyboardBuilder()
            .from(source)
            .renderData(
                MessageKeyboardRenderDataBuilder()
                    .from(source.renderData!!)
                    .visitedLabel("已点击")
                    .build()
            )
            .action(
                MessageKeyboardActionBuilder()
                    .from(source.action!!)
                    .type(2)
                    .build()
            )
            .build()

        assertEquals("template-id", copied.id)
        assertEquals("按钮", copied.renderData?.label)
        assertEquals("已点击", copied.renderData?.visitedLabel)
        assertEquals(1, copied.renderData?.style)
        assertEquals("command", copied.action?.data)
        assertEquals("unsupport", copied.action?.unsupportTips)
        assertEquals(2, copied.action?.type)
        assertEquals(MessageKeyboard.ActionPermission(type = 2), copied.action?.permission)
    }
}
