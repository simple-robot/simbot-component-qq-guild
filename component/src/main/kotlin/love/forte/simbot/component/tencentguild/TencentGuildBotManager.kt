/*
 *  Copyright (c) 2021-2022 ForteScarlet <ForteScarlet@163.com>
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

package love.forte.simbot.component.tencentguild

import io.ktor.http.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.*
import love.forte.simbot.*
import love.forte.simbot.component.tencentguild.internal.TencentGuildBotManagerImpl
import love.forte.simbot.event.EventProcessor
import love.forte.simbot.tencentguild.EventSignals
import love.forte.simbot.tencentguild.Intents
import love.forte.simbot.tencentguild.TencentBotConfiguration
import love.forte.simbot.tencentguild.TencentGuildApi
import org.slf4j.Logger
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext


/**
 *
 * QQ频道BOT的bot管理器。
 *
 * [TencentGuildBotManager] 不允许注册相同 `appId` 的bot。
 *
 * @author ForteScarlet
 */
public abstract class TencentGuildBotManager : BotManager<TencentGuildBot>() {

    protected abstract val logger: Logger

    /**
     * 注册一个Bot的信息，并使用默认配置。
     */
    @OptIn(ExperimentalSerializationApi::class)
    override fun register(verifyInfo: BotVerifyInfo): TencentGuildBot {
        val serializer = TencentBotViaBotFileConfiguration.serializer()

        val jsonElement = verifyInfo.inputStream().use { inp -> CJson.decodeFromStream(JsonElement.serializer(), inp) }
        val component = jsonElement.jsonObject["component"]?.jsonPrimitive?.content
            ?: throw NoSuchComponentException("Component is not found in [${verifyInfo.infoName}]")

        logger.debug("[{}] json element load: {}", verifyInfo.infoName, jsonElement)

        if (component != ComponentTencentGuild.COMPONENT_ID.toString()) {
            logger.debug(
                "[{}] mismatch: [{}] != [{}]",
                verifyInfo.infoName,
                component,
                ComponentTencentGuild.COMPONENT_ID
            )
            throw ComponentMismatchException("[$component] != [${ComponentTencentGuild.COMPONENT_ID}]")
        }
        val configuration = CJson.decodeFromJsonElement(serializer, jsonElement)

        // no config
        return register(configuration.appId, configuration.appKey, configuration.token, configuration::includeConfig)
    }

    public abstract fun register(
        appId: String,
        appKey: String,
        token: String,
        block: TencentBotConfiguration.() -> Unit = {}
    ): TencentGuildBot


    public abstract val configuration: TencentGuildBotManagerConfiguration

    public companion object {
        @JvmStatic
        public fun newInstance(configuration: TencentGuildBotManagerConfiguration): TencentGuildBotManager {
            return TencentGuildBotManagerImpl(configuration)
        }
    }
}

@DslMarker
@Retention(AnnotationRetention.BINARY)
internal annotation class TencentGuildBMDsl


/**
 * 得到一个BotManager.
 */
@TencentGuildBMDsl
public fun tencentGuildBotManager(
    processor: EventProcessor,
    block: TencentGuildBotManagerConfiguration.() -> Unit = { }
): TencentGuildBotManager {
    return TencentGuildBotManager.newInstance(TencentGuildBotManagerConfiguration(processor).also(block))
}


/**
 * [TencentGuildBotManager] 使用的配置类。
 *
 * @param eventProcessor 当前bot所使用的事件处理器
 */
@Suppress("MemberVisibilityCanBePrivate")
public class TencentGuildBotManagerConfiguration(public var eventProcessor: EventProcessor) {

    /**
     * 从此处对所有bot的配置信息进行统一处理。
     */
    public var botConfigure: TencentBotConfiguration.(appId: String, appKey: String, token: String) -> Unit =
        { _, _, _ -> }

    /**
     * 如果其中存在 [kotlinx.coroutines.Job], 则会被作为parentJob使用。
     */
    public var parentCoroutineContext: CoroutineContext = EmptyCoroutineContext

}

// 只有在注册时候会使用到, 不保留为属性。

//@OptIn(ExperimentalSerializationApi::class)
// private val CProp get() = Properties(SerializersModule { })
// private val CYaml get() = CYamlFunction?.invoke()
// private val CYamlFunction: (() -> com.charleskorn.kaml.Yaml)? by lazy {
//     try {
//         Class.forName("com.charleskorn.kaml.Yaml")
//         return@lazy {
//             com.charleskorn.kaml.Yaml(
//                 configuration = YamlConfiguration(
//                     strictMode = false
//                 )
//             )
//         }
//     } catch (e: ClassNotFoundException) {
//         LoggerFactory.getLogger(TencentGuildBotManager::class)
//             .error("[com.charleskorn.kaml.Yaml] not in your classpath. If you want to support for yaml, add 'com.charleskorn.kaml:kaml:\$version' into your classpath.")
//         return@lazy null
//     }
// }
private val CJson
    get() = Json {
        isLenient = true
        ignoreUnknownKeys = true
    }

/**
 * 通过 `Map<String, Object>` 进行反序列化的配置类，
 * 通过由配置文件读取而来的信息来对指定Bot进行信息配置。
 */
@Suppress("MemberVisibilityCanBePrivate")
@Serializable
internal class TencentBotViaBotFileConfiguration(
    val appId: String,

    val appKey: String,

    val token: String,


    /**
     * 分片总数。
     * @see [TencentBotConfiguration.totalShard]
     */
    val totalShard: Int? = null,

    /**
     * 分片策略。key为分片值，
     * value为对应分片下所需的 intent.
     *
     */
    val intentValues: Map<Int, Int> = emptyMap(),

    /**
     * 默认的 [Intents]. 如果对应分片下 [intentValues] 无法找到指定的 intent, 则使用此默认值。
     */
    val defaultIntents: List<String> = EventSignals.intents.keys.toList(),

    /**
     * 服务器路径地址。
     * @see TencentGuildApi.URL_STRING
     */
    val serverUrl: String? = null

) {

    internal val defaultIntentsValue: Intents
        get() {
            return defaultIntents.distinct().map { v ->
                EventSignals.intents[v]
                    ?: kotlin.runCatching {
                        Intents(v.toInt())
                    }.getOrElse {
                        throw SimbotIllegalArgumentException(
                            """Cannot resolve '$v' to intent value.
                    |You can use a intent value, or use name value in: ${EventSignals.intents.keys}
                """.trimMargin(), it
                        )
                    }
            }.reduce { acc, intents -> acc + intents }
        }


    fun includeConfig(configuration: TencentBotConfiguration) {
        if (totalShard != null) {
            configuration.totalShard = totalShard
        }
        configuration.intentsForShardFactory =
            { shard -> intentValues[shard]?.let { Intents(it) } ?: defaultIntentsValue }
        if (serverUrl != null) {
            configuration.serverUrl = Url(serverUrl)
        }
    }
}