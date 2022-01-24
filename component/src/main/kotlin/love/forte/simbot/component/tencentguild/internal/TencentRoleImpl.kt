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