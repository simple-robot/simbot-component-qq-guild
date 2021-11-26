package love.forte.simbot.tencentguild

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.builtins.IntArraySerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure


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
public sealed class Signal<D>(public val op: Opcode) {
    public abstract val d: D

    /**
     * [https://bot.q.qq.com/wiki/develop/api/gateway/reference.html#%E8%BF%9E%E6%8E%A5%E5%88%B0gateway]
     */
    @Serializable
    public data class Hello(override val d: Data) : Signal<Hello.Data>(Opcode.Hello) {
        @Serializable
        public data class Data(@SerialName("heartbeat_interval") public val heartbeatInterval: Long)
    }

    /**
     * [https://bot.q.qq.com/wiki/develop/api/gateway/reference.html#%E9%89%B4%E6%9D%83]
     */
    @Serializable
    public data class Identify(override val d: Data) : Signal<Identify.Data>(Opcode.Identify) {

        @Serializable
        public data class Data(
            public val token: String,
            public val intents: Int, // TODO Intents
            @Serializable(IntRangeSerializer::class)
            public val shard: IntRange,
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



    @Serializable
    public data class Heartbeat(override val d: Int): Signal<Int>(Opcode.Heartbeat)

    @Serializable
    public object HeartbeatACK: Signal<Unit>(Opcode.HeartbeatACK) {
        override val d: Unit get() = Unit
    }


}

/**
 * 1 .. 2 -> [1, 2]
 */
public object IntRangeSerializer : KSerializer<IntRange> {
    private val arraySerializer = IntArraySerializer()
    override fun deserialize(decoder: Decoder): IntRange {
        val array = arraySerializer.deserialize(decoder)
        return when {
            array.isEmpty() -> IntRange.EMPTY
            array.size == 1 -> array[0]..array[0]
            else -> array[0]..array[1]
        }
    }

    override val descriptor: SerialDescriptor get() = arraySerializer.descriptor

    override fun serialize(encoder: Encoder, value: IntRange) {
        arraySerializer.serialize(encoder, intArrayOf(value.first, value.last))
    }


}
