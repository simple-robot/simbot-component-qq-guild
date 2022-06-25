package love.forte.simbot.component.tencentguild

import love.forte.simbot.FragileSimbotApi
import love.forte.simbot.OriginBotManager
import love.forte.simbot.application.ApplicationBuilder
import love.forte.simbot.application.BotRegistrar
import love.forte.simbot.application.EventProvider

/**
 * 在 [ApplicationBuilder.bots] 作用域中寻找并使用 [TencentGuildBotManager]。
 * ```kotlin
 * simpleApplication {
 *     tencentGuildBots {
 *         val bot = register("app id", "app key", "token") {
 *             // config
 *         }
 *         bot.start()
 *     }
 * }
 * ```
 *
 * 如果当前环境中不存在 [TencentGuildBotManager], 则会抛出 [NoSuchElementException].
 * 此异常不会准备阶段被抛出，而是在真正执行构建时。
 *
 * @throws NoSuchElementException 当前环境中不存在 [TencentGuildBotManager]
 *
 */
public inline fun ApplicationBuilder<*>.tencentGuildBots(
    crossinline block: suspend TencentGuildBotManager.(BotRegistrar) -> Unit,
) {
    bots {
        val manager = providers.firstTencentGuildBotManagerOrNull()
            ?: throw NoSuchElementException("No event provider of type [TencentGuildBotManager] in providers $providers")
        manager.block(this)
    }
}

/**
 * 在 [ApplicationBuilder.bots] 作用域中寻找并使用 [TencentGuildBotManager]。
 * ```kotlin
 * simpleApplication {
 *     tencentGuildBots {
 *         val bot = register("app id", "app key", "token") {
 *             // config
 *         }
 *         bot.start()
 *     }
 * }
 * ```
 *
 * 如果当前环境中不存在 [TencentGuildBotManager], 则不会执行 [block].
 *
 */
public inline fun ApplicationBuilder<*>.tencentGuildBotsIfSupport(
    crossinline block: suspend TencentGuildBotManager.(BotRegistrar) -> Unit,
) {
    bots {
        val manager = providers.firstTencentGuildBotManagerOrNull() ?: return@bots
        manager.block(this)
    }
}

/**
 * 在 [ApplicationBuilder.bots] 作用域中寻找并使用 [TencentGuildBotManager]。
 * ```kotlin
 * simpleApplication {
 *     bots {
 *        tencentGuilds {
 *            val bot = register("app id", "app key", "token") {
 *                // config
 *            }
 *            bot.start()
 *        }
 *     }
 * }
 * ```
 *
 * 如果当前环境中不存在 [TencentGuildBotManager], 则会抛出 [NoSuchElementException].
 * 此异常不会准备阶段被抛出，而是在真正执行构建时。
 *
 * @throws NoSuchElementException 当前环境中不存在 [TencentGuildBotManager]
 *
 */
public suspend inline fun BotRegistrar.tencentGuild(
    crossinline block: suspend TencentGuildBotManager.() -> Unit,
) {
    val manager = providers.firstTencentGuildBotManagerOrNull()
        ?: throw NoSuchElementException("No event provider of type [TencentGuildBotManager] in providers $providers")
    manager.block()
}

/**
 * 在 [ApplicationBuilder.bots] 作用域中寻找并使用 [TencentGuildBotManager]。
 * ```kotlin
 * simpleApplication {
 *    bots {
 *        tencentGuildIfSupport {
 *            val bot = register("app id", "app key", "token") {
 *                // config
 *            }
 *            bot.start()
 *        }
 *    }
 * }
 * ```
 *
 * 如果当前环境中不存在 [TencentGuildBotManager], 则不会执行 [block].
 *
 */
public suspend inline fun BotRegistrar.tencentGuildIfSupport(
    crossinline block: suspend TencentGuildBotManager.() -> Unit,
) {
    val manager = providers.firstTencentGuildBotManagerOrNull() ?: return
    manager.block()
}


// region botManager 扩展
/**
 * 从 [OriginBotManager] 中过滤取出所有 [TencentGuildBotManager] 实例.
 */
@FragileSimbotApi
@Suppress("NOTHING_TO_INLINE")
public inline fun tencentGuildBotManagers(): List<TencentGuildBotManager> =
    OriginBotManager.filterIsInstance<TencentGuildBotManager>()

/**
 * 从 [OriginBotManager] 中取出第一个 [TencentGuildBotManager] 实例。
 *
 * @throws NoSuchElementException 如果元素不存在
 */
@FragileSimbotApi
@Suppress("NOTHING_TO_INLINE")
public inline fun tencentGuildBotManager(): TencentGuildBotManager =
    OriginBotManager.first { it is TencentGuildBotManager } as TencentGuildBotManager

/**
 * 从 [OriginBotManager] 中过滤取出所有 [TencentGuildBotManager] 实例。
 * 如果元素不存在则得到null。
 */
@FragileSimbotApi
@Suppress("NOTHING_TO_INLINE")
public inline fun tencentGuildBotManagerOrNull(): TencentGuildBotManager? =
    OriginBotManager.firstOrNull { it is TencentGuildBotManager } as TencentGuildBotManager?

/**
 * 从中过滤取出所有 [TencentGuildBotManager] 实例.
 *
 * @see filterIsTencentGuildBotManagers
 */
@Suppress("NOTHING_TO_INLINE")
@Deprecated("Use filterIsTencentGuildBotManagers", ReplaceWith("filterIsTencentGuildBotManagers()"))
public inline fun Iterable<EventProvider>.tencentGuildBotManagers(): List<TencentGuildBotManager> =
    filterIsTencentGuildBotManagers()

/**
 * 从序列中过滤出 [TencentGuildBotManager] 实例.
 *
 *  @see filterIsTencentGuildBotManagers
 */
@Suppress("NOTHING_TO_INLINE")
@Deprecated("Use filterIsTencentGuildBotManagers", ReplaceWith("filterIsTencentGuildBotManagers()"))
public inline fun Sequence<EventProvider>.tencentGuildBotManagers(): Sequence<TencentGuildBotManager> =
    filterIsTencentGuildBotManagers()

/**
 * 从中过滤取出所有 [TencentGuildBotManager] 实例.
 */
@Suppress("NOTHING_TO_INLINE")
public inline fun Iterable<EventProvider>.filterIsTencentGuildBotManagers(): List<TencentGuildBotManager> =
    filterIsInstance<TencentGuildBotManager>()

/**
 * 从序列中过滤出 [TencentGuildBotManager] 实例.
 */
@Suppress("NOTHING_TO_INLINE")
public inline fun Sequence<EventProvider>.filterIsTencentGuildBotManagers(): Sequence<TencentGuildBotManager> =
    filterIsInstance<TencentGuildBotManager>()


/**
 * 从中过滤取出所有 [TencentGuildBotManager] 实例。
 *
 * @throws NoSuchElementException 如果不存在
 */
@Suppress("NOTHING_TO_INLINE")
public inline fun Iterable<EventProvider>.firstTencentGuildBotManager(): TencentGuildBotManager =
    first { it is TencentGuildBotManager } as TencentGuildBotManager

/**
 * 从序列中过滤出 [TencentGuildBotManager] 实例。
 *
 * @throws NoSuchElementException 如果不存在
 */
@Suppress("NOTHING_TO_INLINE")
public inline fun Sequence<EventProvider>.firstTencentGuildBotManager(): TencentGuildBotManager =
    first { it is TencentGuildBotManager } as TencentGuildBotManager


/**
 * 从中过滤取出所有 [TencentGuildBotManager] 实例。
 * 如果不存在则得到null。
 */
@Suppress("NOTHING_TO_INLINE")
public inline fun Iterable<EventProvider>.firstTencentGuildBotManagerOrNull(): TencentGuildBotManager? =
    firstOrNull { it is TencentGuildBotManager } as TencentGuildBotManager?

/**
 * 从序列中过滤出 [TencentGuildBotManager] 实例。
 * 如果不存在则得到null。
 */
@Suppress("NOTHING_TO_INLINE")
public inline fun Sequence<EventProvider>.firstTencentGuildBotManagerOrNull(): TencentGuildBotManager? =
    firstOrNull { it is TencentGuildBotManager } as TencentGuildBotManager?


// endregion
