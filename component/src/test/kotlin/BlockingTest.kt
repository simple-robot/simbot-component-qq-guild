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

import kotlinx.coroutines.*
import kotlinx.coroutines.future.asCompletableFuture

suspend fun d(): Int {
    println("d1: " + Thread.currentThread().name)
    delay(5000)
    println("d2: " + Thread.currentThread().name)
    return 1
}

fun main() {
    val future = runBlocking {
        println("rb1: " + Thread.currentThread().name)
        val scope = CoroutineScope(Dispatchers.Default)
        val d = scope.async {
            println("a1: " + Thread.currentThread().name)
            d().also {
                println("a2: " + Thread.currentThread().name)
            }
        }
        val d2 = scope.async {
            delay(500)
            throw IllegalArgumentException()
        }
        println("rb2: " + Thread.currentThread().name)
        d.asCompletableFuture().also {
            d2.await()
        }
    }

    println("after future.")

    println("value: ${future.join()}")

}