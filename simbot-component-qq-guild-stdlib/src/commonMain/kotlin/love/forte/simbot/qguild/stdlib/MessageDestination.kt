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

package love.forte.simbot.qguild.stdlib

import kotlinx.serialization.Serializable

/**
 * QQ 机器人在发送消息时的目的地枚举。
 *
 * 此类型通常用来作为配置类中使用的内容。
 *
 * @since 4.4.0
 */
@Serializable
public enum class MessageDestination {
    /**
     * QQ频道的某个(文字)子频道
     */
    CHANNEL,

    /**
     * QQ频道的某个私信会话
     */
    DMS,

    /**
     * QQ群
     */
    GROUP,

    /**
     * 单聊（QQ用户私信会话）
     */
    USER


}