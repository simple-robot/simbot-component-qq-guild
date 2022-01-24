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

package love.forte.simbot.tencentguild

import kotlinx.serialization.KSerializer
import love.forte.simbot.ID
import love.forte.simbot.Timestamp
import love.forte.simbot.definition.GuildInfo
import love.forte.simbot.tencentguild.internal.TencentGuildInfoImpl

/**
 *
 * [频道]()
 *
 * @author ForteScarlet
 */
public interface TencentGuildInfo : GuildInfo {

    /**
     * 频道ID
     */
    public val id: ID

    /**
     * 频道名称
     */
    override val name: String

    /**
     * 频道头像地址
     */
    override val icon: String

    /**
     * 创建人用户ID
     */
    override val ownerId: ID

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
    override val description: String

    /**
     * 加入时间.
     */
    public val joinedAt: Timestamp

    /**
     * 游戏绑定公会区服ID，需要特殊申请并配置后才会返回
     */
    public val unionWorldId: String?

    /**
     * 游戏绑定公会/战队ID，需要特殊申请并配置后才会返回
     */
    public val unionOrgId: String?

    override val currentChannel: Int get() = -1
    override val maximumChannel: Int get() = -1
    override val createTime: Timestamp get() = Timestamp.NotSupport
    override val currentMember: Int get() = memberCount
    override val maximumMember: Int get() = maxMembers

    public companion object {
        internal val serializer: KSerializer<out TencentGuildInfo> = TencentGuildInfoImpl.serializer()
    }
}