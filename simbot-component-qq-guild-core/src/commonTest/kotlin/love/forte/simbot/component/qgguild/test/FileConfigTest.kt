package love.forte.simbot.component.qgguild.test

import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.plus
import love.forte.simbot.bot.SerializableBotConfiguration
import love.forte.simbot.component.qguild.QQGuildComponent
import love.forte.simbot.component.qguild.bot.config.IntentsConfig
import love.forte.simbot.component.qguild.bot.config.QGBotFileConfiguration
import love.forte.simbot.component.qguild.bot.config.ShardConfig
import love.forte.simbot.qguild.event.EventIntents
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull


/**
 *
 * @author ForteScarlet
 */
class FileConfigTest {

    @Test
    fun intentsConfigTest() {
        assertEquals(
            IntentsConfig.Names(setOf("PublicGuildMessages")).intents,
            EventIntents.PublicGuildMessages.intents
        )
        assertEquals(
            IntentsConfig.Names(setOf("public_guild_messages")).intents,
            EventIntents.PublicGuildMessages.intents
        )
        assertEquals(
            IntentsConfig.Names(setOf("PublicGuildMessages", "Guilds")).intents,
            EventIntents.Guilds.intents + EventIntents.PublicGuildMessages.intents
        )
        assertEquals(
            IntentsConfig.Names(setOf("public_guild_messages", "guilds")).intents,
            EventIntents.Guilds.intents + EventIntents.PublicGuildMessages.intents
        )
    }

    @Test
    fun botFileConfigurationTest() {
        val json = Json {
            isLenient = true
            serializersModule += QQGuildComponent.messageSerializersModule
        }

        val jsonStr = """
            {
                "component": "simbot.qqguild",
                "ticket": {
                    "appId": "appId-value",
                    "secret": "secret-value",
                    "token": "token-value"
                },
                "config": {
                    "shard": {
                        "type": "full"
                    }
                }
            }
        """.trimIndent()

        val decoded = json.decodeFromString(SerializableBotConfiguration.serializer(), jsonStr)
        assertIs<QGBotFileConfiguration>(decoded)
        assertNotNull(decoded.config)
        assertIs<ShardConfig.Full>(decoded.config!!.shardConfig)
    }

}
