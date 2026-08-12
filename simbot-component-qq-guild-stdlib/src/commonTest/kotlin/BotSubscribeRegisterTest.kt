import io.ktor.client.engine.mock.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.test.runTest
import love.forte.simbot.qguild.event.GuildCreate
import love.forte.simbot.qguild.stdlib.Bot
import love.forte.simbot.qguild.stdlib.BotFactory
import love.forte.simbot.qguild.stdlib.ConfigurableBotConfiguration
import love.forte.simbot.qguild.stdlib.internal.BotImpl
import love.forte.simbot.qguild.stdlib.subscribe
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 *
 * @author ForteScarlet
 */
class BotSubscribeRegisterTest {

    /**
     * 确保普通的 subscribe 和同名扩展函数
     * 不会产生冲突。
     */
    @Test
    fun subscribeTest() {
        val bot = BotImpl(
            Bot.Ticket("", "", ""),
            ConfigurableBotConfiguration().apply {
                apiClientEngine = MockEngine { respondOk() }
                wsClientEngine = MockEngine { respondOk() }
            }
        )

        bot.subscribe {
        }

        bot.subscribe<GuildCreate> {
        }
    }

    @Test
    fun apiClientAdditionalConfigurationIsAppliedWhenBotIsCreated() = runTest {
        var requestCount = 0
        val bot = BotFactory.create(
            Bot.Ticket("", "", ""),
            ConfigurableBotConfiguration().apply {
                wsClientEngine = MockEngine { respondOk() }
                apiClientEngine = MockEngine {
                    assertEquals("enabled", it.headers["X-Test-Configuration"])
                    requestCount++
                    respond(
                        content = "",
                        status = if (requestCount == 1) HttpStatusCode.InternalServerError else HttpStatusCode.OK
                    )
                }
                apiClientAdditionalConfiguration {
                    defaultRequest {
                        headers.append("X-Test-Configuration", "enabled")
                    }
                }
                apiClientAdditionalConfiguration {
                    install(HttpRequestRetry) {
                        maxRetries = 1
                        retryOnServerErrors()
                    }
                }
            }
        )

        try {
            assertEquals(HttpStatusCode.OK, bot.apiClient.get("https://example.test/retry").status)
            assertEquals(2, requestCount)
        } finally {
            bot.apiClient.close()
            bot.cancel()
        }
    }

}
