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
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonElement
import love.forte.simbot.tencentguild.*
import love.forte.simbot.tencentguild.api.message.MessageSendApi

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
        val result = api.request(bot)
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