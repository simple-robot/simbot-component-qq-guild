import love.forte.simbot.component.qguild.event.QGChannelUpdateEvent
import love.forte.simbot.component.qguild.event.QGEvent
import love.forte.simbot.component.qguild.event.QGMemberUpdateEvent
import love.forte.simbot.component.qguild.qqGuildBots
import love.forte.simbot.component.qguild.useQQGuild
import love.forte.simbot.core.application.createSimpleApplication
import love.forte.simbot.core.event.listeners
import love.forte.simbot.utils.item.toList

suspend fun main() {
    val app = createSimpleApplication {
        useQQGuild()
    }

    app.eventListenerManager.listeners {
        QGMemberUpdateEvent { event ->
            println("UPDATE: $event")
            println("member:   ${event.member()}")
            println("operator: ${event.operator()}")
            println("guild:    ${event.guild()}")
        }
    }

    app.qqGuildBots {
        val bot = register("101986850", "972f64f7c426096f9344b74ba85102fb", "g57N4WsHHRIx1udptqy7GBAEVsfLgynq") {
            botConfig {
                useSandboxServerUrl()
                apiClient
            }
            initConfig.parallel.guild = 2
        }
        bot.start()
        val list = bot.guilds.toList()
        println(list)
        println(list.size)

        val home = list.first()
        home.channels.collect {
            println("Channel: $it")
        }
        home.categories.collect {
            println("Category: $it")
        }

        println("Owner:       ${home.owner()}")
        println("Permissions: ${home.permissions}")
    }


    app.join()

}
