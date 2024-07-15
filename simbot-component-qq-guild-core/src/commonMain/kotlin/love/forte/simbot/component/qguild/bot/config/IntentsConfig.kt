/*
 * Copyright (c) 2023-2024. ForteScarlet.
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

package love.forte.simbot.component.qguild.bot.config

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.qguild.event.EventIntents
import love.forte.simbot.qguild.event.EventIntentsAggregation
import love.forte.simbot.qguild.event.Intents

/**
 * 用于配置bot需要订阅的事件类型
 *
 */
@Serializable
@UsedOnlyForConfigSerialization
public sealed class IntentsConfig {
    /**
     * 配置结果
     */
    public abstract val intents: Intents

    /**
     * 直接使用原始的标记位最终数值。
     *
     * ```json
     * {
     *   "type": "raw",
     *   "intents": 1073741824
     * }
     * ```
     *
     * > 注: `1073741824` 即 `1 << 30`, 也就是 [EventIntents.PublicGuildMessages] 的标记位值。
     *
     */
    @Serializable
    @SerialName("raw")
    @UsedOnlyForConfigSerialization
    public data class Raw(override val intents: Intents) : IntentsConfig()

    /**
     * 通过名称寻找所有可用的 [EventIntents] 并合并为最终的 [Intents].
     *
     * ```json
     * {
     *   "type": "nameBased",
     *   "names": ["Guilds", "PublicGuildMessages"]
     * }
     * ```
     * @property names 继承了 [EventIntents] 的 `object` 的简单类名，例如 `Guilds`.
     * @throws NoSuchElementException 如果名称没有找到
     */
    @Serializable
    @UsedOnlyForConfigSerialization
    @SerialName("nameBased")
    public data class Names(val names: Set<String>) : IntentsConfig() {

        override val intents: Intents
            get() {
                if (names.isEmpty()) {
                    return Intents.ZERO
                }

                return names.map(EventIntentsAggregation::getByName)
                    .fold(Intents.ZERO, Intents::plus)
            }
    }

}
