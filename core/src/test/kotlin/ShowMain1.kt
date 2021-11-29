import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.decodeFromJsonElement
import love.forte.simbot.tencentguild.*

suspend fun main() {
    val bot = tencentBot(
        appId = "app_id",
        appKey = "app_key",
        token = "token",
    )

    // start bot
    bot.start()

    // 添加事件1
    bot.processor { decoder ->
        val dispatch: Signal.Dispatch = this
        val jsonElement: JsonElement = dispatch.data
        if (dispatch.type == "AT_MESSAGE_CREATE") {
            val message: TencentMessage =
                decoder.decodeFromJsonElement(EventSignals.AtMessages.AtMessageCreate.decoder, jsonElement)

            println(message)
        }
        // do something..
    }

    // 指定监听事件名称1
    bot.processor("AT_MESSAGE_CREATE") { decoder ->
        val dispatch: Signal.Dispatch = this
        val message: TencentMessage =
            decoder.decodeFromJsonElement(EventSignals.AtMessages.AtMessageCreate.decoder, dispatch.data)

        println(message)
    }

    // 指定监听事件名称2
    bot.processor(EventSignals.AtMessages.AtMessageCreate.type) { decoder ->
        val dispatch: Signal.Dispatch = this
        val message: TencentMessage =
            decoder.decodeFromJsonElement(EventSignals.AtMessages.AtMessageCreate.decoder, dispatch.data)

        println(message)
    }

    // 指定监听事件类型1
    bot.processor(EventSignals.AtMessages.AtMessageCreate) { message ->
        println(message)
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