/*
 * Copyright (c) 2023. ForteScarlet.
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

import love.forte.simbot.tencentguild.TencentGuildObjective


/**
 *
 * 代表为一个包含一个 [TencentGuildObjective] 类型的[源][source]。
 *
 * @author ForteScarlet
 */
public interface TencentGuildObjectiveContainer<T> {
    // TODO 尚未完善
    
    /**
     * 得到此容器中保留的原始的 [TencentGuildObjective] 对象。
     */
    public val source: T
    
}
