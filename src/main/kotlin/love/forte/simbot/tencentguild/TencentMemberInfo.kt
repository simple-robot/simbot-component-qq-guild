package love.forte.simbot.tencentguild

import love.forte.simbot.ID

/**
 * [成员](https://bot.q.qq.com/wiki/develop/api/openapi/member/model.html)
 *
 * @author ForteScarlet
 */
public interface TencentMemberInfo {
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
    public val joinedAt: Long

}