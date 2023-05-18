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

package love.forte.simbot.component.qguild.internal.utils

import love.forte.simbot.Timestamp
import kotlin.reflect.KProperty

/**
 * 使用一个毫秒时间戳代理为 [Timestamp]. 可用于减少 [Timestamp] 对象的快速急迫产生，
 * 但是会使其每次获取都得到新的实例。
 *
 */
@Suppress("NOTHING_TO_INLINE")
internal inline operator fun Long.getValue(thisRef: Any?, property: KProperty<*>): Timestamp =
    Timestamp.byMillisecond(this)

/**
 * 当前系统毫秒时间戳
 */
internal inline val nowTimeMillis: Long get() = System.currentTimeMillis()
