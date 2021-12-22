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