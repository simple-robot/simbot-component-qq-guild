package test

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject
import love.forte.simbot.qguild.event.GroupMemberAdd
import love.forte.simbot.qguild.event.GroupMemberRemove
import love.forte.simbot.qguild.event.InteractionCreate
import love.forte.simbot.qguild.event.resolveDispatchSerializer
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull

class GroupManagementAndInteractionTests {
    private val json = Json {
        ignoreUnknownKeys = true
    }

    @Test
    fun groupMemberAddDispatchResolveTest() {
        val event = decodeDispatch<GroupMemberAdd>(
            """
                {
                  "op": 0,
                  "s": 1,
                  "t": "GROUP_MEMBER_ADD",
                  "id": "event-id",
                  "d": {
                    "group_openid": "group-openid",
                    "member_openid": "member-openid",
                    "timestamp": "2026-07-07T12:00:00+08:00"
                  }
                }
            """.trimIndent()
        )

        assertEquals("event-id", event.id)
        assertEquals(1, event.seq)
        assertEquals("group-openid", event.data.groupOpenid)
        assertEquals("member-openid", event.data.memberOpenid)
        assertEquals("2026-07-07T12:00:00+08:00", event.data.timestamp)
    }

    @Test
    fun groupMemberRemoveDispatchResolveTest() {
        val event = decodeDispatch<GroupMemberRemove>(
            """
                {
                  "op": 0,
                  "s": 2,
                  "t": "GROUP_MEMBER_REMOVE",
                  "id": "event-id",
                  "d": {
                    "group_openid": "group-openid",
                    "member_openid": "member-openid",
                    "timestamp": "2026-07-07T12:00:00+08:00"
                  }
                }
            """.trimIndent()
        )

        assertEquals("event-id", event.id)
        assertEquals(2, event.seq)
        assertEquals("group-openid", event.data.groupOpenid)
        assertEquals("member-openid", event.data.memberOpenid)
    }

    @Test
    fun interactionCreateDispatchResolveTest() {
        val event = decodeDispatch<InteractionCreate>(
            """
                {
                  "op": 0,
                  "s": 3,
                  "t": "INTERACTION_CREATE",
                  "id": "INTERACTION_CREATE:interaction-id",
                  "d": {
                    "application_id": "102076256",
                    "chat_type": 1,
                    "data": {
                      "resolved": {
                        "button_data": "tp:80d95863c841:5",
                        "button_id": "btn_6"
                      },
                      "type": 11
                    },
                    "group_member_openid": "member-openid",
                    "group_openid": "group-openid",
                    "id": "interaction-id",
                    "scene": "group",
                    "timestamp": "2026-07-07T12:00:00+08:00",
                    "type": 11,
                    "version": 1
                  }
                }
            """.trimIndent()
        )

        assertEquals("INTERACTION_CREATE:interaction-id", event.id)
        assertEquals(3, event.seq)
        assertEquals("102076256", event.data.applicationId)
        assertEquals("interaction-id", event.data.id)
        assertEquals(11, event.data.type)
        assertEquals("group", event.data.scene)
        assertEquals(1, event.data.chatType)
        assertEquals("group-openid", event.data.groupOpenid)
        assertEquals("member-openid", event.data.groupMemberOpenid)
        assertEquals("tp:80d95863c841:5", event.data.data.resolved.buttonData)
        assertEquals("btn_6", event.data.data.resolved.buttonId)
        assertEquals(null, event.data.data.resolved.userId)
        assertEquals(null, event.data.data.resolved.messageId)
    }

    private inline fun <reified T> decodeDispatch(raw: String): T {
        val element = json.decodeFromString(JsonElement.serializer(), raw).jsonObject
        val serializer = assertNotNull(resolveDispatchSerializer(element))
        return assertIs<T>(json.decodeFromJsonElement(serializer, element))
    }
}
