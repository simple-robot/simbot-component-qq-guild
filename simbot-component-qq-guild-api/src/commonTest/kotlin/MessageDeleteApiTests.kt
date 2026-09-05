@file:Suppress("DEPRECATION")

package test

import io.ktor.http.*
import love.forte.simbot.qguild.api.message.DeleteChannelMessageApi
import love.forte.simbot.qguild.api.message.DeleteMessageApi
import love.forte.simbot.qguild.api.message.group.GroupMessageDeleteApi
import love.forte.simbot.qguild.api.message.user.UserMessageDeleteApi
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class MessageDeleteApiTests {
    @Test
    fun channelMessageDeleteApiUsesChannelResource() {
        val api = DeleteChannelMessageApi.create("channel-openid", "message-id", hidetip = true)

        assertEquals(HttpMethod.Delete, api.method)
        assertEquals("/channels/channel-openid/messages/message-id", api.url.encodedPath)
        assertEquals("true", api.url.parameters["hidetip"])
        assertNull(api.body)
    }

    @Test
    fun legacyChannelMessageDeleteApiKeepsItsResource() {
        val api = DeleteMessageApi.create("channel-openid", "message-id")

        assertEquals(HttpMethod.Delete, api.method)
        assertEquals("/channels/channel-openid/messages/message-id", api.url.encodedPath)
        assertNull(api.body)
    }

    @Test
    fun groupMessageDeleteApiUsesV2GroupResource() {
        val api = GroupMessageDeleteApi.create("group-openid", "message-id")

        assertEquals(HttpMethod.Delete, api.method)
        assertEquals("/v2/groups/group-openid/messages/message-id", api.url.encodedPath)
        assertNull(api.body)
    }

    @Test
    fun userMessageDeleteApiUsesV2UserResource() {
        val api = UserMessageDeleteApi.create("user-openid", "message-id")

        assertEquals(HttpMethod.Delete, api.method)
        assertEquals("/v2/users/user-openid/messages/message-id", api.url.encodedPath)
        assertNull(api.body)
    }
}
