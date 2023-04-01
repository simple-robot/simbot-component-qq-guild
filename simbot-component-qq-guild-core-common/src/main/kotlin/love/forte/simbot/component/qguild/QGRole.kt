/*
 * Copyright (c) 2022-2023. ForteScarlet.
 *
 * This file is part of simbot-component-qq-guild.
 *
 * simbot-component-qq-guild is free software: you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * simbot-component-qq-guild is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with simbot-component-qq-guild.
 * If not, see <https://www.gnu.org/licenses/>.
 */

package love.forte.simbot.component.qguild

import kotlinx.coroutines.CoroutineScope
import love.forte.simbot.ExperimentalSimbotApi
import love.forte.simbot.ID
import love.forte.simbot.definition.Role
import love.forte.simbot.qguild.model.Role.Companion.isDefault
import love.forte.simbot.qguild.model.Role as QGSourceRole

/**
 * QQ频道中所使用的角色类型。
 * @author ForteScarlet
 */
@ExperimentalSimbotApi
public interface QGRole : Role, CoroutineScope, QGObjectiveContainer<QGSourceRole> {
    override val id: ID
    override val source: QGSourceRole

    override val name: String get() = source.name
    public val color: Int get() = source.color
    public val isHoist: Boolean get() = source.isHoistBool
    public val number: Int get() = source.number
    public val memberLimit: Int get() = source.memberLimit

    /**
     * 判断是拥有管理员权限。
     *
     * 判断标准为是 [默认角色][QGSourceRole.isDefault] 且不是 [全体成员][QGSourceRole.DEFAULT_ID_ALL_MEMBER]。
     *
     */
    override val isAdmin: Boolean get() = source.isDefault && source.id != QGSourceRole.DEFAULT_ID_ALL_MEMBER
}

// TODO Guild Role
// TODO Member Role