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

@file:JvmName("BotRequestUtil")

package love.forte.simbot.qguild

import kotlinx.coroutines.future.future
import love.forte.simbot.qguild.api.QQGuildApi
import java.util.concurrent.CompletableFuture


/**
 * @see requestBlocking
 * @suppress use [requestBlocking] or [requestAsync]
 * @throws love.forte.simbot.qguild.QQGuildApiException 如果返回状态码不在 200..300之间。
 */
@Api4J
@Deprecated("Use 'requestBlocking'", ReplaceWith("requestBlocking(bot, api)"))
public fun <R> doRequest(bot: Bot, api: QQGuildApi<R>): R = requestBlocking(bot, api)

/**
 * 直接通过bot进行阻塞地请求。
 *
 * @throws love.forte.simbot.qguild.QQGuildApiException 如果返回状态码不在 200..300之间。
 */
@Api4J
public fun <R> requestBlocking(bot: Bot, api: QQGuildApi<R>): R = runInNoScopeBlocking {
    api.requestBy(bot)
}

/**
 * 直接通过bot进行异步地请求。
 *
 * @throws love.forte.simbot.qguild.QQGuildApiException 如果返回状态码不在 200..300之间。
 */
@Api4J
public fun <R> requestAsync(bot: Bot, api: QQGuildApi<R>): CompletableFuture<out R> = bot.future {
    api.requestBy(bot)
}

/**
 * 直接通过bot进行阻塞地请求。
 *
 * @throws love.forte.simbot.qguild.QQGuildApiException 如果返回状态码不在 200..300之间。
 */
@Api4J
public fun requestRawBlocking(bot: Bot, api: QQGuildApi<*>): String = runInNoScopeBlocking {
    api.requestRawBy(bot)
}

/**
 * 直接通过bot进行异步地请求。
 *
 * @throws love.forte.simbot.qguild.QQGuildApiException 如果返回状态码不在 200..300之间。
 */
@Api4J
public fun requestRawAsync(bot: Bot, api: QQGuildApi<*>): CompletableFuture<out String> = bot.future {
    api.requestRawBy(bot)
}
