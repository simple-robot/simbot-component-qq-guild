/*
 *  Copyright (c) 2021 ForteScarlet <https://github.com/ForteScarlet>
 *
 *  根据 Apache License 2.0 获得许可；
 *  除非遵守许可，否则您不得使用此文件。
 *  您可以在以下网址获取许可证副本：
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *   有关许可证下的权限和限制的具体语言，请参见许可证。
 */

package love.forte.simbot.component.tencentguild

import com.charleskorn.kaml.YamlConfiguration
import io.ktor.http.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.properties.Properties
import love.forte.simbot.*
import love.forte.simbot.action.NotSupportActionException
import love.forte.simbot.component.tencentguild.internal.TencentGuildBotManagerImpl
import love.forte.simbot.event.EventProcessor
import love.forte.simbot.tencentguild.EventSignals
import love.forte.simbot.tencentguild.Intents
import love.forte.simbot.tencentguild.TencentBotConfiguration
import love.forte.simbot.tencentguild.TencentGuildApi


/**
 *
 * QQ频道BOT的bot管理器。
 *
 * [TencentGuildBotManager] 不允许注册相同 `appId` 的bot。
 *
 * @author ForteScarlet
 */
public abstract class TencentGuildBotManager : BotManager<TencentGuildBot>() {

    /**
     * 注册一个Bot的信息，并使用默认配置。
     */
    @OptIn(ExperimentalSerializationApi::class)
    override fun register(verifyInfo: BotVerifyInfo): TencentGuildBot {
        val serializer = TencentBotViaBotFileConfiguration.serializer()

        val configuration = verifyInfo.tryResolveVerifyInfo(
            ::VerifyFailureException,
            {
                CJson.decodeFromStream(serializer, it)
            },
            CYaml?.let { yaml ->
                (
                        {
                            yaml.decodeFromStream(serializer, it)
                        }
                )
            } ?: { throw NotSupportActionException("com.charleskorn.kaml.Yaml not in classpath") },
            {
                val p = java.util.Properties().also { p -> p.load(it) }
                val stringMap = mutableMapOf<String, String>()
                for (key in p.stringPropertyNames()) {
                    stringMap[key] = p.getProperty(key)
                }
                // 鉴于 kotlinx properties 的限制，需要对 defaultIntents 属性进行特殊处理
                val defaultIntents = stringMap.remove("defaultIntents")
                if (defaultIntents != null) {
                    val defaultIntentsSplit = defaultIntents.split(Regex(" *, *"))
                    defaultIntentsSplit.forEachIndexed { index, intent ->
                        stringMap["defaultIntents.$index"] = intent
                    }
                }

                CProp.decodeFromStringMap(serializer, stringMap)
            },
            ).getOrThrow()

        if (configuration.component == null) {
            throw NoSuchComponentException("Component is not found in [${verifyInfo.infoName}]")
        }

        if (configuration.component != ComponentTencentGuild.COMPONENT_ID.toString()) {
            throw ComponentMismatchException("[${configuration.component}] != [${ComponentTencentGuild.COMPONENT_ID}]")
        }


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

}

// 只有在注册时候会使用到, 不保留为属性。

@OptIn(ExperimentalSerializationApi::class)
private val CProp get() = Properties(SerializersModule { })
private val CYaml get() = CYamlFunction?.invoke()
private val CYamlFunction: (() -> com.charleskorn.kaml.Yaml)? by lazy {
    try {
        return@lazy {
            com.charleskorn.kaml.Yaml(
                configuration = YamlConfiguration(
                    strictMode = false
                )
            )
        }
    } catch (e: NoClassDefFoundError) {
        LoggerFactory.getLogger(TencentGuildBotManager::class)
            .error("[com.charleskorn.kaml.Yaml] not in your classpath. If you want to support for yaml, add 'com.charleskorn.kaml:kaml:\$version' into your classpath.")
        return@lazy null
    }
}
private val CJson get() = Json {
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
    val component: String? = null,

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

    internal val defaultIntentsValue: Intents get() {
        return defaultIntents.distinct().map { v ->
            EventSignals.intents[v]
                ?: kotlin.runCatching {
                    Intents(v.toInt())
                }.getOrElse { throw SimbotIllegalArgumentException("""Cannot resolve '$v' to intent value.
                    |You can use a intent value, or use name value in: ${EventSignals.intents.keys}
                """.trimMargin(), it) }
        }.reduce { acc, intents -> acc + intents }
    }


    fun includeConfig(configuration: TencentBotConfiguration) {
        if (totalShard != null) {
            configuration.totalShard = totalShard
        }
        configuration.intentsForShardFactory = { shard -> intentValues[shard]?.let { Intents(it) } ?: defaultIntentsValue }
        if (serverUrl != null) {
            configuration.serverUrl = Url(serverUrl)
        }
    }
}