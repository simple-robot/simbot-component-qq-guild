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
public interface TencentGuildInfo : GuildInfo, TencentGuildObjective {

    /**
     * 频道ID
     */
    override val id: ID

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