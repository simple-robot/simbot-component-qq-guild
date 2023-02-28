package love.forte.simbot.tencentguild.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.tencentguild.ApiModel
import love.forte.simbot.tencentguild.utils.InstantISO8601Serializer
import java.time.Instant

@Serializable
public abstract class SimpleMember {

    /**
     * 用户的昵称
     */
    public abstract val nick: String

    /**
     * 用户在频道内的身份组ID, 默认值可参考 [DefaultRoles](https://bot.q.qq.com/wiki/develop/api/openapi/guild/role_model.html#DefaultRoles)
     */
    public abstract val roles: List<String>

    /**
     * 用户加入频道的时间
     */
    @Serializable(InstantISO8601Serializer::class)
    @SerialName("join_at")
    public abstract val joinedAt: Instant
}

/**
 * [成员对象(Member)](https://bot.q.qq.com/wiki/develop/api/openapi/member/model.html)
 */
@ApiModel
@Serializable
public data class Member(
    /**
     * 用户的频道基础信息，只有成员相关接口中会填充此信息
     */
    val user: User,
    /**
     * 用户的昵称
     */
    val nick: String,
    /**
     * 用户在频道内的身份组ID, 默认值可参考 [DefaultRoles](https://bot.q.qq.com/wiki/develop/api/openapi/guild/role_model.html#DefaultRoles)
     */
    val roles: List<String>,
    /**
     * 用户加入频道的时间
     */
    @Serializable(InstantISO8601Serializer::class) @SerialName("join_at") val joinedAt: Instant
)

/**
 * [MemberWithGuildID](https://bot.q.qq.com/wiki/develop/api/openapi/member/model.html#memberwithguildid)
 *
 */
@ApiModel
@Serializable
public data class MemberWithGuildID(

    /**
     * 频道id
     */
    @SerialName("guild_id") val guildId: String,
    /**
     * 用户的频道基础信息，只有成员相关接口中会填充此信息
     */
    val user: User,
    /**
     * 用户的昵称
     */
    val nick: String,
    /**
     * 用户在频道内的身份组ID, 默认值可参考 [DefaultRoles](https://bot.q.qq.com/wiki/develop/api/openapi/guild/role_model.html#DefaultRoles)
     */
    val roles: List<String>,
    /**
     * 用户加入频道的时间
     */
    @Serializable(InstantISO8601Serializer::class) @SerialName("join_at") val joinedAt: Instant
)
