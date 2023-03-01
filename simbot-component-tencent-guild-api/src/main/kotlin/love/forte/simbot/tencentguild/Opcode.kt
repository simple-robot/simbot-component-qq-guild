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


