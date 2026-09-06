package test

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject
import love.forte.simbot.qguild.event.C2CMessageCreate
import love.forte.simbot.qguild.event.resolveDispatchSerializer
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull

class C2CMessageCreateTests {
    private val json = Json {
        ignoreUnknownKeys = true
    }

    @Test
    fun c2cMessageCreateDeserializesMenuSwitchScene() {
        val raw = """
            {
              "op": 0,
              "s": 1,
              "t": "C2C_MESSAGE_CREATE",
              "id": "event-id",
              "d": {
                "id": "message-id",
                "author": {
                  "user_openid": "user-openid"
                },
                "content": "",
                "timestamp": "2026-09-05T12:00:00+08:00",
                "message_type": 0,
                "message_scene": {
                  "source": "custom_menu",
                  "ext": ["search=1", "msg_idx=2"]
                }
              }
            }
        """.trimIndent()

        val element = json.decodeFromString(JsonElement.serializer(), raw).jsonObject
        val serializer = assertNotNull(resolveDispatchSerializer(element))
        val event = assertIs<C2CMessageCreate>(json.decodeFromJsonElement(serializer, element))

        assertEquals("event-id", event.id)
        assertEquals("message-id", event.data.id)
        assertEquals(0, event.data.messageType)
        assertEquals("custom_menu", event.data.messageScene?.source)
        assertEquals(listOf("search=1", "msg_idx=2"), event.data.messageScene?.ext)
    }
}
