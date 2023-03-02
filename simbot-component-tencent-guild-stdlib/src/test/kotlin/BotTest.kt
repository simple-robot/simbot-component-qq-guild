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

import kotlinx.coroutines.asCoroutineDispatcher
import love.forte.simbot.qguild.tencentGuildBot
import love.forte.simbot.utils.runInNoScopeBlocking
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger
import kotlin.concurrent.thread

/**
 *
 * @author ForteScarlet
 */
class BotTest {


    fun test() = runInNoScopeBlocking {

        // 直接构建一个BOT
        val bot = tencentGuildBot(
            "appid",
            appKey = "key",
            token = "token",
        ) {
            // 总分片数量
            totalShard = 4
            val threadN = AtomicInteger(0)
            coroutineContext += Executors.newFixedThreadPool(4) {
                thread(start = false, isDaemon = true, name = "Test-thread-${threadN.getAndIncrement()}") { it.run() }
            }.asCoroutineDispatcher()

        }

        bot.start()
        bot.join()

    }

}
