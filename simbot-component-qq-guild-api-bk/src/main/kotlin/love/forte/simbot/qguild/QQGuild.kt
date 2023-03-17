/*
 * Copyright (c) 2022-2023. ForteScarlet.
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

package love.forte.simbot.qguild

import io.ktor.http.*
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import love.forte.simbot.Timestamp
import love.forte.simbot.instantValue
import love.forte.simbot.toTimestamp
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 *
 * https://bot.q.qq.com/wiki/develop/api/openapi/guild/model.html
 */
public object QQGuild {
    @Suppress("MemberVisibilityCanBePrivate")
    public const val URL_STRING: String = "https://api.sgroup.qq.com"
    
    @JvmField
    public val URL: Url = Url(URL_STRING)
    
    @Suppress("MemberVisibilityCanBePrivate")
    public const val SANDBOX_URL_STRING: String = "https://sandbox.api.sgroup.qq.com"
    
    @JvmField
    public val SANDBOX_URL: Url = Url(SANDBOX_URL_STRING)
    
//    public val serializersModule: SerializersModule = SerializersModule {
//        // region infos
//        polymorphicDefaultDeserializer(TencentGuildInfo::class) {
//            TencentGuildInfoImpl.serializer()
//        }
//        polymorphicDefaultDeserializer(TencentUserInfo::class) {
//            TencentUserInfoImpl.serializer()
//        }
//        polymorphicDefaultDeserializer(TencentRoleInfo::class) {
//            TencentRoleInfoImpl.serializer()
//        }
//        polymorphicDefaultDeserializer(TencentMemberInfo::class) {
//            TencentMemberInfoImpl.serializer()
//        }
//        polymorphicDefaultDeserializer(TencentChannelInfo::class) {
//            TencentChannelInfoImpl.serializer()
//        }
//        // endregion
//
//    }
}




public object TimestampISO8601Serializer : KSerializer<Timestamp> {
    override fun deserialize(decoder: Decoder): Timestamp {
        val decoded = decoder.decodeString()
        return DateTimeFormatter.ISO_OFFSET_DATE_TIME.parse(decoded, Instant::from).toTimestamp()
    }
    
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("ISO8601Timestamp", PrimitiveKind.STRING)
    
    override fun serialize(encoder: Encoder, value: Timestamp) {
        val encoded = DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(value.dateTimeValue)
        encoder.encodeString(encoded)
    }
    
}

private val Timestamp.dateTimeValue: OffsetDateTime
    get() = OffsetDateTime.ofInstant(instantValue, ZoneId.systemDefault())
