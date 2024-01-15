/*
 * Copyright (c) 2024. ForteScarlet.
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

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import love.forte.simbot.component.qguild.useQQGuild
import love.forte.simbot.core.application.launchSimpleApplication
import kotlin.time.measureTimedValue

/*
 * Copyright (c) 2024. ForteScarlet.
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

actual fun launchApp(vararg args: String) {
    println(args.joinToString(", "))

    runBlocking {
        val (app, time) = measureTimedValue {
            launchSimpleApplication {
                config {
                    coroutineContext = Dispatchers.Default
                }

                useQQGuild()
            }.apply {
                onApp(args[0], args[1])
            }
        }

        println("Launch Time: $time")

        app.join()
    }
}
