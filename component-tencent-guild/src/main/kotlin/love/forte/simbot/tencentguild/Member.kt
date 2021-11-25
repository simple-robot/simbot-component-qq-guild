package love.forte.simbot.tencentguild

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.LongID
import love.forte.simbot.Timestamp

/**
 *
 * @author ForteScarlet
 */
@Serializable
public data class TencentMember(
    @SerialName("guild_id")
    public val guildId: LongID? = null,
    public val user: TencentUser,
    public val nick: String,
    public val roles: List<LongID>,
    @SerialName("joined_at")
    public val joinedAt: Long
) {
}