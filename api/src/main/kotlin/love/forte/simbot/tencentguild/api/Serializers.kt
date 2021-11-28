package love.forte.simbot.tencentguild.api

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder


/**
 * `0` -> false
 * other -> true, decode true to `1`
 */
public object BooleanToNumber : KSerializer<Boolean> {
    override fun deserialize(decoder: Decoder): Boolean {
        return decoder.decodeInt() != 0
    }

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("BooleanToByte", PrimitiveKind.BOOLEAN)

    override fun serialize(encoder: Encoder, value: Boolean) {
        encoder.encodeByte(if (value) 1 else 0)
    }

}