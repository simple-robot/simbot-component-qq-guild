/*
 *  Copyright (c) 2022-2022 ForteScarlet <ForteScarlet@163.com>
 *
 *  本文件是 simbot-component-tencent-guild 的一部分。
 *
 *  simbot-component-tencent-guild 是自由软件：你可以再分发之和/或依照由自由软件基金会发布的 GNU 通用公共许可证修改之，无论是版本 3 许可证，还是（按你的决定）任何以后版都可以。
 *
 *  发布 simbot-component-tencent-guild 是希望它能有用，但是并无保障;甚至连可销售和符合某个特定的目的都不保证。请参看 GNU 通用公共许可证，了解详情。
 *
 *  你应该随程序获得一份 GNU 通用公共许可证的复本。如果没有，请看:
 *  https://www.gnu.org/licenses
 *  https://www.gnu.org/licenses/gpl-3.0-standalone.html
 *  https://www.gnu.org/licenses/lgpl-3.0-standalone.html
 *
 *
 */

import love.forte.simbot.ID
import love.forte.simbot.ability.CompletionPerceivable
import love.forte.simbot.action.replyIfSupport
import love.forte.simbot.component.tencentguild.TencentGuildBotManagerConfiguration
import love.forte.simbot.component.tencentguild.useTencentGuild
import love.forte.simbot.core.application.createSimpleApplication
import love.forte.simbot.core.event.EventInterceptorsGenerator
import love.forte.simbot.core.event.EventListenersGenerator
import love.forte.simbot.event.EventResult
import love.forte.simbot.event.GroupMessageEvent
import love.forte.simbot.message.At
import love.forte.simbot.message.Text
import love.forte.simbot.message.plus
import love.forte.simbot.tencentguild.EventSignals


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


private fun TencentGuildBotManagerConfiguration.tencentGuildConfig() {
    botConfigure = { _, _, _ ->
        intentsForShardFactory = { EventSignals.AtMessages.intents }
    }
}

private fun EventListenersGenerator.myListeners() {
    GroupMessageEvent { event ->
        println(this)
        
        // 尝试回复消息
        event.replyIfSupport(At(123.ID) + Text { "你好！" }) // 假如事件实现 ReplyMessageSupport, 则可以直接使用 event.reply(...)
        
        // 获取一些信息
        // event.group.members()
        event.group().members().collect { // 函数式为挂起，属性式为非挂起
            println("Member: $it")
        }
        EventResult.of("abc") // return something?
    }
}

private fun TencentGuildBotManagerConfiguration.tencentBot(completionPerceivable: CompletionPerceivable<*>) {
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
        
        useTencentGuild {
            botManager {
                tencentGuildConfig()
                tencentBot(it)
            }
        }
    }.join()
    
    
}


