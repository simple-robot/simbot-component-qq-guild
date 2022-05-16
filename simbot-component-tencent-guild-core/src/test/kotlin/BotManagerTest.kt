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

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import love.forte.simbot.ID
import love.forte.simbot.LoggerFactory
import love.forte.simbot.action.replyIfSupport
import love.forte.simbot.component.tencentguild.TencentGuildComponentBot
import love.forte.simbot.component.tencentguild.tencentGuildBotManager
import love.forte.simbot.core.event.coreListener
import love.forte.simbot.core.event.coreListenerManager
import love.forte.simbot.core.event.listen
import love.forte.simbot.event.*
import love.forte.simbot.message.At
import love.forte.simbot.message.Text
import love.forte.simbot.message.plus
import love.forte.simbot.tencentguild.EventSignals
import org.slf4j.Logger


private val listenerManager = coreListenerManager {
    interceptors {
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
}


private val botManager = tencentGuildBotManager(listenerManager) {
    botConfigure = { _, _, _ ->
        intentsForShardFactory = { EventSignals.AtMessages.intents }
    }
}


suspend fun main() {
    val listener = listenerManager.listen(
        eventKey = GroupMessageEvent, // 实际上为伴生对象 GroupMessageEvent.Key
        // id = UUID.randomUUID().ID, // 可省略参数：唯一ID
        // blockNext = false, // 可省略参数：是否阻断下一个函数的执行
        // isAsync = false, // 可省略参数：是否异步执行
    ) { event: GroupMessageEvent ->
        
        println(this)
        
        // 尝试回复消息
        event.replyIfSupport(At(123.ID) + Text { "你好！" }) // 假如事件实现 ReplyMessageSupport, 则可以直接使用 event.reply(...)
        
        // 获取一些信息
        // event.group.members()
        event.group().members().collect { // 函数式为挂起，属性式为非挂起
            println("Member: $it")
        }
        
        // do something?
        
        
        "abc" // return something?
    }
    
    
    // 方式2：直接通过 register注册一个实例
    listenerManager.register(coreListener(GroupMessageEvent) {
        // do some
    })
    
    
    
    
    listenerManager.listen(eventKey = ChannelMessageEvent) {
        // do
        
        null // result
    }
    val bot: TencentGuildComponentBot = botManager.register("", "", "") {
        intentsForShardFactory = { EventSignals.AtMessages.intents }
    }
    
    val id = bot.id
    
    println(botManager.get(id))
    
    bot.launch {
        delay(10_000)
        bot.cancel()
    }
    
    bot.start()
    
    bot.join()
    
    println(botManager.get(id))
    
    
}


fun <E : Event> listener(
    id: ID,
    type: Event.Key<E>,
    invoker: suspend (EventListenerProcessingContext, E) -> Any?,
): EventListener =
    object : EventListener {
        override val logger: Logger = LoggerFactory.getLogger("a")
        override val id: ID get() = id
        override val isAsync: Boolean get() = false
        
        override fun isTarget(eventType: Event.Key<*>): Boolean = eventType.isSubFrom(type)
        override suspend fun invoke(context: EventListenerProcessingContext): EventResult {
            val result = invoker(context, type.safeCast(context.event)!!)
            return EventResult.of(result)
        }
    }
