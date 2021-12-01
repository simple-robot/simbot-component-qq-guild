import com.charleskorn.kaml.Yaml
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

    val module = Messages.serializersModule + SerializersModule {
        polymorphic(Message.Element::class) {
            subclass(MentionChannel.serializer())
            subclass(Ark.serializer())
        }
    }

    val json get() = Json {
        isLenient = true
        ignoreUnknownKeys = true
        serializersModule += module
    }

    val yaml = Yaml(module)

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

    @Test
    fun test1() {
        println(messages)
        val jsonStr = json.encodeToString(Messages.serializer, messages)
        println(jsonStr)
        val m2 = json.decodeFromString(Messages.serializer, jsonStr)
        println(m2)
        m2.forEach { m -> println(m) }
        println()
        println()
        println()
    }


    @Test
    fun test2() {
        val yamlStr = yaml.encodeToString(Messages.serializer, messages)
        println(yamlStr)
        val m2 = yaml.decodeFromString(Messages.serializer, yamlStr)
        println(m2)
        m2.forEach { m -> println(m) }
        println()
        println()
        println()

    }

}