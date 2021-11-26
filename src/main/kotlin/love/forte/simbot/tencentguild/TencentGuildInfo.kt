package love.forte.simbot.tencentguild

import kotlinx.serialization.SerialName
import love.forte.simbot.ID

/**
 *
 * [频道]()
 *
 * @author ForteScarlet
 */
public interface TencentGuildInfo {

    /**
     * 频道ID
     */
    public val id: ID

    /**
     * 频道名称
     */
    public val name: String

    /**
     * 频道头像地址
     */
    public val icon: String

    /**
     * 创建人用户ID
     */
    public val ownerId: String

    /**
     * 当前人是否是创建人
     */
    public val isBotOwner: Boolean

    /**
     * 成员数
     */
    public val memberCount: Int

    /**
     * 最大成员数
     */
    public val maxMembers: Int

    /**
     * 描述
     */
    public val description: String

    /**
     * 加入时间
     */
    public val joinedAt: String

    /**
     * 游戏绑定公会区服ID，需要特殊申请并配置后才会返回
     */
    public val unionWorldId: String?

    /**
     * 游戏绑定公会/战队ID，需要特殊申请并配置后才会返回
     */
    public val unionOrgId: String?
}