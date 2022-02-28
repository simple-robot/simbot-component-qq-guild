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

package love.forte.simbot.tencentguild.internal

import kotlinx.serialization.*
import love.forte.simbot.*
import love.forte.simbot.tencentguild.*

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
