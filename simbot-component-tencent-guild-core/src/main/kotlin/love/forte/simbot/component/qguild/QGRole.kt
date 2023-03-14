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

package love.forte.simbot.component.qguild

import love.forte.simbot.ID
import love.forte.simbot.definition.Role
import love.forte.simbot.qguild.model.Role.Companion.isDefault
import love.forte.simbot.qguild.model.Role as QGSourceRole

/**
 * QQ频道中所使用的角色类型。
 * @author ForteScarlet
 */
public interface QGRole : Role {
    override val id: ID
    public val sourceRole: QGSourceRole

    override val name: String get() = sourceRole.name
    public val color: Int get() = sourceRole.color
    public val isHoist: Boolean get() = sourceRole.isHoistBool
    public val number: Int get() = sourceRole.number
    public val memberLimit: Int get() = sourceRole.memberLimit

    /**
     * 判断是拥有管理员权限。
     *
     * 判断标准为是 [默认角色][QGSourceRole.isDefault] 且不是 [全体成员][QGSourceRole.DEFAULT_ID_ALL_MEMBER]。
     *
     */
    override val isAdmin: Boolean get() = sourceRole.isDefault && sourceRole.id != QGSourceRole.DEFAULT_ID_ALL_MEMBER
}
