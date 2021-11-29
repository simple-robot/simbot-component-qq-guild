import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.newFixedThreadPoolContext
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

    fun test0() = runBlocking {

    }

    @OptIn(ObsoleteCoroutinesApi::class)
    fun test() = runBlocking {
        val bot = tencentBot(
            "appid",
            appKey = "key",
            token = "token",
        ) {
            // 总分片数量
            totalShared = 4
            val threadN = AtomicInteger(0)
            coroutineContext += Executors.newFixedThreadPool(4) {
                thread(start = false, isDaemon = true, name = "Test-thread-${threadN.getAndIncrement()}") { it.run() }
            }.asCoroutineDispatcher()



        }

        bot.processor { json ->
        }


    }

}