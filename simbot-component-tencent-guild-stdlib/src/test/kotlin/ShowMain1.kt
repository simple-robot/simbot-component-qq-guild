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

import kotlinx.coroutines.*
import kotlinx.serialization.json.*
import love.forte.simbot.tencentguild.*
import love.forte.simbot.tencentguild.api.message.*

suspend fun main() {
    val bot = tencentBot(
        appId = "app_id",
        appKey = "app_key",
        token = "token",
    )

    // start bot
    bot.start()

    // 添加事件1
    bot.processor { decoder, decoded ->
        val dispatch: Signal.Dispatch = this
        val jsonElement: JsonElement = dispatch.data
        if (dispatch.type == "AT_MESSAGE_CREATE") {
            val message: TencentMessage = decoded() as TencentMessage
            // decoder.decodeFromJsonElement(EventSignals.AtMessages.AtMessageCreate.decoder, jsonElement)

            println(message)
        }
        // do something..
    }

    // 指定监听事件名称1
    bot.processor("AT_MESSAGE_CREATE") { decoder, decoded ->
        val dispatch: Signal.Dispatch = this
        val message: TencentMessage = decoded() as TencentMessage
        // decoder.decodeFromJsonElement(EventSignals.AtMessages.AtMessageCreate.decoder, dispatch.data)

        println(message)
    }

    // 指定监听事件名称2
    bot.processor(EventSignals.AtMessages.AtMessageCreate.type) { decoder, decoded ->
        val dispatch: Signal.Dispatch = this
        val message: TencentMessage = decoded() as TencentMessage
        // decoder.decodeFromJsonElement(EventSignals.AtMessages.AtMessageCreate.decoder, dispatch.data)

        println(message)
    }

    // 指定监听事件类型1
    bot.processor(EventSignals.AtMessages.AtMessageCreate) { message ->
        println(message)

        val api = MessageSendApi(channelId = message.channelId, content = "content", msgId = message.id)
        // 发送回复消息
        val result = api.requestBy(bot)
        println(result)
    }

    // 所有事件都存在于 EventSignals 下的子类型中。

    bot.launch {
        delay(10_000)
        // 模拟bot关闭
        bot.cancel()
    }

    // join bot
    // 挂起直到bot被关闭
    bot.join()

}