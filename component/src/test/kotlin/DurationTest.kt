import kotlin.time.Duration.Companion.nanoseconds
import kotlin.time.Duration.Companion.seconds

fun main() {

    val t = 5.seconds
    val nanos = t.inWholeNanoseconds.nanoseconds

    println(t)
    println(nanos)
}