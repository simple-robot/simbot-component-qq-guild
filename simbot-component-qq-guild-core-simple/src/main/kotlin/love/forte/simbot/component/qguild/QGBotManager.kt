/*
 * Copyright (c) 2023. ForteScarlet.
 *
 * This file is part of simbot-component-qq-guild.
 *
 * simbot-component-qq-guild is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * simbot-component-qq-guild is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with simbot-component-qq-guild. If not, see <https://www.gnu.org/licenses/>.
 */

package love.forte.simbot.component.qguild

import love.forte.simbot.Attribute
import love.forte.simbot.Component
import love.forte.simbot.NoSuchComponentException
import love.forte.simbot.application.ApplicationConfiguration
import love.forte.simbot.application.EventProviderAutoRegistrarFactory
import love.forte.simbot.application.EventProviderFactory
import love.forte.simbot.attribute
import love.forte.simbot.component.qguild.config.QGBotComponentConfiguration
import love.forte.simbot.component.qguild.internal.QGBotManagerImpl
import love.forte.simbot.event.EventProcessor
import love.forte.simbot.qguild.BotConfiguration
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
public abstract class QGBotManager : BaseQGBotManager() {

    /**
     * [QGBotManager] 的构建工厂。
     */
    public companion object Factory : EventProviderFactory<QGBotManager, QGBotManagerConfiguration> {
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
            val component = components.find { it.id == QQGuildComponent.ID_VALUE } as? QQGuildComponent
                ?: throw NoSuchComponentException("component id [${QQGuildComponent.ID_VALUE}], and type of QQGuildComponent.")

            return QGBotManagerImpl(eventProcessor, configuration, component).also {
                configuration.useBotManager(it)
            }
        }
    }
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

    override var botConfigure: BotConfiguration.(appId: String, appKey: String, token: String) -> Unit = { _, _, _ -> }

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
        botConfiguration: QGBotComponentConfiguration.() -> Unit,
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
