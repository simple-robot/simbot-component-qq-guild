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
 *
 * @author ForteScarlet
 */
public expect class SimpleConcurrentQueue<T> {

    /**
     * 向此队列添加一个元素。
     */
    public fun add(element: T): Boolean

    /**
     * 移除指定目标。
     */
    public fun remove(element: T): Boolean
}


public expect fun <T> createSimpleConcurrentQueue(): SimpleConcurrentQueue<T>


public expect inline fun <T> SimpleConcurrentQueue<T>.forEach(block: (T) -> Unit)
