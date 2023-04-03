package love.forte.simbot.component.qgguild.test

import love.forte.simbot.component.qguild.config.IntentsConfig
import love.forte.simbot.qguild.event.EventIntents
import kotlin.test.Test


/**
 *
 * @author ForteScarlet
 */
class FileConfigTest {

    @Test
    fun intentsConfigTest() {
        assert(IntentsConfig.Names(setOf("PublicGuildMessages")).intents == EventIntents.PublicGuildMessages.intents)
        assert(IntentsConfig.Names(setOf("public_guild_messages")).intents == EventIntents.PublicGuildMessages.intents)

        assert(IntentsConfig.Names(setOf("PublicGuildMessages", "Guilds")).intents == EventIntents.Guilds.intents + EventIntents.PublicGuildMessages.intents)
        assert(IntentsConfig.Names(setOf("public_guild_messages", "guilds")).intents == EventIntents.Guilds.intents + EventIntents.PublicGuildMessages.intents)
    }

}
