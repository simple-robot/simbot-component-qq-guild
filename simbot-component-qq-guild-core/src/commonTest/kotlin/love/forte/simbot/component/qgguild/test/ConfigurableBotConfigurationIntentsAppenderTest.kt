package love.forte.simbot.component.qgguild.test

import love.forte.simbot.qguild.event.EventIntents
import love.forte.simbot.qguild.event.intents
import love.forte.simbot.qguild.stdlib.ConfigurableBotConfiguration
import love.forte.simbot.qguild.stdlib.MessageDestination
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue


class ConfigurableBotConfigurationIntentsAppenderTest {
    @Test
    fun testIntentsDsl() {
        val configuration = ConfigurableBotConfiguration()

        configuration.intents {
            audioAction()
        }

        assertTrue { EventIntents.AudioAction.intents in configuration.intents }
        assertFalse { EventIntents.ForumsEvent.intents in configuration.intents }

        configuration.intents {
            forumsEvent()
        }

        assertTrue { EventIntents.ForumsEvent.intents in configuration.intents }
    }

    @Test
    fun contentAsMarkdownAllTest() {
        val configuration = ConfigurableBotConfiguration()

        configuration.contentAsMarkdownAll(false)
        assertTrue(MessageDestination.entries.all { configuration.contentAsMarkdown[it] == false })

        configuration.contentAsMarkdownAll()
        assertTrue(MessageDestination.entries.all { configuration.contentAsMarkdown[it] == true })
    }}
