package love.forte.simbot.tencentguild.internal

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import love.forte.simbot.CharSequenceID
import love.forte.simbot.Timestamp
import love.forte.simbot.tencentguild.TencentMemberInfo
import love.forte.simbot.tencentguild.TimestampISO8601Serializer

/**
 *
 * @author ForteScarlet
 */
@Serializable
internal data class TencentMemberInfoImpl(
    @SerialName("guild_id")
    override val guildId: CharSequenceID? = null,
    override val user: TencentUserInfoImpl,
    override val nick: String = "",
    @SerialName("roles")
    override val roleIds: List<CharSequenceID>,
    @SerialName("joined_at")
    @Serializable(TimestampISO8601Serializer::class)
    override val joinedAt: Timestamp
) : TencentMemberInfo


// TODO 临时处理
internal val defImpl = TencentUserInfoImpl

@Serializable
internal data class TencentMemberInfoImplForMessage(
    @SerialName("guild_id")
    override val guildId: CharSequenceID? = null,
    override val nick: String = "",
    @SerialName("roles")
    override val roleIds: List<CharSequenceID>,
    @SerialName("joined_at")
    @Serializable(TimestampISO8601Serializer::class)
    override val joinedAt: Timestamp
) : TencentMemberInfo {
    @Transient
    override lateinit var user: TencentUserInfoImpl
}