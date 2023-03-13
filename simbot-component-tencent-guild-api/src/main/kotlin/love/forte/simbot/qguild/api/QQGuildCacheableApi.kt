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

package love.forte.simbot.qguild.api


/**
 *
 * 支持结果缓存的 [QQGuildApi] 实现，
 * 会通过一定的策略对目标 [QQGuildApi] 的结果进行缓存，并在 [doRequest] 中进行获取并跳过请求环节。
 *
 *
 * @author ForteScarlet
 */
public sealed class QQGuildCacheableApi<R> : QQGuildApi<R>() {

    /**
     * 内部存在的缓存结果。如果为null则代表缓存不存在或已失效。
     */
    public abstract val cachedValue: R?

    /**
     * 缓存模式。
     */
    public sealed class CacheType {

        /**
         * TODO
         */
        public class Lazy(public val lazyThreadSafetyMode: LazyThreadSafetyMode = LazyThreadSafetyMode.SYNCHRONIZED)


    }

}


// Type
// Kind
// Mode
