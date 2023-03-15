/*
 * Copyright (c) 2022-2023. ForteScarlet.
 *
 * This file is part of simbot-component-qq-guild.
 *
 * simbot-component-qq-guild is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * simbot-component-qq-guild is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with simbot-component-qq-guild. If not, see <https://www.gnu.org/licenses/>.
 */

package love.forte.simbot.qguild

import kotlinx.coroutines.DisposableHandle
import love.forte.simbot.qguild.event.Signal


public inline fun <reified E : Signal.Dispatch> Bot.registerProcessor(
    crossinline block: suspend E.(raw: String) -> Unit
): DisposableHandle {
    return registerProcessor { raw ->
        if (this is E) {
            block(raw)
        }
    }
}
