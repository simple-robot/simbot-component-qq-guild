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

import kotlinx.atomicfu.locks.SynchronizedObject
import kotlinx.atomicfu.locks.synchronized

/**
 * native平台下的 [SimpleConcurrentQueue] 实现。
 *
 * _注意：此实现尚处于试验阶段。_
 *
 */
public actual class SimpleConcurrentQueue<T>(initialCapacity: Int) : SynchronizedObject() {

    private val list = ArrayList<T>(initialCapacity)

    /**
     * 向此队列添加一个元素。
     */
    public actual fun add(element: T): Boolean {
        return synchronized(this) { list.add(element) }
    }

    /**
     * 移除指定目标。
     */
    public actual fun remove(element: T): Boolean {
        return synchronized(this) { list.remove(element) }
    }

    /**
     * 得到当前队列的迭代器。
     */
    public val iterator: MutableIterator<T> get() = list.iterator()
}

/**
 * 创建一个 [SimpleConcurrentQueue]。
 */
public actual fun <T> createSimpleConcurrentQueue(): SimpleConcurrentQueue<T> = SimpleConcurrentQueue(10)

/**
 * foreach [SimpleConcurrentQueue] values.
 */
public actual inline fun <T> SimpleConcurrentQueue<T>.forEach(block: (T) -> Unit) {
    iterator.forEach(block)
}
