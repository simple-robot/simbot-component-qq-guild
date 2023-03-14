/*
 * Copyright (c) 2022-2023. ForteScarlet.
 *
 * This file is part of simbot-component-tencent-guild.
 *
 * simbot-component-tencent-guild is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * simbot-component-tencent-guild is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with simbot-component-tencent-guild. If not, see <https://www.gnu.org/licenses/>.
 */

import love.forte.simbot.ID
import love.forte.simbot.ability.CompletionPerceivable
import love.forte.simbot.component.qguild.QGBotManagerConfiguration
import love.forte.simbot.component.qguild.useQQGuild
import love.forte.simbot.core.application.createSimpleApplication
import love.forte.simbot.core.event.EventInterceptorsGenerator
import love.forte.simbot.core.event.EventListenerRegistrationDescriptionsGenerator
import love.forte.simbot.event.GroupMessageEvent
import love.forte.simbot.message.At
import love.forte.simbot.message.Text
import love.forte.simbot.message.plus
import love.forte.simbot.qguild.event.EventSignals


private fun EventInterceptorsGenerator.myInterceptors() {
    processingIntercept(114514.ID) {
        println("Processing Intercept 1 start")
        it.proceed().also {
            println("Processing Intercept 1 end")
        }
    }
    listenerIntercept(1.ID) {
        println("Listener Intercept 2 start")
        it.proceed().also {
            println("Listener Intercept 2 end")
        }
    }
    listenerIntercept(2.ID) {
        println("Listener Intercept 3 start")
        it.proceed().also {
            println("Listener Intercept 3 end")
        }
    }
    listenerIntercept(3.ID) {
        println("Listener Intercept 4 start")
        it.proceed().also {
            println("Listener Intercept 4 end")
        }
    }
}


private fun QGBotManagerConfiguration.tencentGuildConfig() {
    botConfigure = { _, _, _ ->
        intentsForShardFactory = { EventSignals.AtMessages.intents }
    }
}

private fun EventListenerRegistrationDescriptionsGenerator.myListeners() {
    GroupMessageEvent { event ->
        println(this)
        
        // 尝试回复消息
        event.reply(At(123.ID) + Text { "你好！" }) // 假如事件实现 ReplyMessageSupport, 则可以直接使用 event.reply(...)
        
        // 获取一些信息
        // event.group.members()
        event.group().members.collect { // 函数式为挂起，属性式为非挂起
            println("Member: $it")
        }
    }
}

private fun QGBotManagerConfiguration.tencentBot(completionPerceivable: CompletionPerceivable<*>) {
    register("", "", "", {
        intentsForShardFactory = { EventSignals.AtMessages.intents }
    }) { bot ->
        completionPerceivable.onCompletion {
            bot.start()
        }
    }
}

/**
 * 程序入口
 */
suspend fun main() {
    createSimpleApplication {
        eventProcessor {
            interceptors {
                myInterceptors()
            }
            listeners {
                myListeners()
            }
        }
        
        useQQGuild {
            botManager {
                tencentGuildConfig()
                tencentBot(it)
            }
        }
    }.join()
    
    
}


