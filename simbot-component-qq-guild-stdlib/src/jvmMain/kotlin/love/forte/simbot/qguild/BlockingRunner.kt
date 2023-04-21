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

package love.forte.simbot.qguild

import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.startCoroutine

/*
    在simbot的 suspend-transformers 独立之前，此类临时使用。
    如果当前环境中存在 love.forte.simbot.utils.BlockingRunnerKt
    则 runInNoScopeBlocking 直接使用其策略，否则降级为一个类似的简化实现。
 */

private val isSimbotBlockingRunnerAvailable: Boolean by lazy {
    runCatching {
        Class.forName("love.forte.simbot.utils.BlockingRunnerKt")
        true
    }.getOrElse { false }
}

private val runInNoScopeBlockingStrategy: RunInNoScopeBlockingStrategy by lazy {
    if (isSimbotBlockingRunnerAvailable) Simbot else Default
}

internal fun <T> runInNoScopeBlocking(block: suspend () -> T): T = runInNoScopeBlockingStrategy(block)

private interface RunInNoScopeBlockingStrategy {
    operator fun <T> invoke(block: suspend () -> T): T
}

private object Default : RunInNoScopeBlockingStrategy {
    override fun <T> invoke(block: suspend () -> T): T {
        val runner = SuspendRunner<T>()
        block.startCoroutine(runner)
        return runner.await().getOrThrow()
    }
}

private object Simbot : RunInNoScopeBlockingStrategy {
    override fun <T> invoke(block: suspend () -> T): T = love.forte.simbot.utils.runInNoScopeBlocking(block = block)
}

// Yes. I am the BlockingRunner.
@Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN")
private class SuspendRunner<T> : Continuation<T> {
    override val context: CoroutineContext get() = EmptyCoroutineContext
    private var result: Result<T>? = null

    override fun resumeWith(result: Result<T>) {
        synchronized(this) {
            this.result = result
            (this as Object).notifyAll()
        }
    }

    fun await(): Result<T> {
        synchronized(this) {
            while (true) {
                when (val result = this.result) {
                    null -> {
                        (this as Object).wait()
                    }

                    else -> {
                        return result
                    }
                }
            }
        }
    }
}
