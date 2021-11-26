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