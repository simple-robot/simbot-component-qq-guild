/*
 * Copyright (c) 2023. ForteScarlet.
 *
 * This file is part of simbot-component-qq-guild.
 *
 * simbot-component-qq-guild is free software: you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * simbot-component-qq-guild is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with simbot-component-qq-guild.
 * If not, see <https://www.gnu.org/licenses/>.
 */

package love.forte.simbot.component.qguild

import love.forte.simbot.Component
import love.forte.simbot.NoSuchComponentException
import love.forte.simbot.application.ApplicationConfiguration
import love.forte.simbot.application.EventProviderAutoRegistrarFactory
import love.forte.simbot.component.qguild.config.QGBotComponentConfiguration
import love.forte.simbot.component.qguild.internal.QQGuildBotManagerImpl
import love.forte.simbot.event.EventProcessor
import love.forte.simbot.qguild.BotConfiguration
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 *
 * QQ频道BOT的bot管理器。
 *
 * [QQGuildBotManager] 不允许注册相同 `appId` 的bot。
 *
 * _Note: 仅由内部实现，对外不稳定_
 *
 * @author ForteScarlet
 */
public abstract class QQGuildBotManager : BaseQQGuildBotManager() {

    /**
     * [QQGuildBotManager] 的构建工厂。
     */
    public companion object Factory : BaseFactory<QQGuildBotManager>() {
        override suspend fun create(
            eventProcessor: EventProcessor,
            components: List<Component>,
            applicationConfiguration: ApplicationConfiguration,
            configurator: QQGuildBotManagerConfiguration.() -> Unit,
        ): QQGuildBotManager {
            val configuration = QQGuildBotManagerConfigurationImpl().also {
                it.parentCoroutineContext = applicationConfiguration.coroutineContext
                configurator(it)
            }

            // find component
            val component = components.find { it.id == QQGuildComponent.ID_VALUE } as? QQGuildComponent
                ?: throw NoSuchComponentException("component id [${QQGuildComponent.ID_VALUE}], and type of QQGuildComponent.")

            return QQGuildBotManagerImpl(eventProcessor, configuration, component).also {
                configuration.useBotManager(it)
            }
        }
    }
}


/**
 * [QQGuildBotManager] 的自动注册工厂。
 */
public class QGBotManagerAutoRegistrarFactory :
    EventProviderAutoRegistrarFactory<QQGuildBotManager, QQGuildBotManagerConfiguration> {
    override val registrar: QQGuildBotManager.Factory get() = QQGuildBotManager
}


/**
 * [QQGuildBotManager] 使用的配置类。
 */
@Suppress("MemberVisibilityCanBePrivate")
private class QQGuildBotManagerConfigurationImpl : QQGuildBotManagerConfiguration {
    override var parentCoroutineContext: CoroutineContext = EmptyCoroutineContext

    override var botConfigure: BotConfiguration.(appId: String, appKey: String, token: String) -> Unit = { _, _, _ -> }

    override fun botConfigure(configure: BotConfiguration.(appId: String, appKey: String, token: String) -> Unit) {
        botConfigure.also { old ->
            botConfigure = { appId, appKey, token ->
                old(appId, appKey, token)
                configure(appId, appKey, token)
            }
        }
    }


    private var botManagerConfig: suspend (QQGuildBotManager) -> Unit = {}

    private fun addBotManagerConfig(block: suspend (QQGuildBotManager) -> Unit) {
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
        botConfiguration: QGBotComponentConfiguration.() -> Unit,
        onBot: suspend (QGBot) -> Unit,
    ) {
        addBotManagerConfig { manager ->
            manager.register(appId, appKey, token, botConfiguration)
        }
    }

    suspend fun useBotManager(botManager: QQGuildBotManagerImpl) {
        botManagerConfig(botManager)
    }

}
