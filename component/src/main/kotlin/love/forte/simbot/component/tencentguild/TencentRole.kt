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

package love.forte.simbot.component.tencentguild

import kotlinx.coroutines.flow.Flow
import love.forte.simbot.ID
import love.forte.simbot.definition.Permission
import love.forte.simbot.definition.Role
import love.forte.simbot.tencentguild.TencentRoleInfo

/**
 * 腾讯频道中所使用的角色类型。
 * @author ForteScarlet
 */
public interface TencentRole : Role, TencentRoleInfo {
    override val id: ID
    override val name: String
    override suspend fun permissions(): Flow<Permission>
    override val color: Int
    override val isHoist: Boolean
    override val number: Int
    override val memberLimit: Int
}