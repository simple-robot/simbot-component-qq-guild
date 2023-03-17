/*
 * Copyright (c) 2023. ForteScarlet.
 *
 * This file is part of simbot-component-qq-guild.
 *
 * simbot-component-qq-guild is free software: you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * simbot-component-qq-guild is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with simbot-component-qq-guild.
 * If not, see <https://www.gnu.org/licenses/>.
 */

package love.forte.simbot.qguild.utils

import kotlinx.datetime.Instant
import kotlinx.datetime.serializers.InstantIso8601Serializer
import kotlinx.serialization.KSerializer


/**
 * 为 [Instant] 提供 `ISO8601` 格式支持的序列化器。
 */
public object InstantISO8601Serializer : KSerializer<Instant> by InstantIso8601Serializer
//{
//    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("ISO8601Instant", PrimitiveKind.STRING)
//    override fun deserialize(decoder: Decoder): Instant {
//        val decoded = decoder.decodeString()
//        return DateTimeFormatter.ISO_OFFSET_DATE_TIME.parse(decoded, Instant::from)
//    }
//
//    override fun serialize(encoder: Encoder, value: Instant) {
//        encoder.encodeString(DateTimeFormatter.ISO_INSTANT.format(value))
//    }
//}
