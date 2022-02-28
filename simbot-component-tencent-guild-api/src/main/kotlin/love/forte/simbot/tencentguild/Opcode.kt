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

import kotlinx.serialization.*
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.*


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
            return when (val code = decoder.decodeInt()) {
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


