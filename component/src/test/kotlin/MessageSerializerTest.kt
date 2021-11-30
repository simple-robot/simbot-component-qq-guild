import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.plus
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import love.forte.simbot.ID
import love.forte.simbot.component.tencentguild.message.Ark
import love.forte.simbot.component.tencentguild.message.MentionChannel
import love.forte.simbot.message.*
import love.forte.simbot.tencentguild.buildArk
import kotlin.test.Test

/**
 *
 * @author ForteScarlet
 */
class MessageSerializerTest {

    val json get() = Json {
        isLenient = true
        ignoreUnknownKeys = true
        serializersModule += Messages.serializersModule + SerializersModule {
            polymorphic(Message.Element::class) {
                subclass(MentionChannel.serializer())
                subclass(Ark.serializer())
            }
        }
    }

    @Test
    fun test1() {
        val messages = At(1.ID) + Text { "Forte" } + MentionChannel(555.ID) + Ark(
            buildArk(114514.ID) {
                kvs {
                    kv("Key", "value-145")
                    kv("KK2") {
                        obj()
                        obj {
                            kv("O-K", "O-V-2")
                            kv("F-F", "O-V-3")
                        }
                    }
                }
            }
        )

        println(messages)

        val jsonStr = json.encodeToString(Messages.serializer, messages)

        println(jsonStr)

        val m2 = json.decodeFromString(Messages.serializer, jsonStr)

        println(m2)

    }

}