import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import love.forte.simbot.ID
import love.forte.simbot.component.tencentguild.TencentGuildBot
import love.forte.simbot.component.tencentguild.tencentGuildBotManager
import love.forte.simbot.core.event.coreEventManager
import love.forte.simbot.event.*
import love.forte.simbot.tencentguild.EventSignals


val listenerManager = coreEventManager {
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


val botManager = tencentGuildBotManager {
    this.eventProcessor = listenerManager
}

val appId = ""
val appKey = ""
val token = ""


suspend fun main() {
    val bot: TencentGuildBot = botManager.register(appId, appKey, token) {
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


fun <E : Event> listener(id: ID, type: Event.Key<E>, invoker: (EventProcessingContext, E) -> Any?): EventListener =
    object : EventListener {
        override val id: ID get() = id
        override fun isTarget(eventType: Event.Key<*>): Boolean = eventType.isSubFrom(type)
        override suspend fun invoke(context: EventProcessingContext): EventResult {
            val result = invoker(context, type.safeCast(context.event)!!)
            return EventResult.of(result)
        }
    }
