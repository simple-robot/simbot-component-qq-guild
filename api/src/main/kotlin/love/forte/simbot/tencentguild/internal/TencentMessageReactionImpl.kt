/*
 *  Copyright (c) 2022-2022 ForteScarlet <ForteScarlet@163.com>
 *
 *  根据 GNU LESSER GENERAL PUBLIC LICENSE 3 获得许可；
 *  除非遵守许可，否则您不得使用此文件。
 *  您可以在以下网址获取许可证副本：
 *
 *       https://www.gnu.org/licenses/lgpl-3.0-standalone.html
 *
 *   有关许可证下的权限和限制的具体语言，请参见许可证。
 */

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