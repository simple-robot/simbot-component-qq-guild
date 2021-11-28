package love.forte.simbot.tencentguild.internal

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.LongID
import love.forte.simbot.tencentguild.TencentMemberInfo
import love.forte.simbot.tencentguild.TencentMessage
import love.forte.simbot.tencentguild.TencentUserInfo

@Serializable
internal data class TencentMessageImpl(
    override val id: LongID,
    @SerialName("channel_id")
    override val channelId: LongID,
    @SerialName("guild_id")
    override val guildId: LongID,
    override val content: String,
    override val timestamp: Long,
    @SerialName("edited_timestamp")
    override val editedTimestamp: Long,
    @SerialName("mention_everyone")
    override val mentionEveryone: Boolean,
    override val author: TencentUserInfo,
    override val attachments: List<TencentMessage.Attachment> = emptyList(),
    override val embeds: List<TencentMessage.Embed> = emptyList(),
    override val mentions: List<TencentUserInfo> = emptyList(),
    override val member: TencentMemberInfo,
    override val ark: TencentMessage.Ark? = null
) : TencentMessage
