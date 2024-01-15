/*
 * Copyright (c) 2024. ForteScarlet.
 *
 * This file is part of simbot-component-qq-guild.
 *
 * simbot-component-qq-guild is free software: you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * simbot-component-qq-guild is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with simbot-component-qq-guild.
 * If not, see <https://www.gnu.org/licenses/>.
 */

import kotlinx.coroutines.flow.take
import love.forte.simbot.component.qguild.event.QGAtMessageCreateEvent
import love.forte.simbot.component.qguild.message.QGReplyTo
import love.forte.simbot.component.qguild.qqGuildBots
import love.forte.simbot.core.application.SimpleApplication
import love.forte.simbot.event.EventResult
import love.forte.simbot.event.listen
import love.forte.simbot.message.messagesOf
import love.forte.simbot.message.toText
import love.forte.simbot.qguild.event.EventIntentsInstances

/*
 * Copyright (c) 2024. ForteScarlet.
 *
 * This file is part of simbot-component-qq-guild.
 *
 * simbot-component-qq-guild is free software: you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * simbot-component-qq-guild is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with simbot-component-qq-guild.
 * If not, see <https://www.gnu.org/licenses/>.
 */


fun main(vararg args: String) {
    launchApp(*args)
}

expect fun launchApp(vararg args: String)

internal suspend fun SimpleApplication.onApp(appid: String, token: String) {

    eventDispatcher.apply {
        listen<QGAtMessageCreateEvent> {
            println("event: $it")
            it.bot.guildRelation.guilds(30)
                .asFlow()
                .take(30)
                .collect { g ->
                    println("Guild: $g")
                }
            it.content().send(messagesOf("Hello!".toText(), QGReplyTo(it.messageContent.id))).also { r ->
                println("sent: $r")
            }
            it.reply("mua！").also { r ->
                println("replied: $r")
            }
            EventResult.empty()
        }

//        listen<ChatChannelMessageEvent> {
//            println("event: $it")
//            it.reply("mua2！").also { r ->
//                println("replied: $r")
//            }
//            EventResult.empty()
//        }
    }

    qqGuildBots {
        register(appid, "", token) {
            botConfig {
                useSandboxServerUrl()
                intentsValue = EventIntentsInstances.foldRight(0) { a, b -> a.intentsValue or b }
            }
        }.apply {
            start()
            println("Bot $this started")
        }
    }

    println("App: $this")
}
