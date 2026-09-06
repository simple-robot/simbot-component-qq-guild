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

package love.forte.simbot.qguild.utils

import kotlinx.coroutines.CancellationException
import love.forte.simbot.qguild.QGInternalApi

/**
 * 类似于[kotlin.runCatching]，但不会捕获 [CancellationException]。
 *
 * [CancellationException] 是协程的控制信号，而不是常规操作失败，它必须继续传播。
 *
 * @since 4.7.0
 */
@QGInternalApi
public inline fun <R> runCatchingCancellable(block: () -> R): Result<R> {
    return try {
        Result.success(block())
    } catch (e: CancellationException) {
        throw e
    } catch (e: Throwable) {
        Result.failure(e)
    }
}

/**
 * 类似于[kotlin.runCatching]，但不会捕获 [CancellationException]。
 *
 * [CancellationException] 是协程的控制信号，而不是常规操作失败，它必须继续传播。
 *
 * @since 4.7.0
 */
@QGInternalApi
public inline fun <T, R> T.runCatchingCancellable(block: T.() -> R): Result<R> {
    return try {
        Result.success(block())
    } catch (e: CancellationException) {
        throw e
    } catch (e: Throwable) {
        Result.failure(e)
    }
}
