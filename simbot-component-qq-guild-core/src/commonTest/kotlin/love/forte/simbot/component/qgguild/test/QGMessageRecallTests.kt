package love.forte.simbot.component.qgguild.test

import io.ktor.client.engine.mock.*
import io.ktor.http.*
import kotlinx.coroutines.test.runTest
import love.forte.simbot.common.id.StringID.Companion.ID
import love.forte.simbot.component.qguild.internal.bot.QGBotImpl
import love.forte.simbot.component.qguild.internal.event.QGGroupMessageCreateEventImpl
import love.forte.simbot.component.qguild.internal.friend.QGFriendImpl
import love.forte.simbot.component.qguild.internal.group.QGGroupImpl
import love.forte.simbot.qguild.event.GroupMessageCreate
import kotlin.test.Test
import kotlin.test.assertEquals

class QGMessageRecallTests : AbstractInteractionTests() {
    @Test
    fun groupMessageReceiptDeletesTheSentMessage() = runTest {
        val app = app { request ->
            when (request.method) {
                HttpMethod.Post -> respondSentMessage()
                HttpMethod.Delete -> {
                    assertEquals(
                        "/v2/groups/group-openid/messages/sent-message-id",
                        request.url.encodedPath
                    )
                    respondOk()
                }

                else -> error("Unexpected request: ${request.method} ${request.url}")
            }
        }
        val bot = app.bot() as QGBotImpl

        try {
            QGGroupImpl(bot, "group-openid".ID, null, null, true).send("TEXT").delete()
        } finally {
            app.cancel()
            bot.cancel()
        }
    }

    @Test
    fun userMessageReceiptDeletesTheSentMessage() = runTest {
        val app = app { request ->
            when (request.method) {
                HttpMethod.Post -> respondSentMessage()
                HttpMethod.Delete -> {
                    assertEquals(
                        "/v2/users/user-openid/messages/sent-message-id",
                        request.url.encodedPath
                    )
                    respondOk()
                }

                else -> error("Unexpected request: ${request.method} ${request.url}")
            }
        }
        val bot = app.bot() as QGBotImpl

        try {
            QGFriendImpl(bot, "user-openid".ID).send("TEXT").delete()
        } finally {
            app.cancel()
            bot.cancel()
        }
    }

    @Test
    fun groupMessageContentDeletesTheIncomingMessage() = runTest {
        val app = app { request ->
            assertEquals(HttpMethod.Delete, request.method)
            assertEquals(
                "/v2/groups/group-openid/messages/incoming-message-id",
                request.url.encodedPath
            )
            respondOk()
        }
        val bot = app.bot() as QGBotImpl

        try {
            groupMessageEvent(bot).messageContent.delete()
        } finally {
            app.cancel()
            bot.cancel()
        }
    }

    private fun groupMessageEvent(bot: QGBotImpl): QGGroupMessageCreateEventImpl {
        val sourceEvent = GroupMessageCreate(
            id = "event-id",
            s = 1,
            data = GroupMessageCreate.Data(
                id = "incoming-message-id",
                author = GroupMessageCreate.Author("member-openid"),
                content = "source",
                timestamp = "2023-11-06T13:37:18+08:00",
                groupOpenid = "group-openid"
            )
        )

        return QGGroupMessageCreateEventImpl(bot, "{}", sourceEvent, sourceEvent.id)
    }

    private fun MockRequestHandleScope.respondSentMessage() =
        respondOk("""{"id":"sent-message-id","timestamp":"1698435200"}""")
}
