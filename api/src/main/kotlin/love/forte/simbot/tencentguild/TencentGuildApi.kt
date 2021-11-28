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
import love.forte.simbot.tencentguild.internal.*
import love.forte.simbot.toTimestamp
import java.time.Instant

/**
 *
 * https://bot.q.qq.com/wiki/develop/api/openapi/guild/model.html
 */
public object TencentGuildApi {
    public val URL: Url = Url("https://api.sgroup.qq.com/")
    public val SANDBOX_URL: Url = Url("https://sandbox.api.sgroup.qq.com")
    public val serializersModule: SerializersModule = SerializersModule {
        //region infos
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
        //endregion

    }
}



public object TimestampISO8601Serializer : KSerializer<Timestamp> {
    override fun deserialize(decoder: Decoder): Timestamp {
        return Instant.parse(decoder.decodeString()).toTimestamp()
    }

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("ISO8601Timestamp", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Timestamp) {
        encoder.encodeString(value.toString())
    }

}