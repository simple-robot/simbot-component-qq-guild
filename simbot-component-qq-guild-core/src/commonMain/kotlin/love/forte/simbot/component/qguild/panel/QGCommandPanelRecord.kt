/*
 * Copyright (c) 2026. ForteScarlet.
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

package love.forte.simbot.component.qguild.panel

import love.forte.simbot.component.qguild.QGObjectiveContainer
import love.forte.simbot.qguild.model.panel.CommandPanel
import love.forte.simbot.qguild.model.panel.CommandPanelRecord

/**
 * 指令面板记录信息。
 *
 * 它的内容信息是获取时基于 [love.forte.simbot.qguild.model.panel.CommandPanelRecord] 的瞬时**快照**。
 *
 * @since 4.7.0
 */
@SubclassOptInRequired(InternalForInheritanceQGPanelApi::class)
public abstract class QGCommandPanelRecord : QGObjectiveContainer<CommandPanelRecord>, QGCommandPanelHandle {
    /**
     * 面板生效场景的原始值。
     */
    public val scopeValue: String
        get() = source.scope

    /**
     * 面板生效范围的原始值。
     */
    public val targetTypeValue: String
        get() = source.targetType

    /**
     * 面板配置。
     */
    public val panel: CommandPanel
        get() = source.panel

    /**
     * 面板版本。
     */
    public val version: Int?
        get() = source.version
}
