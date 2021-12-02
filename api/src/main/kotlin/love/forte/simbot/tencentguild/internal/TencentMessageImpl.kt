package love.forte.simbot.tencentguild.internal

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.CharSequenceID
import love.forte.simbot.Timestamp
import love.forte.simbot.tencentguild.TencentMessage
import love.forte.simbot.tencentguild.TimestampISO8601Serializer

@Serializable
internal data class TencentMessageImpl(
    override val id: CharSequenceID,
    @SerialName("channel_id")
    override val channelId: CharSequenceID,
    @SerialName("guild_id")
    override val guildId: CharSequenceID,
    override val content: String,
    @Serializable(TimestampISO8601Serializer::class)
    override val timestamp: Timestamp,
    @SerialName("edited_timestamp")
    override val editedTimestamp: Timestamp = Timestamp.NotSupport,
    @SerialName("mention_everyone")
    override val mentionEveryone: Boolean = false,
    override val author: TencentUserInfoImpl,
    override val attachments: List<TencentMessage.Attachment> = emptyList(),
    override val embeds: List<TencentMessage.Embed> = emptyList(),
    override val mentions: List<TencentUserInfoImpl> = emptyList(),
    override val member: TencentMemberInfoImplForMessage,
    override val ark: TencentMessage.Ark? = null
) : TencentMessage {
    init {
        if (member.guildId == null) {
            member.guildId = guildId
        }
        member.user = author

    }
}
