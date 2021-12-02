import love.forte.simbot.component.tencentguild.event.TcgChannelAtMessageEvent
import love.forte.simbot.event.ChannelMessageEvent
import love.forte.simbot.event.isSubFrom
import kotlin.test.Test

/**
 *
 * @author ForteScarlet
 */
class TargetTest {

    @Test
    fun test() {
        val channelType = ChannelMessageEvent.Key
        val sub = TcgChannelAtMessageEvent.Key

        println(sub.isSubFrom(channelType))

    }
}