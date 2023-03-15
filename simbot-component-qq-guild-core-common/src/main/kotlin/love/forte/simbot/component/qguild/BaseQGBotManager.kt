/*
 * Copyright (c) 2021-2023. ForteScarlet.
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

import io.ktor.http.*
import kotlinx.coroutines.Job
import love.forte.simbot.*
import love.forte.simbot.application.ApplicationConfiguration
import love.forte.simbot.bot.BotManager
import love.forte.simbot.bot.BotVerifyInfo
import love.forte.simbot.bot.ComponentMismatchException
import love.forte.simbot.component.qguild.config.QGBotComponentConfiguration
import love.forte.simbot.component.qguild.config.QGBotFileConfiguration
import love.forte.simbot.component.qguild.util.registerRootJob
import love.forte.simbot.event.EventProcessor
import love.forte.simbot.qguild.BotConfiguration
import love.forte.simbot.qguild.ConfigurableBotConfiguration
import org.slf4j.Logger
import kotlin.coroutines.CoroutineContext

/**
 * QQ频道BOT的bot管理器。
 *
 * [BaseQGBotManager] 不允许注册相同 `appId` 的bot。
 *
 * @author ForteScarlet
 */
public abstract class BaseQGBotManager : BotManager<QGBot>() {
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
     *
     * 注意，在配置 [ConfigurableBotConfiguration] 时，
     * 如果 [coroutineContext][ConfigurableBotConfiguration.coroutineContext] 中
     * 存在自定义的 [Job][kotlinx.coroutines.Job]，那么 `Application` 中的Job
     * 则会作为一个 `Root Job` 而不是 `Parent Job` 使用。
     *
     * `Root Job` 仅会在其自身完成或关闭的时候**通知**相关联的子类使它们关闭，
     * 但不会有硬性关联，这种通知是通过 [Job.invokeOnCompletion][kotlinx.coroutines.Job.invokeOnCompletion] 实现的，
     * 参考 [Job.registerRootJob]。
     *
     * 如果 [coroutineContext] 中不存在自定义的Job，则会直接使用 [QGBotManager] 内的 [Job] 作为 parent Job 。
     *
     */
    public abstract fun register(
        appId: String,
        appKey: String,
        token: String,
        block: QGBotComponentConfiguration.() -> Unit = {},
    ): QGBot
}

/**
 * @suppress
 */
@DslMarker
@Retention(AnnotationRetention.BINARY)
public annotation class QGBotManagerConfigurationDsl


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
        botConfiguration: QGBotComponentConfiguration.() -> Unit = {},
        onBot: suspend (QGBot) -> Unit,
    )


}


