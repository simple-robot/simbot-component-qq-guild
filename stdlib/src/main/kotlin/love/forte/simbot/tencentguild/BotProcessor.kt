package love.forte.simbot.tencentguild

import kotlinx.serialization.json.Json


public inline fun TencentBot.processor(
    typeName: String,
    crossinline block: suspend Signal.Dispatch.(decoder: Json, decoded: () -> Any) -> Unit
) {
    processor { decoder, decoded ->
        if (this.type == typeName) {
            this.block(decoder, decoded)
        }
    }
}


public inline fun <reified R : Any> TencentBot.processor(eventType: EventSignals<R>, crossinline block: suspend (R) -> Unit) {
    processor { _, decoded ->
        if (type == eventType.type) {
            // val eventData: R = decoder.decodeFromJsonElement(eventType.decoder, data)
            block(decoded() as R)
        }
    }

}