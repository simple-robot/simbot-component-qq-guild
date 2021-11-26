package love.forte.simbot.tencentguild

import love.forte.simbot.ID

/**
 *
 * @author ForteScarlet
 */
public interface TencentUserInfo {
    /**
     * 用户 id
     */
    public val id: ID

    /**
     * 用户名
     */
    public val username: String

    /**
     * 用户头像地址
     */
    public val avatar: String

    /**
     * 是否是机器人
     */
    public val isBot: Boolean

    /**
     * 特殊关联应用的 openid，需要特殊申请并配置后才会返回。如需申请，请联系平台运营人员。
     */
    public val unionOpenid: String?

    /**
     * 机器人关联的互联应用的用户信息，与union_openid关联的应用是同一个。如需申请，请联系平台运营人员。
     */
    public val unionUserAccount: String?

}