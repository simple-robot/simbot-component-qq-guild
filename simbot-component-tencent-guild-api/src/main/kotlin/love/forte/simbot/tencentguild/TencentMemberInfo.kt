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
import love.forte.simbot.definition.MemberInfo
import love.forte.simbot.tencentguild.internal.TencentMemberInfoImpl

/**
 * [成员](https://bot.q.qq.com/wiki/develop/api/openapi/member/model.html)
 *
 * @author ForteScarlet
 */
public interface TencentMemberInfo : MemberInfo, TencentGuildObjective {
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
    public val roleIds: List<ID>

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