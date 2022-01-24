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

package test

import kotlinx.coroutines.*
import kotlin.test.Test

/**
 *
 * @author ForteScarlet
 */
class JobTest {

    @Test
    fun test(): Unit = runBlocking {
        val pJob = Job()



        val j1 = Job(pJob)
        val j2 = Job(pJob)

        val s1 = CoroutineScope(j1)
        val s2 = CoroutineScope(j2)

        for (child in pJob.children) {
            println(child)
        }

        repeat(5) {
            s1.launch {
                while (true) {
                    delay(5000)
                    println("s1===")
                }
            }
        }
        repeat(5) {
            s2.launch {
                while (true) {
                    delay(5000)
                    println("s2===")
                }
            }
        }

        println("=====")

        for (child in pJob.children) {
            println(child)
        }

        pJob.cancelAndJoin()
        println("canceled")




    }


}