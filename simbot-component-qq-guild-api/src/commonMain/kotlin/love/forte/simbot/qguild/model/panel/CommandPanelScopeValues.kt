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

package love.forte.simbot.qguild.model.panel

/**
 * 与指令面板生效范围相关的已知可选值的常量类。
 * @since 4.7.0
 */
public object CommandPanelScopeValues {
    /**
     * C2C 单聊场景。
     */
    public const val C2C: String = "c2c"

    /**
     * 群聊场景。
     */
    public const val GROUP: String = "group"

    /**
     * 文字子频道场景。
     */
    public const val CHANNEL: String = "channel"

    /**
     * 频道私信场景。
     */
    public const val DM: String = "dm"
}
