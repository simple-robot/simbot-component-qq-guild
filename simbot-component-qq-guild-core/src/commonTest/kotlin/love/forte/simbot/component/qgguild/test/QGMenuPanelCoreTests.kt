package love.forte.simbot.component.qgguild.test

import io.ktor.client.engine.mock.*
import io.ktor.http.*
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import love.forte.simbot.common.id.StringID.Companion.ID
import love.forte.simbot.component.qguild.internal.bot.QGBotImpl
import love.forte.simbot.component.qguild.panel.create
import love.forte.simbot.component.qguild.panel.update
import love.forte.simbot.qguild.model.panel.CommandPanelRecord
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotSame

/**
 * Core menu and command-panel facade tests.
 *
 * @since 4.7.0
 */
class QGMenuPanelCoreTests : AbstractInteractionTests() {
    @Test
    fun customMenuFacadeForwardsRequestsAndDoesNotCache() = runTest {
        var getCount = 0
        val app = app { request ->
            assertEquals(HttpMethod.Get, request.method)
            getCount++
            respondOk("""{"version":$getCount,"menu":{"items":[]}}""")
        }
        val bot = app.bot() as QGBotImpl

        try {
            assertNotSame(bot.customMenus, bot.customMenus)

            assertEquals(1, bot.customMenus.get().version)
            assertEquals(2, bot.customMenus.get().version)
        } finally {
            app.cancel()
            bot.cancel()
        }
    }

    @Test
    fun commandPanelFacadeCreatesHandlesAndCollectsEveryPagePerRun() = runTest {
        var listRequests = 0
        val app = app { request ->
            when {
                request.method == HttpMethod.Post && request.url.encodedPath == "/v2/panels" ->
                    respondOk("""{"panel_id":"created-panel"}""")

                request.method == HttpMethod.Get && request.url.encodedPath == "/v2/panels" -> {
                    assertEquals("future-scope", request.url.parameters["scope"])
                    assertEquals("2", request.url.parameters["limit"])
                    listRequests++
                    if (request.url.parameters["cursor"] == null) {
                        respondOk(
                            """{"records":[${recordJson("first")}],"next_cursor":"next","is_end":false}"""
                        )
                    } else {
                        assertEquals("next", request.url.parameters["cursor"])
                        respondOk(
                            """{"records":[${recordJson("second")}],"next_cursor":"","is_end":true}"""
                        )
                    }
                }

                request.method == HttpMethod.Get && request.url.encodedPath == "/v2/panels/created-panel" ->
                    respondOk(recordJson("created-panel"))

                request.method == HttpMethod.Put && request.url.encodedPath == "/v2/panels/created-panel" ->
                    respondOk("""{"version":9}""")

                request.method == HttpMethod.Put && request.url.encodedPath == "/v2/panels/created-panel/target" ->
                    respondOk()

                request.method == HttpMethod.Delete && request.url.encodedPath == "/v2/panels/created-panel" ->
                    respondOk()

                else -> error("Unexpected request: ${request.method} ${request.url}")
            }
        }
        val bot = app.bot() as QGBotImpl

        try {
            val handle = bot.commandPanels.create {
                scope = CommandPanelRecord.SCOPE_C2C
                targetType = "future-target"
            }
            assertEquals("created-panel", handle.id.toString())

            val snapshots = bot.commandPanels
                .list("future-scope", limit = 2)
                .asFlow()
                .toList()
            assertEquals(listOf("first", "second"), snapshots.map { it.id.toString() })
            assertEquals("future-scope", snapshots.first().scopeValue)
            assertEquals("future-target", snapshots.first().targetTypeValue)

            bot.commandPanels
                .list("future-scope", limit = 2)
                .asFlow()
                .toList()
            assertEquals(4, listRequests)

            assertEquals("created-panel", handle.get().id.toString())
            assertEquals(9, handle.update { remark = "updated" }.version)
            handle.addUserTargets(userOpenids = listOf("user-openid".ID))
            handle.removeGroupTargets(groupOpenids = listOf("group-openid".ID))
            handle.delete()
        } finally {
            app.cancel()
            bot.cancel()
        }
    }

    private fun recordJson(id: String): String =
        """{"panel_id":"$id","scope":"future-scope","target_type":"future-target","panel":{"items":[],"remark":null,"version":null},"version":3}"""
}
