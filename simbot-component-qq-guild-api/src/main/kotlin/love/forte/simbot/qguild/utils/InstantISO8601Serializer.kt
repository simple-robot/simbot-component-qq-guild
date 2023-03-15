/*
 * Copyright (c) 2023. ForteScarlet.
 *
 * This file is part of simbot-component-qq-guild.
 *
 * simbot-component-qq-guild is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * simbot-component-qq-guild is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with simbot-component-qq-guild. If not, see <https://www.gnu.org/licenses/>.
 */

package love.forte.simbot.qguild.utils

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import love.forte.simbot.qguild.utils.InstantISO8601Serializer.deserialize
import java.time.Instant
import java.time.format.DateTimeFormatter


/**
 * 为 [Instant] 提供 [ISO8601][DateTimeFormatter.ISO_OFFSET_DATE_TIME] 格式支持的序列化器。
 *
 * 内部使用 [DateTimeFormatter.ISO_OFFSET_DATE_TIME] 作为 [deserialize] 的解析器，因为 [Instant] 的默认解析器 [DateTimeFormatter.ISO_INSTANT] 似乎无法解析日期的时区格式。
 *
 */
public object InstantISO8601Serializer : KSerializer<Instant> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("ISO8601Instant", PrimitiveKind.STRING)
    override fun deserialize(decoder: Decoder): Instant {
        val decoded = decoder.decodeString()
        return DateTimeFormatter.ISO_OFFSET_DATE_TIME.parse(decoded, Instant::from)
    }

    override fun serialize(encoder: Encoder, value: Instant) {
        encoder.encodeString(DateTimeFormatter.ISO_INSTANT.format(value))
    }
}
