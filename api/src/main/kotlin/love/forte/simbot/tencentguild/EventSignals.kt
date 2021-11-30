package love.forte.simbot.tencentguild

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer
import love.forte.simbot.CharSequenceID

/**
 * [intents](https://bot.q.qq.com/wiki/develop/api/gateway/intents.html#intents)
 *
 * 合并两种监听类型：
 * ```kotlin
 * val intA = Intents(...)
 * val intB = Intents(...)
 * val intC = intA + intB
 * ```
 *
 */
@JvmInline
@Serializable
public value class Intents(public val value: Int) {
    public operator fun plus(intents: Intents): Intents = Intents(value or intents.value)
}


/**
 * 接收到的事件的类型以及它们对应的数据解析器.
 */
public sealed class EventSignals<out D>(
    public val type: String,
    public val decoder: DeserializationStrategy<out D>
) {
    public companion object {
        public inline val allIntents: Intents get() =
            Guilds.intents + GuildMembers.intents + DirectMessage.intents + AudioAction.intents + AtMessages.intents
    }
    /*
    GUILDS (1 << 0)
      - GUILD_CREATE          // 当机器人加入新guild时
      - GUILD_UPDATE          // 当guild资料发生变更时
      - GUILD_DELETE          // 当机器人退出guild时
      - CHANNEL_CREATE        // 当channel被创建时
      - CHANNEL_UPDATE        // 当channel被更新时
      - CHANNEL_DELETE        // 当channel被删除时

    GUILD_MEMBERS (1 << 1)
      - GUILD_MEMBER_ADD      // 当成员加入时
      - GUILD_MEMBER_UPDATE   // 当成员资料变更时
      - GUILD_MEMBER_REMOVE   // 当成员被移除时

    DIRECT_MESSAGE (1 << 12)
      - DIRECT_MESSAGE_CREATE // 当收到用户发给机器人的私信消息时

    AUDIO_ACTION (1 << 29)
      - AUDIO_START           // 音频开始播放时
      - AUDIO_FINISH          // 音频播放结束时
      - AUDIO_ON_MIC          // 上麦时
      - AUDIO_OFF_MIC         // 下麦时

    AT_MESSAGES (1 << 30)
      - AT_MESSAGE_CREATE     // 当收到@机器人的消息时
     */

    public val events: Map<String, EventSignals<*>> = mapOf(
        "READY" to Other.ReadyEvent,
        "RESUMED" to Other.Resumed,
        "GUILD_CREATE" to Guilds.GuildCreate,
        "GUILD_UPDATE" to Guilds.GuildUpdate,
        "GUILD_DELETE" to Guilds.GuildDelete,
        "CHANNEL_CREATE" to Guilds.ChannelCreate,
        "CHANNEL_UPDATE" to Guilds.ChannelUpdate,
        "CHANNEL_DELETE" to Guilds.ChannelDelete,
        "GUILD_MEMBER_ADD" to GuildMembers.GuildMemberAdd,
        "GUILD_MEMBER_UPDATE" to GuildMembers.GuildMemberUpdate,
        "GUILD_MEMBER_REMOVE" to GuildMembers.GuildMemberRemove,
        "DIRECT_MESSAGE_CREATE" to DirectMessage.DirectMessageCreate,
        "AUDIO_START" to AudioAction.AudioStart,
        "AUDIO_FINISH" to AudioAction.AudioFinish,
        "AUDIO_ON_MIC" to AudioAction.AudioOnMic,
        "AUDIO_OFF_MIC" to AudioAction.AudioOffMic,
        "AT_MESSAGE_CREATE" to AtMessages.AtMessageCreate,
    )

    public sealed class Other<D>(t: String, decoder: DeserializationStrategy<out D>): EventSignals<D>(t, decoder) {
        /**
         * 鉴权后的ready
         */
        public object ReadyEvent: Other<ReadyEvent.Data>("READY", Data.serializer()) {
            @Serializable
            public data class Data(
                public val version: Int,
                @SerialName("session_id")
                public val sessionId: String,
                @SerialName("user")
                private val _user: TencentBotInfoImpl,
                public val shard: Shard
            ) {
                public val user: TencentBotInfo get() = _user
            }
        }

        /**
         * 连接重连后的事件重放
         */
        public object Resumed : Other<String>("RESUMED", String.serializer())

    }

    public sealed class Guilds<D>(t: String, decoder: DeserializationStrategy<out D>) : EventSignals<D>(t, decoder) {
        /** 当机器人加入新guild时 */
        public object GuildCreate : Guilds<TencentGuildInfo>("GUILD_CREATE", TencentGuildInfo.serializer)
        /** 当guild资料发生变更时 */
        public object GuildUpdate : Guilds<TencentGuildInfo>("GUILD_UPDATE", TencentGuildInfo.serializer)
        /** 当机器人退出guild时 */
        public object GuildDelete : Guilds<TencentGuildInfo>("GUILD_DELETE", TencentGuildInfo.serializer)
        /** 当channel被创建时 */
        public object ChannelCreate : Guilds<TencentChannelInfo>("CHANNEL_CREATE", TencentChannelInfo.serializer)
        /** 当channel被更新时 */
        public object ChannelUpdate : Guilds<TencentChannelInfo>("CHANNEL_UPDATE", TencentChannelInfo.serializer)
        /** 当channel被删除时 */
        public object ChannelDelete : Guilds<TencentChannelInfo>("CHANNEL_DELETE", TencentChannelInfo.serializer)
        public companion object {
            public val intents: Intents = Intents(1 shl 0)
        }
    }

    public sealed class GuildMembers<D>(t: String, decoder: DeserializationStrategy<out D>) : EventSignals<D>(t, decoder) {
        /** 当成员加入时 */
        public object GuildMemberAdd : GuildMembers<TencentMemberInfo>("GUILD_MEMBER_ADD", TencentMemberInfo.serializer)
        /** 当成员资料变更时 */
        public object GuildMemberUpdate : GuildMembers<Unit>("GUILD_MEMBER_UPDATE", Unit.serializer())
        /** 当成员被移除时 */
        public object GuildMemberRemove : GuildMembers<TencentMemberInfo>("GUILD_MEMBER_REMOVE", TencentMemberInfo.serializer)
        public companion object {
            public val intents: Intents = Intents(1 shl 1)
        }
    }

    public sealed class DirectMessage<D>(t: String, decoder: DeserializationStrategy<out D>) : EventSignals<D>(t, decoder) {
        /** 当收到用户发给机器人的私信消息时 */
        public object DirectMessageCreate : DirectMessage<TencentMessage>("DIRECT_MESSAGE_CREATE", TencentMessage.serializer)
        public companion object {
            public val intents: Intents = Intents(1 shl 12)
        }
    }

    public sealed class AudioAction<D>(t: String, decoder: DeserializationStrategy<out D>) : EventSignals<D>(t, decoder) {
        /** 音频开始播放时 */
        public object AudioStart : AudioAction<TencentAudioAction>("AUDIO_START", TencentAudioAction.serializer)
        /** 音频播放结束时 */
        public object AudioFinish : AudioAction<TencentAudioAction>("AUDIO_FINISH", TencentAudioAction.serializer)
        /** 上麦时 */
        public object AudioOnMic : AudioAction<TencentAudioAction>("AUDIO_ON_MIC", TencentAudioAction.serializer)
        /** 下麦时 */
        public object AudioOffMic : AudioAction<TencentAudioAction>("AUDIO_OFF_MIC", TencentAudioAction.serializer)
        public companion object {
            public val intents: Intents = Intents(1 shl 29)
        }
    }

    public sealed class AtMessages<D>(t: String, decoder: DeserializationStrategy<out D>) : EventSignals<D>(t, decoder) {
        /** 当收到@机器人的消息时 */
        public object AtMessageCreate : AtMessages<TencentMessage>("AT_MESSAGE_CREATE", TencentMessage.serializer)

        public companion object {
            public val intents: Intents = Intents(1 shl 30)
        }
    }

}





@Serializable
public data class TencentBotInfoImpl(
    override val id: CharSequenceID,
    override val username: String,
    @SerialName("bot")
    override val isBot: Boolean = true,
    override val avatar: String = "",
    override val unionOpenid: String? = null,
    override val unionUserAccount: String? = null,
) : TencentBotInfo