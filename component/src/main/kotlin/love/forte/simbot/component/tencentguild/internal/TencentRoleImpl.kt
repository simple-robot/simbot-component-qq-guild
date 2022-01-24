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

package love.forte.simbot.component.tencentguild.internal

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.serialization.Transient
import love.forte.simbot.Api4J
import love.forte.simbot.ID
import love.forte.simbot.LongID
import love.forte.simbot.component.tencentguild.TencentRole
import love.forte.simbot.definition.Permission
import love.forte.simbot.definition.PermissionStatus
import love.forte.simbot.tencentguild.Permissions
import love.forte.simbot.tencentguild.TencentRoleInfo

/**
 *
 * @author ForteScarlet
 */
internal class TencentRoleImpl(
    @Suppress("unused")
    private val bot: TencentGuildBotImpl,
    private val info: TencentRoleInfo
) : TencentRole, TencentRoleInfo by info {

    override suspend fun permissions(): Flow<Permission> = PERMISSIONS.asFlow()

    @Api4J
    override val permissions: List<Permission>
        get() = PERMISSIONS.toList()

    companion object {
        private val PERMISSIONS = listOf(
            TencentRolePermissions(Permissions(Permissions.CHANNEL_MANAGEABLE)),
            TencentRolePermissions(Permissions(Permissions.CHANNEL_VIEWABLE)),
        )
    }

}

private class TencentRolePermissions(permissions: Permissions) : Permission {
    @Transient
    override val id: LongID = permissions.value.ID

    @Transient
    override val status: PermissionStatus = permissions.status
}