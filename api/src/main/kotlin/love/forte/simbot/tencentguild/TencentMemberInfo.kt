package love.forte.simbot.tencentguild

import kotlinx.serialization.KSerializer
import love.forte.simbot.ID
import love.forte.simbot.Timestamp
import love.forte.simbot.definition.MemberInfo
import love.forte.simbot.tencentguild.internal.TencentMemberInfoImpl

/**
 * [成员](https://bot.q.qq.com/wiki/develop/api/openapi/member/model.html)
 *
 * @author ForteScarlet
 */
public interface TencentMemberInfo : MemberInfo {
    /**
     * 频道id
     */
    public val guildId: ID?

    /**
     * 用户基础信息
     */
    public val user: TencentUserInfo

    /**
     * 用户在频道内的昵称
     */
    public val nick: String

    /**
     * 用户在频道内的身份
     */
    public val roles: List<ID>

    /**
     * iISO8601 timestamp	用户加入频道的时间
     */
    public val joinedAt: Timestamp

    override val avatar: String
        get() = user.avatar
    override val id: ID
        get() = user.id
    override val username: String
        get() = user.username
    override val joinTime: Timestamp
        get() = joinedAt
    override val nickname: String
        get() = nick

    public companion object {
        internal val serializer: KSerializer<out TencentMemberInfo> = TencentMemberInfoImpl.serializer()
    }
}