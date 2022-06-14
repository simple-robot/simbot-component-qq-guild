/*
 *  Copyright (c) 2022-2022 ForteScarlet <ForteScarlet@163.com>
 *
 *  本文件是 simbot-component-tencent-guild 的一部分。
 *
 *  simbot-component-tencent-guild 是自由软件：你可以再分发之和/或依照由自由软件基金会发布的 GNU 通用公共许可证修改之，无论是版本 3 许可证，还是（按你的决定）任何以后版都可以。
 *
 *  发布 simbot-component-tencent-guild 是希望它能有用，但是并无保障;甚至连可销售和符合某个特定的目的都不保证。请参看 GNU 通用公共许可证，了解详情。
 *
 *  你应该随程序获得一份 GNU 通用公共许可证的复本。如果没有，请看:
 *  https://www.gnu.org/licenses
 *  https://www.gnu.org/licenses/gpl-3.0-standalone.html
 *  https://www.gnu.org/licenses/lgpl-3.0-standalone.html
 *
 *
 */

package love.forte.simbot.tencentguild

import io.ktor.http.*
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.modules.SerializersModule
import love.forte.simbot.Timestamp
import love.forte.simbot.instantValue
import love.forte.simbot.tencentguild.internal.*
import love.forte.simbot.toTimestamp
import java.time.Instant
import java.time.format.DateTimeFormatter

/**
 *
 * https://bot.q.qq.com/wiki/develop/api/openapi/guild/model.html
 */
public object TencentGuildApi {
    @Suppress("MemberVisibilityCanBePrivate")
    public const val URL_STRING: String = "https://api.sgroup.qq.com"
    
    @JvmField
    public val URL: Url = Url(URL_STRING)
    
    @Suppress("MemberVisibilityCanBePrivate")
    public const val SANDBOX_URL_STRING: String = "https://sandbox.api.sgroup.qq.com"
    
    @JvmField
    public val SANDBOX_URL: Url = Url(SANDBOX_URL_STRING)
    
    public val serializersModule: SerializersModule = SerializersModule {
        // region infos
        polymorphicDefault(TencentGuildInfo::class) {
            TencentGuildInfoImpl.serializer()
        }
        polymorphicDefault(TencentUserInfo::class) {
            TencentUserInfoImpl.serializer()
        }
        polymorphicDefault(TencentRoleInfo::class) {
            TencentRoleInfoImpl.serializer()
        }
        polymorphicDefault(TencentMemberInfo::class) {
            TencentMemberInfoImpl.serializer()
        }
        polymorphicDefault(TencentChannelInfo::class) {
            TencentChannelInfoImpl.serializer()
        }
        // endregion
        
    }
}


public object TimestampISO8601Serializer : KSerializer<Timestamp> {
    override fun deserialize(decoder: Decoder): Timestamp {
        return DateTimeFormatter.ISO_INSTANT.parse(decoder.decodeString(), Instant::from).toTimestamp()
        // return Instant.parse(decoder.decodeString()).toTimestamp()
    }
    
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("ISO8601Timestamp", PrimitiveKind.STRING)
    
    override fun serialize(encoder: Encoder, value: Timestamp) {
        encoder.encodeString(DateTimeFormatter.ISO_INSTANT.format(value.instantValue))
    }
    
}