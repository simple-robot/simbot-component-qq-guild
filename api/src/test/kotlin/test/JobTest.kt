/*
 *  Copyright (c) 2022-2022 ForteScarlet <ForteScarlet@163.com>
 *
 *  本文件是 simbot-component-tencent-guild 的一部分。
 *
 *  simbot-component-tencent-guild 是自由软件：你可以再分发之和/或依照由自由软件基金会发布的 GNU 通用公共许可证修改之，无论是版本 3 许可证，还是（按你的决定）任何以后版都可以。
 *
 *  发布 simbot-component-tencent-guild 是希望它能有用，但是并无保障;甚至连可销售和符合某个特定的目的都不保证。请参看 GNU 通用公共许可证，了解详情。
 *
 *  你应该随程序获得一份 GNU 通用公共许可证的复本。如果没有，请看:
 *  https://www.gnu.org/licenses
 *  https://www.gnu.org/licenses/gpl-3.0-standalone.html
 *  https://www.gnu.org/licenses/lgpl-3.0-standalone.html
 *
 *
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