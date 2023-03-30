/*
 * Copyright (c) 2022-2023. ForteScarlet.
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

import love.forte.simbot.ability.CompletionPerceivable
import love.forte.simbot.application.*


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
 * 在 [ApplicationBuilder] 中 **尝试** 安装使用 [QQGuildBotManager]。
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
 *    install(QQGuildBotManager) { ... }
 * }
 * ```
 * @see QQGuildBotManager
 *
 */
@Suppress("UNCHECKED_CAST")
@ApplicationBuilderDsl
@Throws(ClassNotFoundException::class)
public fun <A : Application> ApplicationBuilder<A>.useQQGuildBotManager(configurator: QQGuildBotManagerConfiguration.(perceivable: CompletionPerceivable<A>) -> Unit = {}) {
    install(QQGuildBotManager, configurator)
}

/**
 * 同时安装使用 [QQGuildComponent] 和 [QQGuildBotManager].
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
 * 使用 [QQGuildBotManager]
 *
 * @throws NoSuchElementException 如果不存在
 */
public inline fun <A : Application> A.qqGuildBots(block: QQGuildBotManager.() -> Unit) {
    botManagers.firstQQGuildBotManager().also(block)
}


/**
 * 为 [QQGuildUsageBuilder] 中的函数染色。
 */
@DslMarker
@Target(AnnotationTarget.FUNCTION)
internal annotation class QQGuildUsageBuilderDsl


/**
 * 使用在 [useQQGuild] 函数中，用于同时针对 [QQGuildComponent] 和 [QQGuildBotManager]
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
     * 追加一个安装 [QQGuildBotManager] 时候使用的配置。
     */
    @QQGuildUsageBuilderDsl
    public fun botManager(configurator: QQGuildBotManagerConfiguration.(perceivable: CompletionPerceivable<A>) -> Unit)
    
}


private class QQGuildUsageBuilderImpl<A : Application> : QQGuildUsageBuilder<A> {
    private var componentConfig: QQGuildComponentConfiguration.(perceivable: CompletionPerceivable<A>) -> Unit = {}
    private var botManagerConfig: QQGuildBotManagerConfiguration.(perceivable: CompletionPerceivable<A>) -> Unit =
        {}
    
    override fun component(configurator: QQGuildComponentConfiguration.(perceivable: CompletionPerceivable<A>) -> Unit) {
        componentConfig.also { old ->
            componentConfig = {
                old(it)
                configurator(it)
            }
        }
    }
    
    override fun botManager(configurator: QQGuildBotManagerConfiguration.(perceivable: CompletionPerceivable<A>) -> Unit) {
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
