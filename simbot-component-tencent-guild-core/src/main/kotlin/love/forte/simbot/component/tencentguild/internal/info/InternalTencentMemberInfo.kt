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

package love.forte.simbot.component.tencentguild.internal.info

import love.forte.simbot.ID
import love.forte.simbot.Timestamp
import love.forte.simbot.tencentguild.TencentMemberInfo
import love.forte.simbot.tencentguild.TencentUserInfo

/**
 *
 * @author ForteScarlet
 */
internal data class InternalTencentMemberInfo(
    override var guildId: ID?,
    override var user: TencentUserInfo,
    override var nick: String,
    override var roleIds: List<ID>,
    override var joinedAt: Timestamp,
) : TencentMemberInfo


internal fun TencentMemberInfo.toInternal(
    copy: Boolean = true, copyUser: Boolean = true, copyRoleIds: Boolean = true,
): InternalTencentMemberInfo {
    if (this is InternalTencentMemberInfo) {
        return if (copy) {
            val user = user
            val roleIds = roleIds
            copy(
                user = if (copyUser) user.toInternal(true) else user,
                roleIds = if (copyRoleIds) roleIds.toList() else roleIds
            )
        } else {
            this
        }
    }
    
    return InternalTencentMemberInfo(
        guildId = guildId,
        user = user.toInternal(copyUser),
        nick = nick,
        roleIds = if (copyRoleIds) roleIds.toList() else roleIds,
        joinedAt = joinedAt
    )
}