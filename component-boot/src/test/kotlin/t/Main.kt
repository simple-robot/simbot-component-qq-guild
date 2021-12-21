package t

import love.forte.di.annotation.Beans
import love.forte.simboot.SimbootApp
import love.forte.simboot.annotation.Listener
import love.forte.simboot.core.SimbootApplication
import love.forte.simbot.action.replyIfSupport
import love.forte.simbot.event.ChannelMessageEvent
import love.forte.simbot.message.Text


@SimbootApplication
class Main

suspend fun main() {
    val context = SimbootApp.run(Main::class)


    context.join()
}




@Beans
class MyListener {

    @Listener
    suspend fun ChannelMessageEvent.listener() {
        println("ON listen")
        replyIfSupport(Text { "Hi~" })
    }

}