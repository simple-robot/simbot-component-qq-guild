package love.forte.simbot.tencentguild.internal

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer
import love.forte.simbot.LongID
import love.forte.simbot.tencentguild.TencentMemberInfo
import love.forte.simbot.tencentguild.TencentUserInfo

/**
 *
 * @author ForteScarlet
 */
@Serializable
internal data class TencentMemberInfoImpl(
    @SerialName("guild_id")
    override val guildId: LongID? = null,
    override val user: TencentUserInfo,
    override val nick: String,
    override val roles: List<LongID>,
    @SerialName("joined_at")
    override val joinedAt: Long
) : TencentMemberInfo {

}
