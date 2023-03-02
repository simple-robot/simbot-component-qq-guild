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

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import love.forte.simbot.qguild.*
import love.forte.simbot.qguild.api.message.MessageSendApi
import love.forte.simbot.qguild.event.EventSignals
import love.forte.simbot.qguild.event.Signal
import love.forte.simbot.qguild.model.Message

suspend fun main() {
    val bot = tencentGuildBot(
        appId = "app_id",
        appKey = "app_key",
        token = "token",
    )

    // start bot
    bot.start()

    // 添加事件1
    bot.registerProcessor { _, decoded ->
        val dispatch: Signal.Dispatch = this
        if (dispatch.type == "AT_MESSAGE_CREATE") {
            val message: Message = decoded() as Message
            // decoder.decodeFromJsonElement(EventSignals.AtMessages.AtMessageCreate.decoder, jsonElement)

            println(message)
        }
        // do something..
    }

    // 指定监听事件名称1
    bot.registerProcessor("AT_MESSAGE_CREATE") { decoder, decoded ->
        val dispatch: Signal.Dispatch = this
        val message: Message = decoded() as Message
        // decoder.decodeFromJsonElement(EventSignals.AtMessages.AtMessageCreate.decoder, dispatch.data)

        println(message)
    }

    // 指定监听事件名称2
    bot.registerProcessor(EventSignals.AtMessages.AtMessageCreate.type) { decoder, decoded ->
        val dispatch: Signal.Dispatch = this
        val message: Message = decoded() as Message
        // decoder.decodeFromJsonElement(EventSignals.AtMessages.AtMessageCreate.decoder, dispatch.data)

        println(message)
    }

    // 指定监听事件类型1
    bot.registerProcessor(EventSignals.AtMessages.AtMessageCreate) { message ->
        println(message)

        val api = MessageSendApi.create(channelId = message.channelId, content = "content", msgId = message.id)
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
