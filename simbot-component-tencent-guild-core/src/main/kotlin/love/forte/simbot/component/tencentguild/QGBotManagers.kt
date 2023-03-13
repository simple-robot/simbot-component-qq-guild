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

import love.forte.simbot.FragileSimbotApi
import love.forte.simbot.application.ApplicationBuilder
import love.forte.simbot.application.BotRegistrar
import love.forte.simbot.application.EventProvider
import love.forte.simbot.bot.OriginBotManager

/**
 * 在 [ApplicationBuilder.bots] 作用域中寻找并使用 [QGBotManager]。
 * ```kotlin
 * simpleApplication {
 *     qqGuildBots {
 *         val bot = register("app id", "app key", "token") {
 *             // config
 *         }
 *         bot.start()
 *     }
 * }
 * ```
 *
 * 如果当前环境中不存在 [QGBotManager], 则会抛出 [NoSuchElementException].
 * 此异常不会准备阶段被抛出，而是在真正执行构建时。
 *
 * @throws NoSuchElementException 当前环境中不存在 [QGBotManager]
 *
 */
public inline fun ApplicationBuilder<*>.qqGuildBots(
    crossinline block: suspend QGBotManager.(BotRegistrar) -> Unit,
) {
    bots {
        val manager = providers.firstQQGuildBotManagerOrNull()
            ?: throw NoSuchElementException("No event provider of type [QGBotManager] in providers $providers")
        manager.block(this)
    }
}

/**
 * 在 [ApplicationBuilder.bots] 作用域中寻找并使用 [QGBotManager]。
 * ```kotlin
 * simpleApplication {
 *     qqGuildBotsIfSupport {
 *         val bot = register("app id", "app key", "token") {
 *             // config
 *         }
 *         bot.start()
 *     }
 * }
 * ```
 *
 * 如果当前环境中不存在 [QGBotManager], 则不会执行 [block].
 *
 */
public inline fun ApplicationBuilder<*>.qqGuildBotsIfSupport(
    crossinline block: suspend QGBotManager.(BotRegistrar) -> Unit,
) {
    bots {
        val manager = providers.firstQQGuildBotManagerOrNull() ?: return@bots
        manager.block(this)
    }
}

/**
 * 在 [ApplicationBuilder.bots] 作用域中寻找并使用 [QGBotManager]。
 * ```kotlin
 * simpleApplication {
 *     bots {
 *        qqGuild {
 *            val bot = register("app id", "app key", "token") {
 *                // config
 *            }
 *            bot.start()
 *        }
 *     }
 * }
 * ```
 *
 * 如果当前环境中不存在 [QGBotManager], 则会抛出 [NoSuchElementException].
 * 此异常不会准备阶段被抛出，而是在真正执行构建时。
 *
 * @throws NoSuchElementException 当前环境中不存在 [QGBotManager]
 *
 */
public suspend inline fun BotRegistrar.qqGuild(
    crossinline block: suspend QGBotManager.() -> Unit,
) {
    val manager = providers.firstQQGuildBotManagerOrNull()
        ?: throw NoSuchElementException("No event provider of type [QGBotManager] in providers $providers")
    manager.block()
}

/**
 * 在 [ApplicationBuilder.bots] 作用域中寻找并使用 [QGBotManager]。
 * ```kotlin
 * simpleApplication {
 *    bots {
 *        qqGuildIfSupport {
 *            val bot = register("app id", "app key", "token") {
 *                // config
 *            }
 *            bot.start()
 *        }
 *    }
 * }
 * ```
 *
 * 如果当前环境中不存在 [QGBotManager], 则不会执行 [block].
 *
 */
public suspend inline fun BotRegistrar.qqGuildIfSupport(
    crossinline block: suspend QGBotManager.() -> Unit,
) {
    val manager = providers.firstQQGuildBotManagerOrNull() ?: return
    manager.block()
}


// region botManager 扩展
/**
 * 从 [OriginBotManager] 中过滤取出所有 [QGBotManager] 实例.
 */
@FragileSimbotApi
@Suppress("NOTHING_TO_INLINE")
public inline fun qqGuildBotManagers(): List<QGBotManager> =
    OriginBotManager.filterIsInstance<QGBotManager>()

/**
 * 从 [OriginBotManager] 中取出第一个 [QGBotManager] 实例。
 *
 * @throws NoSuchElementException 如果元素不存在
 */
@FragileSimbotApi
@Suppress("NOTHING_TO_INLINE")
public inline fun qqGuildBotManager(): QGBotManager =
    OriginBotManager.first { it is QGBotManager } as QGBotManager

/**
 * 从 [OriginBotManager] 中过滤取出所有 [QGBotManager] 实例。
 * 如果元素不存在则得到null。
 */
@FragileSimbotApi
@Suppress("NOTHING_TO_INLINE")
public inline fun qqGuildBotManagerOrNull(): QGBotManager? =
    OriginBotManager.firstOrNull { it is QGBotManager } as QGBotManager?

/**
 * 从中过滤取出所有 [QGBotManager] 实例.
 *
 * @see filterIsQQGuildBotManagers
 */
@Suppress("NOTHING_TO_INLINE")
@Deprecated("Use filterIsQQGuildBotManagers", ReplaceWith("filterIsQQGuildBotManagers()"))
public inline fun Iterable<EventProvider>.qqGuildBotManagers(): List<QGBotManager> =
    filterIsQQGuildBotManagers()

/**
 * 从序列中过滤出 [QGBotManager] 实例.
 *
 *  @see filterIsQQGuildBotManagers
 */
@Suppress("NOTHING_TO_INLINE")
@Deprecated("Use filterIsQQGuildBotManagers", ReplaceWith("filterIsQQGuildBotManagers()"))
public inline fun Sequence<EventProvider>.qqGuildBotManagers(): Sequence<QGBotManager> =
    filterIsQQGuildBotManagers()

/**
 * 从中过滤取出所有 [QGBotManager] 实例.
 */
@Suppress("NOTHING_TO_INLINE")
public inline fun Iterable<EventProvider>.filterIsQQGuildBotManagers(): List<QGBotManager> =
    filterIsInstance<QGBotManager>()

/**
 * 从序列中过滤出 [QGBotManager] 实例.
 */
@Suppress("NOTHING_TO_INLINE")
public inline fun Sequence<EventProvider>.filterIsQQGuildBotManagers(): Sequence<QGBotManager> =
    filterIsInstance<QGBotManager>()


/**
 * 从中过滤取出所有 [QGBotManager] 实例。
 *
 * @throws NoSuchElementException 如果不存在
 */
@Suppress("NOTHING_TO_INLINE")
public inline fun Iterable<EventProvider>.firstQQGuildBotManager(): QGBotManager =
    first { it is QGBotManager } as QGBotManager

/**
 * 从序列中过滤出 [QGBotManager] 实例。
 *
 * @throws NoSuchElementException 如果不存在
 */
@Suppress("NOTHING_TO_INLINE")
public inline fun Sequence<EventProvider>.firstQQGuildBotManager(): QGBotManager =
    first { it is QGBotManager } as QGBotManager


/**
 * 从中过滤取出所有 [QGBotManager] 实例。
 * 如果不存在则得到null。
 */
@Suppress("NOTHING_TO_INLINE")
public inline fun Iterable<EventProvider>.firstQQGuildBotManagerOrNull(): QGBotManager? =
    firstOrNull { it is QGBotManager } as QGBotManager?

/**
 * 从序列中过滤出 [QGBotManager] 实例。
 * 如果不存在则得到null。
 */
@Suppress("NOTHING_TO_INLINE")
public inline fun Sequence<EventProvider>.firstQQGuildBotManagerOrNull(): QGBotManager? =
    firstOrNull { it is QGBotManager } as QGBotManager?


// endregion
