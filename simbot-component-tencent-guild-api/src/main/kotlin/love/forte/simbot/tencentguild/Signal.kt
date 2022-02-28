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
import kotlinx.serialization.builtins.*
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.*
import kotlinx.serialization.json.*


public sealed interface ReceivedSignal // 接收的，下行
public sealed interface SendingSignal // 发送的，上行

/**
 * [负载](https://bot.q.qq.com/wiki/develop/api/gateway/reference.html)
 *
 * 上下行数据。
 *
 * @author ForteScarlet
 */
@Serializable
public sealed class Signal<D>(@Serializable(Opcode.SerializerByCode::class) public val op: Opcode) {
    @SerialName("d")
    public abstract val data: D

    /**
     * [https://bot.q.qq.com/wiki/develop/api/gateway/reference.html#%E8%BF%9E%E6%8E%A5%E5%88%B0gateway]
     */
    @Serializable
    public data class Hello(@SerialName("d") override val data: Data) : Signal<Hello.Data>(Opcode.Hello) {
        @Serializable
        public data class Data(@SerialName("heartbeat_interval") public val heartbeatInterval: Long)
    }

    /**
     * [https://bot.q.qq.com/wiki/develop/api/gateway/reference.html#%E9%89%B4%E6%9D%83]
     */
    @Serializable
    public data class Identify(@SerialName("d") override val data: Data) : Signal<Identify.Data>(Opcode.Identify) {

        @Serializable
        public data class Data(
            public val token: String,
            public val intents: Intents,
            public val shard: Shard,
            public val properties: Prop
        ) {

            @Serializable
            public data class Prop(
                @SerialName("\$os") public val os: String,
                @SerialName("\$browser") public val browser: String,
                @SerialName("\$device") public val device: String,
            )

        }
    }


    /**
     * 心跳包
     * [https://bot.q.qq.com/wiki/develop/api/gateway/reference.html#%E5%8F%91%E9%80%81%E5%BF%83%E8%B7%B3]
     */
    @Serializable
    public data class Heartbeat(@SerialName("d") override val data: Long?) : Signal<Long?>(Opcode.Heartbeat)

    /**
     * [https://bot.q.qq.com/wiki/develop/api/gateway/reference.html#%E5%8F%91%E9%80%81%E5%BF%83%E8%B7%B3]
     */
    @Serializable
    public object HeartbeatACK : Signal<Unit>(Opcode.HeartbeatACK) {
        @SerialName("d")
        override val data: Unit
            get() = Unit
    }


    @Serializable
    public data class Resume(@SerialName("d") override val data: Data) : Signal<Resume.Data>(Opcode.Resume) {

        @Serializable
        public data class Data(
            public val token: String,
            @SerialName("session_id") public val sessionId: String,
            public val seq: Long
        )
    }


    @Serializable
    public data class Dispatch(
        @SerialName("d") override val data: JsonElement,
        @SerialName("t") public val type: String,
        @SerialName("s") public val seq: Long
    ) : Signal<JsonElement>(Opcode.Dispatch)


}

/**
 * 1, 2 -> [1, 2]
 */
public object SharedSerializer : KSerializer<Shard> {
    private val arraySerializer = IntArraySerializer()
    override fun deserialize(decoder: Decoder): Shard {
        val array = arraySerializer.deserialize(decoder)
        return when {
            array.isEmpty() -> Shard(0, 1)
            array.size == 1 -> Shard(array[0], array[0])
            else -> Shard(array[0], array[1])
        }
    }

    override val descriptor: SerialDescriptor get() = arraySerializer.descriptor

    override fun serialize(encoder: Encoder, value: Shard) {
        arraySerializer.serialize(encoder, intArrayOf(value.value, value.total))
    }


}


@Serializable(SharedSerializer::class)
public data class Shard(val value: Int, val total: Int) {
    public constructor(range: IntRange) : this(range.first, range.last)

    init {
        require(value < total) { "Shared value must less than total, but value $value >= total $total" }
    }

    override fun toString(): String = "[$value,$total]"
}
