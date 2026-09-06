/*
 * Copyright (c) 2026. ForteScarlet.
 *
 * This file is part of simbot-component-qq-guild.
 *
 * simbot-component-qq-guild is free software: you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * simbot-component-qq-guild is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with simbot-component-qq-guild.
 * If not, see <https://www.gnu.org/licenses/>.
 */

package love.forte.simbot.component.qgguild.test

import io.ktor.client.engine.mock.*
import kotlinx.coroutines.test.runTest
import love.forte.simbot.component.qguild.internal.bot.QGBotImpl
import love.forte.simbot.component.qguild.internal.event.QGC2CMessageCreateEventImpl
import love.forte.simbot.qguild.event.C2CMessageCreate
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * [QGC2CMessageCreateEventImpl] 的字段映射测试。
 *
 * @since 4.7.0
 */
class QGC2CMessageCreateEventTests : AbstractInteractionTests() {
    /**
     * 验证新增的 C2C 消息元数据会从 API 事件透传到组件事件。
     *
     * @since 4.7.0
     */
    @Test
    fun forwardsC2CMessageMetadata() = runTest {
        val app = app { respondOk() }
        val bot = app.bot() as QGBotImpl

        try {
            val sourceEvent = C2CMessageCreate(
                id = "event-id",
                s = 1,
                data = C2CMessageCreate.Data(
                    id = "message-id",
                    author = C2CMessageCreate.Author("user-openid"),
                    content = "",
                    timestamp = "2026-09-05T12:00:00+08:00",
                    messageType = 0,
                    messageScene = C2CMessageCreate.MessageScene(
                        source = "custom_menu",
                        ext = listOf("search=1", "msg_idx=2"),
                    ),
                ),
            )
            val event = QGC2CMessageCreateEventImpl(bot, "{}", sourceEvent, sourceEvent.id)

            assertEquals(0, event.messageType)
            assertEquals("custom_menu", event.messageScene?.source)
            assertEquals(listOf("search=1", "msg_idx=2"), event.messageScene?.ext)
        } finally {
            app.cancel()
            bot.cancel()
        }
    }
}
