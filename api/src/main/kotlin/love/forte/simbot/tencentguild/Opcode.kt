package love.forte.simbot.tencentguild

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder


public sealed interface ReceiveAble
public sealed interface SendAble

@Serializable(Opcode.SerializerByCode::class)
public sealed class Opcode(public val code: Int) {
    public val isReceive: Boolean get() = this is ReceiveAble
    public val isSend: Boolean get() = this is SendAble


    // 0
    public object Dispatch : Opcode(0), ReceiveAble
    // 1
    public object Heartbeat : Opcode(1), ReceiveAble, SendAble
    // 2
    public object Identify : Opcode(2), SendAble
    // 6
    public object Resume : Opcode(6), SendAble
    // 7
    public object Reconnect : Opcode(7), ReceiveAble
    // 9
    public object InvalidSession : Opcode(9), ReceiveAble
    // 10
    public object Hello : Opcode(10), ReceiveAble
    // 11
    public object HeartbeatACK : Opcode(11), ReceiveAble


    public object SerializerByCode : KSerializer<Opcode> {
        override fun deserialize(decoder: Decoder): Opcode {
            return when(val code = decoder.decodeInt()) {
                Dispatch.code -> Dispatch
                Heartbeat.code -> Heartbeat
                Identify.code -> Identify
                Resume.code -> Resume
                Reconnect.code -> Reconnect
                InvalidSession.code -> InvalidSession
                Hello.code -> Hello
                HeartbeatACK.code -> HeartbeatACK
                else -> throw NoSuchElementException("opcode: $code")
            }
        }
        override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("opcode", PrimitiveKind.INT)
        override fun serialize(encoder: Encoder, value: Opcode) {
            encoder.encodeInt(value.code)
        }

    }
}


