package love.forte.simbot.tencentguild.internal

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.ID
import love.forte.simbot.tencentguild.TencentMessageReaction


/**
 *
 * @see TencentMessageReaction
 * @author ForteScarlet
 */
@Serializable
internal class TencentMessageReactionImpl(
    @SerialName("user_id")
    @Serializable(ID.AsCharSequenceIDSerializer::class)
    override val userId: ID,
    @SerialName("guild_id")
    @Serializable(ID.AsCharSequenceIDSerializer::class)
    override val guildId: ID,
    @SerialName("channel_id")
    @Serializable(ID.AsCharSequenceIDSerializer::class)
    override val channelId: ID,
    override val target: TencentMessageReactionTargetImpl,
    override val emoji: TencentEmojiImpl
) : TencentMessageReaction

/**
 *
 * @see TencentMessageReaction.Target
 */
@Serializable
internal data class TencentMessageReactionTargetImpl(
    @Serializable(ID.AsCharSequenceIDSerializer::class)
    override val id: ID,
    override val type: TencentMessageReaction.TargetType
) : TencentMessageReaction.Target