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