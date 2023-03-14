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

package love.forte.simbot.component.qguild

import io.ktor.http.*
import love.forte.simbot.*
import love.forte.simbot.application.ApplicationConfiguration
import love.forte.simbot.application.EventProviderAutoRegistrarFactory
import love.forte.simbot.application.EventProviderFactory
import love.forte.simbot.bot.BotManager
import love.forte.simbot.bot.BotVerifyInfo
import love.forte.simbot.bot.ComponentMismatchException
import love.forte.simbot.component.qguild.config.QGBotFileConfiguration
import love.forte.simbot.component.qguild.internal.QGBotManagerImpl
import love.forte.simbot.event.EventProcessor
import love.forte.simbot.qguild.BotConfiguration
import love.forte.simbot.qguild.ConfigurableBotConfiguration
import org.slf4j.Logger
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext


/**
 *
 * QQ频道BOT的bot管理器。
 *
 * [QGBotManager] 不允许注册相同 `appId` 的bot。
 *
 * @author ForteScarlet
 */
public abstract class QGBotManager : BotManager<QGBot>() {
    public abstract val eventProcessor: EventProcessor
    protected abstract val logger: Logger
    abstract override val component: QQGuildComponent
    public abstract val configuration: QGBotManagerConfiguration


    /**
     * 注册一个Bot的信息，并使用默认配置。
     */
    override fun register(verifyInfo: BotVerifyInfo): QGBot {
        val serializer = QGBotFileConfiguration.serializer()

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
        val (appId, secret, token) = configuration.ticket
        return register(appId, secret, token, configuration::includeConfig)
    }

    /**
     * 通过所需信息注册一个bot。
     */
    public abstract fun register(
        appId: String,
        appKey: String,
        token: String,
        block: ConfigurableBotConfiguration.() -> Unit = {},
    ): QGBot

    /**
     * [QGBotManager] 的构建工厂。
     */
    public companion object Factory :
        EventProviderFactory<QGBotManager, QGBotManagerConfiguration> {
        override val key: Attribute<QGBotManager> = attribute("SIMBOT.TCG")

        override suspend fun create(
            eventProcessor: EventProcessor,
            components: List<Component>,
            applicationConfiguration: ApplicationConfiguration,
            configurator: QGBotManagerConfiguration.() -> Unit,
        ): QGBotManager {
            val configuration = QGBotManagerConfigurationImpl().also {
                it.parentCoroutineContext = applicationConfiguration.coroutineContext
                configurator(it)
            }

            // find component
            val component =
                components.find { it.id == QQGuildComponent.ID_VALUE } as? QQGuildComponent
                    ?: throw NoSuchComponentException("component id [${QQGuildComponent.ID_VALUE}], and type of QQGuildComponent.")

            return QGBotManagerImpl(eventProcessor, configuration, component).also {
                configuration.useBotManager(it)
            }
        }

        @JvmStatic
        @Deprecated("Use bot manager in Application.")
        public fun newInstance(
            processor: EventProcessor,
            configuration: QGBotManagerConfiguration,
        ): QGBotManager {
            return QGBotManagerImpl(processor, configuration, QQGuildComponent())
        }
    }
}

@DslMarker
@Retention(AnnotationRetention.BINARY)
internal annotation class QGBotManagerConfigurationDsl


/**
 * 得到一个BotManager.
 */
@Suppress("DEPRECATION")
@Deprecated("Use bot manager in Application.")
public fun qqGuildBotManager(
    processor: EventProcessor,
    block: QGBotManagerConfiguration.() -> Unit = { },
): QGBotManager {
    return QGBotManager.newInstance(processor, QGBotManagerConfigurationImpl().also(block))
}


/**
 * [QGBotManager] 使用的配置类描述。
 */
public interface QGBotManagerConfiguration {

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
    @QGBotManagerConfigurationDsl
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
    @QGBotManagerConfigurationDsl
    public fun register(
        appId: String, appKey: String, token: String,
        botConfiguration: BotConfiguration.() -> Unit = {},
        onBot: suspend (QGBot) -> Unit,
    )


}


/**
 * [QGBotManager] 的自动注册工厂。
 */
public class QGBotManagerAutoRegistrarFactory :
    EventProviderAutoRegistrarFactory<QGBotManager, QGBotManagerConfiguration> {
    override val registrar: QGBotManager.Factory get() = QGBotManager
}


/**
 * [QGBotManager] 使用的配置类。
 */
@Suppress("MemberVisibilityCanBePrivate")
private class QGBotManagerConfigurationImpl : QGBotManagerConfiguration {
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


    private var botManagerConfig: suspend (QGBotManager) -> Unit = {}

    private fun addBotManagerConfig(block: suspend (QGBotManager) -> Unit) {
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
        onBot: suspend (QGBot) -> Unit,
    ) {
        addBotManagerConfig { manager ->
            manager.register(appId, appKey, token, botConfiguration)
        }
    }

    suspend fun useBotManager(botManager: QGBotManagerImpl) {
        botManagerConfig(botManager)
    }

}



