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

import io.ktor.http.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.properties.Properties
import love.forte.simbot.BotManager
import love.forte.simbot.BotVerifyInfo
import love.forte.simbot.component.tencentguild.internal.TencentGuildBotManagerImpl
import love.forte.simbot.event.EventProcessor
import love.forte.simbot.tencentguild.EventSignals
import love.forte.simbot.tencentguild.Intents
import love.forte.simbot.tencentguild.TencentBotConfiguration
import love.forte.simbot.tencentguild.TencentGuildApi

private inline fun <reified K, reified V> Map<K, V>.find(vararg keys: K, nullMessage: () -> String): V {
    for (k in keys) {
        val v = this[k]
        if (v != null) return v
    }
    throw NullPointerException(nullMessage())
}


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
        val appId = verifyInfo.find("appId", "app_id", "appid", "id") {
            "Required property 'app_id'"
        }
        val appKey = verifyInfo.find("appKey", "app_key", "app_secret", "appSecret") {
            "Required property 'app_key'"
        }
        val token = verifyInfo["token"]
            ?: throw NullPointerException("Required property 'token'")

        val configuration = Prop.decodeFromStringMap(PropertiesConfiguration.serializer(), verifyInfo)

        // no config
        return register(appId, appKey, token, configuration::includeConfig)
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

@OptIn(ExperimentalSerializationApi::class)
private val Prop = Properties(SerializersModule { })

/**
 * 通过 `Map<String, Object>` 进行反序列化的配置类，
 * 通过由配置文件读取而来的信息来对指定Bot进行信息配置。
 */
@Suppress("MemberVisibilityCanBePrivate")
@Serializable
internal class PropertiesConfiguration(
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
    val defaultIntents: Intents = EventSignals.allIntents,

    /**
     * 服务器路径地址。
     * @see TencentGuildApi.URL_STRING
     */
    val serverUrl: String? = null

) {


    fun includeConfig(configuration: TencentBotConfiguration) {
        if (totalShard != null) {
            configuration.totalShard = totalShard
        }
        configuration.intentsForShardFactory = { shard -> intentValues[shard]?.let { Intents(it) } ?: defaultIntents }
        if (serverUrl != null) {
            configuration.serverUrl = Url(serverUrl)
        }
    }
}