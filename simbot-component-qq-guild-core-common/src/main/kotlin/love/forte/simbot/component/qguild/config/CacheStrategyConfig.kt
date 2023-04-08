/*
 * Copyright (c) 2023. ForteScarlet.
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

package love.forte.simbot.component.qguild.config

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


/**
 * 缓存策略配置。
 *
 * @author ForteScarlet
 */
@Serializable
public sealed class CacheStrategyConfig {

    /**
     * '无效'的缓存策略，即不启用缓存。
     *
     */
    @SerialName("invalid")
    public object Invalid : CacheStrategyConfig()

    @SerialName("transferability")
    public object Transferability : CacheStrategyConfig()

}

// TODO?
@Serializable
public data class CacheConfig(
    /**
     * '传递性' 缓存。
     *
     * 默认启用。
     *
     * ```json
     * "config": {
     *   "cache": {
     *     "transmit": {
     *       "enable": true
     *     }
     *   }
     * }
     * ```
     *
     * 更多说明参考 [TransmitCacheConfig]
     *
     * @see TransmitCacheConfig
     */
    @SerialName("transmit")
    val transmitCacheConfig: TransmitCacheConfig = TransmitCacheConfig(),

    /**
     * 动态缓存配置。
     *
     */
    @SerialName("dynamic")
    val dynamicCacheConfig: DynamicCacheConfig = DynamicCacheConfig(false),
) {

}


/**
 * '传递性' 缓存。
 *
 *  举个例子：
 * ```kotlin
 * bot.guild().channels.collect {           // 1
 *     val category = it.category.resolve() // 2
 *     val guild = category.guild()         // 3
 * }
 * ```
 *
 * 如上，步骤为：
 *
 * 1. 在一个 `guild` 中获取所有的 `channel`
 * 2. 然后再获取每一个 `channel` 的分组详情
 * 3. 然后再获取这个子频道的所属 `guild`
 *
 * 在这个过程中，步骤 `3` 中获取的 `guild` 已经确定为步骤 `1` 中的 `guild`，
 * 因此如果不是为了 `guild` 信息的绝对 _"实时"_ ，它们的查询实际上并没有必要。
 *
 * 传递性缓存即用于处理此种情况，当一个存在从属关系的子信息可以使用其所属的来源信息时，则直接将其传递而非再次查询。
 *
 * 传递性缓存不会应用于任何序列/列表，只会生效于有从属关系的单属性API中。
 *
 */
@Serializable
public data class TransmitCacheConfig(val enable: Boolean = true)


/**
 * 内部动态缓存策略。
 *
 * 当一个bot能够监听 `member`、`channel` 的变动事件时，
 * 那么他就可以针对某个 `guild` 下的 `member` 和 `channel` 进行实时的内部缓存来避免频繁的API调用，
 * 提高整体程序的响应速度和网络（请求）压力。
 *
 * TODO
 */
@Serializable
public data class DynamicCacheConfig(val enable: Boolean = true, )
