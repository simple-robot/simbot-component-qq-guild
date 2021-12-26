import com.charleskorn.kaml.Yaml
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.properties.Properties


@OptIn(ExperimentalSerializationApi::class)
fun main() {
    val yaml = Yaml(
        serializersModule = EmptySerializersModule,
    )
    val properties = Properties(EmptySerializersModule)
    val json = Json {
        isLenient = true
    }

    val loader = Thread.currentThread().contextClassLoader
    val resourcesYaml = loader.getResource("TestConfig.yaml")!!
    val resourcesProp = loader.getResource("TestConfig.properties")!!
    val resourcesJson = loader.getResource("TestConfig.json")!!

    val ser = DecodeTest.serializer()

    println(json.decodeFromStream(ser, resourcesJson.openStream()))
    println(yaml.decodeFromStream(ser, resourcesYaml.openStream()))

    @Suppress("SimplifiableCallChain")
    val prop = java.util.Properties().also {
        it.load(resourcesProp.openStream())
    }.map { it.key.toString() to it.value.toString() }.toMap()

    println(properties.decodeFromStringMap(ser, prop))


}

@Serializable
data class DecodeTest(
    val names: List<String>
)