package love.forte.simbot.tencentguild

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement


public inline fun TencentBot.processor(
    typeName: String,
    crossinline block: suspend Signal.Dispatch.(decoder: Json) -> Unit
) {
    processor {
        if (this.type == typeName) {
            this.block(it)
        }
    }
}


public inline fun <reified R> TencentBot.processor(eventType: EventSignals<R>, crossinline block: suspend (R) -> Unit) {
    processor { decoder ->
        if (type == eventType.type) {
            val eventData: R = decoder.decodeFromJsonElement(eventType.decoder, data)
            block(eventData)
        }
    }

}