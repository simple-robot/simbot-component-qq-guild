/*
 * Copyright (c) 2021-2023. ForteScarlet.
 *
 * This file is part of simbot-component-tencent-guild.
 *
 * simbot-component-tencent-guild is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * simbot-component-tencent-guild is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with simbot-component-tencent-guild. If not, see <https://www.gnu.org/licenses/>.
 */

package love.forte.simbot.component.tencentguild

import io.ktor.http.*
import kotlinx.serialization.Serializable
import love.forte.simbot.*
import love.forte.simbot.application.ApplicationConfiguration
import love.forte.simbot.application.EventProviderAutoRegistrarFactory
import love.forte.simbot.application.EventProviderFactory
import love.forte.simbot.bot.BotManager
import love.forte.simbot.bot.BotVerifyInfo
import love.forte.simbot.bot.ComponentMismatchException
import love.forte.simbot.component.tencentguild.internal.TencentGuildBotManagerImpl
import love.forte.simbot.event.EventProcessor
import love.forte.simbot.qguild.BotConfiguration
import love.forte.simbot.qguild.ConfigurableBotConfiguration
import love.forte.simbot.qguild.QGuildApi
import love.forte.simbot.qguild.event.Intents
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
public abstract class TencentGuildBotManager : BotManager<TencentGuildComponentBot>() {
    public abstract val eventProcessor: EventProcessor
    protected abstract val logger: Logger
    abstract override val component: TencentGuildComponent
    public abstract val configuration: TencentGuildBotManagerConfiguration
    
    
    /**
     * 注册一个Bot的信息，并使用默认配置。
     */
    override fun register(verifyInfo: BotVerifyInfo): TencentGuildComponentBot {
        val serializer = TencentBotViaBotFileConfiguration.serializer()
        
        val component = verifyInfo.componentId
        
        val currentComponent = this.component.id
        if (component != currentComponent) {
            logger.debug(
                "[{}] mismatch: [{}] != [{}]",
                verifyInfo.name,
                component,
                currentComponent
            )
            throw ComponentMismatchException("[$component] != [$currentComponent]")
        }
        val configuration = verifyInfo.decode(serializer)
        
        // no config
        return register(configuration.appId, configuration.appKey, configuration.token, configuration::includeConfig)
    }
    
    /**
     * 通过所需信息注册一个bot。
     */
    public abstract fun register(
        appId: String,
        appKey: String,
        token: String,
        block: ConfigurableBotConfiguration.() -> Unit = {},
    ): TencentGuildComponentBot
    
    /**
     * [TencentGuildBotManager] 的构建工厂。
     */
    public companion object Factory :
        EventProviderFactory<TencentGuildBotManager, TencentGuildBotManagerConfiguration> {
        override val key: Attribute<TencentGuildBotManager> = attribute("SIMBOT.TCG")
        
        override suspend fun create(
            eventProcessor: EventProcessor,
            components: List<Component>,
            applicationConfiguration: ApplicationConfiguration,
            configurator: TencentGuildBotManagerConfiguration.() -> Unit,
        ): TencentGuildBotManager {
            val configuration = TencentGuildBotManagerConfigurationImpl().also {
                it.parentCoroutineContext = applicationConfiguration.coroutineContext
                configurator(it)
            }
            
            // find component
            val component =
                components.find { it.id == TencentGuildComponent.ID_VALUE } as? TencentGuildComponent
                    ?: throw NoSuchComponentException("component id [${TencentGuildComponent.ID_VALUE}], and type of TencentGuildComponent.")
            
            return TencentGuildBotManagerImpl(eventProcessor, configuration, component).also {
                configuration.useBotManager(it)
            }
        }
        
        @JvmStatic
        @Deprecated("Use bot manager in Application.")
        public fun newInstance(
            processor: EventProcessor,
            configuration: TencentGuildBotManagerConfiguration,
        ): TencentGuildBotManager {
            return TencentGuildBotManagerImpl(processor, configuration, TencentGuildComponent())
        }
    }
}

@DslMarker
@Retention(AnnotationRetention.BINARY)
internal annotation class TencentGuildBotManagerConfigurationDsl


/**
 * 得到一个BotManager.
 */
@Suppress("DEPRECATION")
@Deprecated("Use bot manager in Application.")
public fun tencentGuildBotManager(
    processor: EventProcessor,
    block: TencentGuildBotManagerConfiguration.() -> Unit = { },
): TencentGuildBotManager {
    return TencentGuildBotManager.newInstance(processor, TencentGuildBotManagerConfigurationImpl().also(block))
}


/**
 * [TencentGuildBotManager] 使用的配置类描述。
 */
public interface TencentGuildBotManagerConfiguration {
    
    /**
     * 对所有bot的配置信息进行统一处理的函数。
     * 如果直接对 [botConfigure] 进行赋值，则会覆盖之前的配置。
     *
     * 如果想要以_追加_的形式进行配置，考虑使用同名的函数 [botConfigure].
     *
     * e.g.
     * ```kotlin
     * botConfigure = { appId, appKey, token -> /* ... */ }
     * ```
     */
    public var botConfigure: BotConfiguration.(appId: String, appKey: String, token: String) -> Unit
    
    /**
     * 对所有bot的配置信息进行统一处理的函数。
     * 使用当前函数会保留之前的配置。
     *
     * 如果想直接覆盖配置并抛弃之前的配置，考虑使用同名的属性 [botConfigure].
     *
     * e.g.
     * ```kotlin
     * botConfigure { appId, appKey, token -> /* ... */ }
     * ```
     */
    @TencentGuildBotManagerConfigurationDsl
    public fun botConfigure(configure: BotConfiguration.(appId: String, appKey: String, token: String) -> Unit)
    
    /**
     * 当前botManager使用的协程上下文。初始值为 [ApplicationConfiguration] 所提供的上下文。
     *
     */
    public var parentCoroutineContext: CoroutineContext
    
    
    /**
     * 提供 [appId]、[appKey]、[token] 和 [配置信息][botConfiguration] 来预注册一个bot。
     *
     * @param onBot 当bot被注册后的回调函数。
     */
    @TencentGuildBotManagerConfigurationDsl
    public fun register(
        appId: String, appKey: String, token: String,
        botConfiguration: BotConfiguration.() -> Unit = {},
        onBot: suspend (TencentGuildComponentBot) -> Unit,
    )
    
    
}


/**
 * [TencentGuildBotManager] 的自动注册工厂。
 */
public class TencentGuildBotManagerAutoRegistrarFactory :
    EventProviderAutoRegistrarFactory<TencentGuildBotManager, TencentGuildBotManagerConfiguration> {
    override val registrar: TencentGuildBotManager.Factory get() = TencentGuildBotManager
}


/**
 * [TencentGuildBotManager] 使用的配置类。
 */
@Suppress("MemberVisibilityCanBePrivate")
private class TencentGuildBotManagerConfigurationImpl : TencentGuildBotManagerConfiguration {
    override var parentCoroutineContext: CoroutineContext = EmptyCoroutineContext
    
    override var botConfigure: BotConfiguration.(appId: String, appKey: String, token: String) -> Unit =
        { _, _, _ -> }
    
    override fun botConfigure(configure: BotConfiguration.(appId: String, appKey: String, token: String) -> Unit) {
        botConfigure.also { old ->
            botConfigure = { appId, appKey, token ->
                old(appId, appKey, token)
                configure(appId, appKey, token)
            }
        }
    }
    
    
    private var botManagerConfig: suspend (TencentGuildBotManager) -> Unit = {}
    
    private fun addBotManagerConfig(block: suspend (TencentGuildBotManager) -> Unit) {
        botManagerConfig.also { old ->
            botManagerConfig = {
                old(it)
                block(it)
            }
        }
    }
    
    override fun register(
        appId: String,
        appKey: String,
        token: String,
        botConfiguration: BotConfiguration.() -> Unit,
        onBot: suspend (TencentGuildComponentBot) -> Unit,
    ) {
        addBotManagerConfig { manager ->
            manager.register(appId, appKey, token, botConfiguration)
        }
    }
    
    suspend fun useBotManager(botManager: TencentGuildBotManagerImpl) {
        botManagerConfig(botManager)
    }
    
}

/**
 * bot配置文件所对应的配置类，
 *
 * 通过由配置文件读取而来的信息来对指定Bot进行信息配置。
 *
 * _**Note: 仅用于配置文件反序列化使用**_
 */
@Suppress("MemberVisibilityCanBePrivate")
@Serializable
public data class TencentBotViaBotFileConfiguration(
    /**
     * app id.
     */
    val appId: String,

    /**
     * app key.
     */
    val appKey: String,

    /**
     * token.
     */
    val token: String,

    /**
     * 分片总数。
     * @see [BotConfiguration.totalShard]
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
    val defaultIntents: List<String> = TODO(), // EventIntents.intents.keys.toList(),

    /**
     * 服务器路径地址。
     * @see QGuildApi.URL_STRING
     */
    val serverUrl: String? = null,

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
    
    
    internal fun includeConfig(configuration: ConfigurableBotConfiguration) {
        if (totalShard != null) {
            TODO()
//            configuration.totalShard = totalShard
        }
        // TODO
//        configuration.intentsForShardFactory =
//            { shard -> intentValues[shard]?.let { Intents(it) } ?: defaultIntentsValue }
        if (serverUrl != null) {
            configuration.serverUrl = Url(serverUrl)
        }
    }
}
