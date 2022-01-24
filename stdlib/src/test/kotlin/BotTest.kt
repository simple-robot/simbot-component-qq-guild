/*
 *  Copyright (c) 2022-2022 ForteScarlet <ForteScarlet@163.com>
 *
 *  根据 GNU LESSER GENERAL PUBLIC LICENSE 3 获得许可；
 *  除非遵守许可，否则您不得使用此文件。
 *  您可以在以下网址获取许可证副本：
 *
 *       https://www.gnu.org/licenses/lgpl-3.0-standalone.html
 *
 *   有关许可证下的权限和限制的具体语言，请参见许可证。
 */

import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.runBlocking
import love.forte.simbot.tencentguild.tencentBot
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger
import kotlin.concurrent.thread

/**
 *
 * @author ForteScarlet
 */
class BotTest {


    fun test() = runBlocking {

        // 直接构建一个BOT
        val bot = tencentBot(
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