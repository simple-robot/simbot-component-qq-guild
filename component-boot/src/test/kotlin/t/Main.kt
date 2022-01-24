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