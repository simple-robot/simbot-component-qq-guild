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