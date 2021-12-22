package t

import love.forte.di.annotation.Beans
import love.forte.simboot.SimbootApp
import love.forte.simboot.annotation.Filter
import love.forte.simboot.annotation.Listener
import love.forte.simboot.core.SimbootApplication
import love.forte.simboot.filter.MatchType
import love.forte.simbot.action.replyIfSupport
import love.forte.simbot.event.ChannelMessageEvent
import love.forte.simbot.event.EventListenerProcessingContext
import love.forte.simbot.event.EventProcessingContext
import love.forte.simbot.message.Text


@SimbootApplication
class Main

suspend fun main() {
    // println(EventSignals.AtMessages.intents)
    val context = SimbootApp.run(Main::class)
    context.join()

    // val ins = MyListener()
    // val func = MyListener::class.memberExtensionFunctions.find { it.name == "listener" }!!
    // func.callSuspend(ins, "abc")


}




@Beans
class MyListener {

    @Listener
    @Filter("A", matchType = MatchType.TEXT_CONTAINS)
    suspend fun ChannelMessageEvent.listener(
        context: EventListenerProcessingContext,
        context2: EventProcessingContext,
    ) {
        println(context)
        println(context2)
        println(context == context2)
        replyIfSupport(Text { "Hi~" })
    }

}