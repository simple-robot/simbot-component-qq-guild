/*
 * Copyright (c) 2022-2023. ForteScarlet.
 *
 * This file is part of simbot-component-tencent-guild.
 *
 * simbot-component-tencent-guild is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * simbot-component-tencent-guild is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with simbot-component-tencent-guild. If not, see <https://www.gnu.org/licenses/>.
 */

package love.forte.simbot.component.tencentguild

import love.forte.simbot.ID
import love.forte.simbot.definition.Role
import love.forte.simbot.tencentguild.TencentRoleInfo

/**
 * QQ频道中所使用的角色类型。
 * @author ForteScarlet
 */
public interface TencentRole : Role, TencentRoleInfo {
    override val id: ID
    override val name: String
    override val color: Int
    override val isHoist: Boolean
    override val number: Int
    override val memberLimit: Int
}
