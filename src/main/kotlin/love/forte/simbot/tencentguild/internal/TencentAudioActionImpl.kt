package love.forte.simbot.tencentguild.internal

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.ID
import love.forte.simbot.tencentguild.TencentAudioAction

@Serializable
public data class TencentAudioActionImpl(
    @SerialName("guild_id")
    override val guildId: ID,
    @SerialName("channel_id")
    override val channelId: ID,
    @SerialName("audio_url")
    override val audioUrl: String,
    override val text: String
) : TencentAudioAction
