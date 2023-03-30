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

package love.forte.simbot.qguild.internal

/**
 * 弱引用类型，在JS平台下并非真正的弱引用。
 * 内部将会持有其引用并直到使用 [clear] 将其清除。
 *
 */
public actual class WeakRef<T : Any> actual constructor(referred: T) {
    private var actualValue: T? = referred

    /**
     * 获取内部持有的引用对象。
     *
     * 直到调用 [clear] 才会得到null。
     */
    public actual fun get(): T? = actualValue

    /**
     * 清除内部对象。
     */
    public actual fun clear() {
        actualValue = null
    }

}

