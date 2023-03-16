/*
 * Copyright (c) 2021-2023. ForteScarlet.
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

import love.forte.simbot.FragileSimbotApi
import love.forte.simbot.application.Application
import love.forte.simbot.application.ApplicationBuilder
import love.forte.simbot.application.BotRegistrar
import love.forte.simbot.application.EventProvider
import love.forte.simbot.bot.OriginBotManager


/**
 * 在 [ApplicationBuilder.bots] 作用域中寻找并使用 [QQGuildBotManager]。
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
 * 如果当前环境中不存在 [QQGuildBotManager], 则会抛出 [NoSuchElementException].
 * 此异常不会在准备阶段被抛出，而是在真正执行构建时。
 *
 * @throws NoSuchElementException 不存在 [QQGuildBotManager]
 *
 */
public inline fun ApplicationBuilder<*>.qqGuildBots(
    crossinline block: suspend QQGuildBotManager.(BotRegistrar) -> Unit,
) {
    bots {
        val manager = providers.firstQQGuildBotManagerOrNull()
            ?: throw NoSuchElementException("No event provider of type [BaseQGBotManager] in providers $providers")
        manager.block(this)
    }
}

/**
 * 在 [ApplicationBuilder.bots] 作用域中寻找并使用 [QQGuildBotManager]。
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
 * 如果当前环境中不存在 [QQGuildBotManager], 则不会执行 [block].
 *
 */
public inline fun ApplicationBuilder<*>.qqGuildBotsIfSupport(
    crossinline block: suspend QQGuildBotManager.(BotRegistrar) -> Unit,
) {
    bots {
        val manager = providers.firstQQGuildBotManagerOrNull() ?: return@bots
        manager.block(this)
    }
}

/**
 * 在 [ApplicationBuilder.bots] 作用域中寻找并使用 [QQGuildBotManager]。
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
 * 如果当前环境中不存在 [QQGuildBotManager], 则会抛出 [NoSuchElementException].
 * 此异常不会准备阶段被抛出，而是在真正执行构建时。
 *
 * @throws NoSuchElementException 当前环境中不存在 [QQGuildBotManager]
 *
 */
public suspend inline fun BotRegistrar.qqGuild(
    crossinline block: suspend QQGuildBotManager.() -> Unit,
) {
    val manager = providers.firstQQGuildBotManagerOrNull()
        ?: throw NoSuchElementException("No event provider of type [QQGuildBotManager] in providers $providers")
    manager.block()
}

/**
 * 在 [ApplicationBuilder.bots] 作用域中寻找并使用 [QQGuildBotManager]。
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
 * 如果当前环境中不存在 [QQGuildBotManager], 则不会执行 [block].
 *
 */
public suspend inline fun BotRegistrar.qqGuildIfSupport(
    crossinline block: suspend QQGuildBotManager.() -> Unit,
) {
    val manager = providers.firstQQGuildBotManagerOrNull() ?: return
    manager.block()
}

/**
 * 获取第一个 [QQGuildBotManager] 并使用
 * @throws [NoSuchElementException] if no such element is found.
 */
public inline fun Application.qgGuildBots(block: QQGuildBotManager.() -> Unit) {
    val manager = botManagers.first { it is QQGuildBotManager } as QQGuildBotManager
    manager.block()
}

/**
 * 尝试寻找并使用 [QQGuildBotManager]
 */
public inline fun Application.qgGuildBotsIfSupport(block: QQGuildBotManager.() -> Unit) {
    val manager = (botManagers.firstOrNull { it is QQGuildBotManager } ?: return) as QQGuildBotManager
    manager.block()
}

/**
 * 从 [OriginBotManager] 中过滤取出所有 [QQGuildBotManager] 实例.
 */
@FragileSimbotApi
@Suppress("NOTHING_TO_INLINE")
public inline fun qqGuildBotManagers(): List<QQGuildBotManager> =
    OriginBotManager.filterIsInstance<QQGuildBotManager>()

/**
 * 从 [OriginBotManager] 中取出第一个 [QQGuildBotManager] 实例。
 *
 * @throws NoSuchElementException 如果元素不存在
 */
@FragileSimbotApi
@Suppress("NOTHING_TO_INLINE")
public inline fun qqGuildBotManager(): QQGuildBotManager =
    OriginBotManager.first { it is QQGuildBotManager } as QQGuildBotManager

/**
 * 从 [OriginBotManager] 中过滤取出所有 [QQGuildBotManager] 实例。
 * 如果元素不存在则得到null。
 */
@FragileSimbotApi
@Suppress("NOTHING_TO_INLINE")
public inline fun qqGuildBotManagerOrNull(): QQGuildBotManager? =
    OriginBotManager.firstOrNull { it is QQGuildBotManager } as QQGuildBotManager?

/**
 * 从中过滤取出所有 [QQGuildBotManager] 实例.
 */
@Suppress("NOTHING_TO_INLINE")
public inline fun Iterable<EventProvider>.filterIsQQGuildBotManagers(): List<QQGuildBotManager> =
    filterIsInstance<QQGuildBotManager>()

/**
 * 从序列中过滤出 [QQGuildBotManager] 实例.
 */
@Suppress("NOTHING_TO_INLINE")
public inline fun Sequence<EventProvider>.filterIsQQGuildBotManagers(): Sequence<QQGuildBotManager> =
    filterIsInstance<QQGuildBotManager>()

/**
 * 从中过滤取出所有 [QQGuildBotManager] 实例。
 *
 * @throws NoSuchElementException 如果不存在
 */
@Suppress("NOTHING_TO_INLINE")
public inline fun Iterable<EventProvider>.firstQQGuildBotManager(): QQGuildBotManager =
    first { it is QQGuildBotManager } as QQGuildBotManager

/**
 * 从序列中过滤出 [QQGuildBotManager] 实例。
 *
 * @throws NoSuchElementException 如果不存在
 */
@Suppress("NOTHING_TO_INLINE")
public inline fun Sequence<EventProvider>.firstQQGuildBotManager(): QQGuildBotManager =
    first { it is QQGuildBotManager } as QQGuildBotManager

/**
 * 从中过滤取出所有 [QQGuildBotManager] 实例。
 * 如果不存在则得到null。
 */
@Suppress("NOTHING_TO_INLINE")
public inline fun Iterable<EventProvider>.firstQQGuildBotManagerOrNull(): QQGuildBotManager? =
    firstOrNull { it is QQGuildBotManager } as QQGuildBotManager?

/**
 * 从序列中过滤出 [QQGuildBotManager] 实例。
 * 如果不存在则得到null。
 */
@Suppress("NOTHING_TO_INLINE")
public inline fun Sequence<EventProvider>.firstQQGuildBotManagerOrNull(): QQGuildBotManager? =
    firstOrNull { it is QQGuildBotManager } as QQGuildBotManager?
