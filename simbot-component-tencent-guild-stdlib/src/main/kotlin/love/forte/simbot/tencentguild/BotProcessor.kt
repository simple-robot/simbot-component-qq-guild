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

package love.forte.simbot.tencentguild

import kotlinx.serialization.json.Json


public inline fun TencentGuildBot.processor(
    typeName: String,
    crossinline block: suspend Signal.Dispatch.(decoder: Json, decoded: () -> Any) -> Unit
) {
    processor { decoder, decoded ->
        if (this.type == typeName) {
            this.block(decoder, decoded)
        }
    }
}


public inline fun <reified R : Any> TencentGuildBot.processor(
    eventType: EventSignals<R>,
    crossinline block: suspend (R) -> Unit
) {
    processor { _, decoded ->
        if (type == eventType.type) {
            // val eventData: R = decoder.decodeFromJsonElement(eventType.decoder, data)
            block(decoded() as R)
        }
    }

}
