/*
 *  Copyright (c) 2022-2022 ForteScarlet <ForteScarlet@163.com>
 *
 *  本文件是 simbot-component-tencent-guild 的一部分。
 *
 *  simbot-component-tencent-guild 是自由软件：你可以再分发之和/或依照由自由软件基金会发布的 GNU 通用公共许可证修改之，无论是版本 3 许可证，还是（按你的决定）任何以后版都可以。
 *
 *  发布 simbot-component-tencent-guild 是希望它能有用，但是并无保障;甚至连可销售和符合某个特定的目的都不保证。请参看 GNU 通用公共许可证，了解详情。
 *
 *  你应该随程序获得一份 GNU 通用公共许可证的复本。如果没有，请看:
 *  https://www.gnu.org/licenses
 *  https://www.gnu.org/licenses/gpl-3.0-standalone.html
 *  https://www.gnu.org/licenses/lgpl-3.0-standalone.html
 *
 *
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