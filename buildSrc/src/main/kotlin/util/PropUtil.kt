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

package util

import org.gradle.api.Project
import org.gradle.kotlin.dsl.extra

/**
 * 尝试从 [System.getProperty] 和 [System.getenv] 中获取指定属性。
 * 优先使用 [System.getProperty]。
 */
fun systemProp(propKey: String, envKey: String = propKey): String? =
    System.getProperty(propKey) ?: System.getenv(envKey)


fun Project.getProp(key: String): Any? = if (extra.has(key)) extra.get(key) else null
