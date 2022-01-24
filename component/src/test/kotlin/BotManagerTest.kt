/*
 *  Copyright (c) 2022-2022 ForteScarlet <ForteScarlet@163.com>
 *
 *  根据 GNU LESSER GENERAL PUBLIC LICENSE 3 获得许可；
 *  除非遵守许可，否则您不得使用此文件。
 *  您可以在以下网址获取许可证副本：
 *
 *       https://www.gnu.org/licenses/lgpl-3.0-standalone.html
 *
 *   有关许可证下的权限和限制的具体语言，请参见许可证。
 */

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import love.forte.simbot.ID
import love.forte.simbot.LoggerFactory
import love.forte.simbot.action.replyIfSupport
import love.forte.simbot.component.tencentguild.TencentGuildBot
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
    ) { context: EventListenerProcessingContext, event: GroupMessageEvent ->

        println(context)

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
    listenerManager.register(coreListener(GroupMessageEvent) { context: EventListenerProcessingContext, event: GroupMessageEvent ->
        // do some
    })




    listenerManager.listen(eventKey = ChannelMessageEvent) { context, event ->
        // do

        null // result
    }
    val bot: TencentGuildBot = botManager.register("", "", "") {
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
    invoker: suspend (EventListenerProcessingContext, E) -> Any?
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
