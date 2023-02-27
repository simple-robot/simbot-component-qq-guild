package love.forte.simbot.tencentguild.utils

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import love.forte.simbot.tencentguild.utils.InstantISO8601Serializer.deserialize
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
