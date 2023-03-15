/*
 * Copyright (c) 2023. ForteScarlet.
 *
 * This file is part of simbot-component-qq-guild.
 *
 * simbot-component-qq-guild is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * simbot-component-qq-guild is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with simbot-component-qq-guild. If not, see <https://www.gnu.org/licenses/>.
 */

package love.forte.simbot.component.qguild.util

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.DisposableHandle
import kotlinx.coroutines.Job


/**
 * 为当前bot指定一个 `Root Job`。
 *
 * `Root Job` 不会像 `Parent Job` 那样与当前Job硬性关联，
 * 其唯一的作用就是当 [rootJob] 完成的时候通知并关闭当前Job，
 * 这是通过 [Job.invokeOnCompletion] 实现的。
 *
 * 当前job既不会出现在 [rootJob] 的 [children][Job.children]
 * 中，也不会因为异常等情况导致 `Root Job` 被关闭。
 *
 * 可以通过返回的 [DisposableHandle] 来取消这种关联。
 *
 */
public fun Job.registerRootJob(rootJob: Job): DisposableHandle {
    val subJob = this
    return rootJob.invokeOnCompletion { e ->
        when (e) {
            null -> subJob.cancel()
            is CancellationException -> subJob.cancel(e)
            else -> subJob.cancel(CancellationException(e.localizedMessage, e))
        }
    }
}
