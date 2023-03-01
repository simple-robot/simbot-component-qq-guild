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

package love.forte.simbot.component.tencentguild

import love.forte.simbot.ability.CompletionPerceivable
import love.forte.simbot.application.Application
import love.forte.simbot.application.ApplicationBuilder
import love.forte.simbot.application.ApplicationBuilderDsl


/**
 * 在 [ApplicationBuilder] 中安装使用 [TencentGuildComponent]。
 *
 * usage:
 * ```kotlin
 * simbotApplication(Foo) {
 *    useTencentGuildComponent()
 *    // 或
 *    useTencentGuildComponent { ... }
 * }
 * ```
 *
 * 相当于：
 * ```kotlin
 * simbotApplication(Foo) {
 *    install(TencentGuildComponent) { ... }
 * }
 * ```
 *
 * @see TencentGuildComponent
 *
 */
@ApplicationBuilderDsl
public fun <A : Application> ApplicationBuilder<A>.useTencentGuildComponent(configurator: TencentGuildComponentConfiguration.(perceivable: CompletionPerceivable<A>) -> Unit = {}) {
    install(TencentGuildComponent, configurator)
}

/**
 * 在 [ApplicationBuilder] 中安装使用 [TencentGuildBotManager]。
 *
 * usage:
 * ```kotlin
 * simbotApplication(Foo) {
 *    useTencentGuildBotManager()
 *    // 或
 *    useTencentGuildBotManager { ... }
 * }
 * ```
 *
 * 相当于：
 * ```kotlin
 * simbotApplication(Foo) {
 *    install(TencentGuildBotManager) { ... }
 * }
 * ```
 *
 * @see TencentGuildBotManager
 *
 */
@ApplicationBuilderDsl
public fun <A : Application> ApplicationBuilder<A>.useTencentGuildBotManager(configurator: TencentGuildBotManagerConfiguration.(perceivable: CompletionPerceivable<A>) -> Unit = {}) {
    install(TencentGuildBotManager, configurator)
}


/**
 * 同时安装使用 [TencentGuildComponent] 和 [TencentGuildBotManager].
 *
 * usage:
 * ```kotlin
 * simbotApplication(Foo) {
 *    useTencentGuild()
 *    // 或
 *    useTencentGuild {
 *       component { ... }
 *       botManager { ... }
 *    }
 * }
 * ```
 *
 * 相当于：
 * ```kotlin
 * simbotApplication(Foo) {
 *    install(TencentGuildComponent) { ... }
 *    install(TencentGuildBotManager) { ... }
 * }
 * ```
 *
 *
 */
@ApplicationBuilderDsl
public fun <A : Application> ApplicationBuilder<A>.useTencentGuild(builder: TencentGuildUsageBuilder<A>.() -> Unit = {}) {
    TencentGuildUsageBuilderImpl<A>().also(builder).build(this)
}

/**
 * 为 [TencentGuildUsageBuilder] 中的函数染色。
 */
@DslMarker
@Target(AnnotationTarget.FUNCTION)
internal annotation class TencentGuildUsageBuilderDsl


/**
 * 使用在 [useTencentGuild] 函数中，用于同时针对 [TencentGuildComponent] 和 [TencentGuildBotManager]
 * 进行配置。
 *
 * @see useTencentGuild
 */
public interface TencentGuildUsageBuilder<A : Application> {
    
    /**
     * 追加一个安装 [TencentGuildComponent] 时候使用的配置。
     */
    @TencentGuildUsageBuilderDsl
    public fun component(configurator: TencentGuildComponentConfiguration.(perceivable: CompletionPerceivable<A>) -> Unit)
    
    
    /**
     * 追加一个安装 [TencentGuildBotManager] 时候使用的配置。
     */
    @TencentGuildUsageBuilderDsl
    public fun botManager(configurator: TencentGuildBotManagerConfiguration.(perceivable: CompletionPerceivable<A>) -> Unit)
    
}


private class TencentGuildUsageBuilderImpl<A : Application> : TencentGuildUsageBuilder<A> {
    private var componentConfig: TencentGuildComponentConfiguration.(perceivable: CompletionPerceivable<A>) -> Unit = {}
    private var botManagerConfig: TencentGuildBotManagerConfiguration.(perceivable: CompletionPerceivable<A>) -> Unit =
        {}
    
    override fun component(configurator: TencentGuildComponentConfiguration.(perceivable: CompletionPerceivable<A>) -> Unit) {
        componentConfig.also { old ->
            componentConfig = {
                old(it)
                configurator(it)
            }
        }
    }
    
    override fun botManager(configurator: TencentGuildBotManagerConfiguration.(perceivable: CompletionPerceivable<A>) -> Unit) {
        botManagerConfig.also { old ->
            botManagerConfig = {
                old(it)
                configurator(it)
            }
        }
    }
    
    fun build(applicationBuilder: ApplicationBuilder<A>) {
        applicationBuilder.useTencentGuildComponent(componentConfig)
        applicationBuilder.useTencentGuildBotManager(botManagerConfig)
    }
    
    
}
