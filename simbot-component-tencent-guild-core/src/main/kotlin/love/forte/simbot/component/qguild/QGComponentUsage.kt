/*
 * Copyright (c) 2022-2023. ForteScarlet.
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

import love.forte.simbot.ability.CompletionPerceivable
import love.forte.simbot.application.Application
import love.forte.simbot.application.ApplicationBuilder
import love.forte.simbot.application.ApplicationBuilderDsl


/**
 * 在 [ApplicationBuilder] 中安装使用 [QQGuildComponent]。
 *
 * usage:
 * ```kotlin
 * simbotApplication(Foo) {
 *    useQQGuildComponent()
 *    // 或
 *    useQQGuildComponent { ... }
 * }
 * ```
 *
 * 相当于：
 * ```kotlin
 * simbotApplication(Foo) {
 *    install(QQGuildComponent) { ... }
 * }
 * ```
 *
 * @see QQGuildComponent
 *
 */
@ApplicationBuilderDsl
public fun <A : Application> ApplicationBuilder<A>.useQQGuildComponent(configurator: QQGuildComponentConfiguration.(perceivable: CompletionPerceivable<A>) -> Unit = {}) {
    install(QQGuildComponent, configurator)
}

/**
 * 在 [ApplicationBuilder] 中安装使用 [QGBotManager]。
 *
 * usage:
 * ```kotlin
 * simbotApplication(Foo) {
 *    useQQGuildBotManager()
 *    // 或
 *    useQQGuildBotManager { ... }
 * }
 * ```
 *
 * 相当于：
 * ```kotlin
 * simbotApplication(Foo) {
 *    install(QGBotManager) { ... }
 * }
 * ```
 *
 * @see QGBotManager
 *
 */
@ApplicationBuilderDsl
public fun <A : Application> ApplicationBuilder<A>.useQQGuildBotManager(configurator: QGBotManagerConfiguration.(perceivable: CompletionPerceivable<A>) -> Unit = {}) {
    install(QGBotManager, configurator)
}


/**
 * 同时安装使用 [QQGuildComponent] 和 [QGBotManager].
 *
 * usage:
 * ```kotlin
 * simbotApplication(Foo) {
 *    useQQGuild()
 *    // 或
 *    useQQGuild {
 *       component { ... }
 *       botManager { ... }
 *    }
 * }
 * ```
 *
 * 相当于：
 * ```kotlin
 * simbotApplication(Foo) {
 *    install(QQGuildComponent) { ... }
 *    install(QGBotManager) { ... }
 * }
 * ```
 *
 *
 */
@ApplicationBuilderDsl
public fun <A : Application> ApplicationBuilder<A>.useQQGuild(builder: QQGuildUsageBuilder<A>.() -> Unit = {}) {
    QQGuildUsageBuilderImpl<A>().also(builder).build(this)
}

/**
 * 使用 [QGBotManager]
 *
 * @throws NoSuchElementException 如果不存在
 */
public inline fun <A : Application> A.qqGuildBots(block: QGBotManager.() -> Unit) {
    botManagers.firstQQGuildBotManager().also(block)
}


/**
 * 为 [QQGuildUsageBuilder] 中的函数染色。
 */
@DslMarker
@Target(AnnotationTarget.FUNCTION)
internal annotation class QQGuildUsageBuilderDsl


/**
 * 使用在 [useQQGuild] 函数中，用于同时针对 [QQGuildComponent] 和 [QGBotManager]
 * 进行配置。
 *
 * @see useQQGuild
 */
public interface QQGuildUsageBuilder<A : Application> {
    
    /**
     * 追加一个安装 [QQGuildComponent] 时候使用的配置。
     */
    @QQGuildUsageBuilderDsl
    public fun component(configurator: QQGuildComponentConfiguration.(perceivable: CompletionPerceivable<A>) -> Unit)
    
    
    /**
     * 追加一个安装 [QGBotManager] 时候使用的配置。
     */
    @QQGuildUsageBuilderDsl
    public fun botManager(configurator: QGBotManagerConfiguration.(perceivable: CompletionPerceivable<A>) -> Unit)
    
}


private class QQGuildUsageBuilderImpl<A : Application> : QQGuildUsageBuilder<A> {
    private var componentConfig: QQGuildComponentConfiguration.(perceivable: CompletionPerceivable<A>) -> Unit = {}
    private var botManagerConfig: QGBotManagerConfiguration.(perceivable: CompletionPerceivable<A>) -> Unit =
        {}
    
    override fun component(configurator: QQGuildComponentConfiguration.(perceivable: CompletionPerceivable<A>) -> Unit) {
        componentConfig.also { old ->
            componentConfig = {
                old(it)
                configurator(it)
            }
        }
    }
    
    override fun botManager(configurator: QGBotManagerConfiguration.(perceivable: CompletionPerceivable<A>) -> Unit) {
        botManagerConfig.also { old ->
            botManagerConfig = {
                old(it)
                configurator(it)
            }
        }
    }
    
    fun build(applicationBuilder: ApplicationBuilder<A>) {
        applicationBuilder.useQQGuildComponent(componentConfig)
        applicationBuilder.useQQGuildBotManager(botManagerConfig)
    }
    
    
}
