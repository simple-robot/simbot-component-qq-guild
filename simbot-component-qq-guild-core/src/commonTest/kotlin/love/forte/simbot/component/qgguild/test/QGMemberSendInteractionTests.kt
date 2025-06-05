package love.forte.simbot.component.qgguild.test

import io.ktor.client.engine.mock.*
import io.ktor.client.request.*
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import love.forte.simbot.common.atomic.atomic
import love.forte.simbot.common.id.LongID.Companion.ID
import love.forte.simbot.common.id.UUID
import love.forte.simbot.component.qguild.event.QGSendSupportInteractionEvent
import love.forte.simbot.component.qguild.guild.QGMember
import love.forte.simbot.component.qguild.internal.bot.QGBotImpl
import love.forte.simbot.component.qguild.internal.guild.QGMemberImpl
import love.forte.simbot.event.InteractionMessage
import love.forte.simbot.message.Text
import love.forte.simbot.qguild.model.SimpleMember
import love.forte.simbot.qguild.model.User
import kotlin.test.*

/**
 *
 * @author ForteScarlet
 */
class QGMemberSendInteractionTests : AbstractInteractionTests() {

    private fun member(bot: QGBotImpl): QGMemberImpl {
        return QGMemberImpl(
            bot,
            SimpleMember(
                user = User(
                    id = "654321",
                    username = "username"
                ),
                nick = "nick",
                roles = emptyList(),
                joinedAt = Clock.System.now().toString(),
            ),
            123456L.ID
        )
    }

    private fun MockRequestHandleScope.respondSendOk(req: HttpRequestData): HttpResponseData {
        if (req.url.encodedPath.endsWith("users/@me/dms")) {
            return respondOk("""
                {"guild_id":"555555",
                "channel_id":"666666",
                create_time: "${Clock.System.now()}"}
            """.trimIndent())
        }

        return respondOk(
            """
                    {
                    "id":"${UUID.random()}",
                    "channel_id":"555555",
                    "guild_id":"666666",
                    "content":"TEXT",
                    "timestamp":"${Clock.System.now()}",
                    "author": {
                        "id":"654321",
                        "username":"username"
                        },
                        "seq_in_channel":1
                    }
                """.trimIndent()
        )
    }

    @Test
    fun testQGMemberSendInteraction() = runTest {
        // TEXT
        testInteractionEvent<_, QGSendSupportInteractionEvent>(
            mockClient = { respondSendOk(it) },
            prepare = {
                atomic(false) to member(it)
            },
            listener = { (atob, friend), e, _, _ ->
                atob.value = true
                assertIs<QGMember>(e.content)
                assertSame(friend, e.content)
                val message = e.message
                assertIs<InteractionMessage.Text>(message)
                assertEquals("TEXT", message.text)
            }
        ) { (atob, friend), _ ->
            friend.send("TEXT")
            assertTrue(atob.value)
        }

        // Message
        testInteractionEvent<_, QGSendSupportInteractionEvent>(
            mockClient = { respondSendOk(it) },
            prepare = {
                atomic(false) to member(it)
            },
            listener = { (atob, friend), e, _, _ ->
                atob.value = true
                assertIs<QGMember>(e.content)
                assertSame(friend, e.content)
                val message = e.message
                assertIs<InteractionMessage.Message>(message)
                val messageMessage = message.message
                assertIs<Text>(messageMessage)
                assertEquals("TEXT", messageMessage.text)
            }
        ) { (atob, friend), _ ->
            friend.send(Text { "TEXT" })
            assertTrue(atob.value)
        }
    }

}