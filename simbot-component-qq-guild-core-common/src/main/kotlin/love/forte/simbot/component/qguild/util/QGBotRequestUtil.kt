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

@file:JvmName("QGBotRequestUtil")

package love.forte.simbot.component.qguild.util

import kotlinx.coroutines.future.future
import love.forte.simbot.Api4J
import love.forte.simbot.component.qguild.QGBot
import love.forte.simbot.qguild.api.QQGuildApi
import love.forte.simbot.qguild.request
import love.forte.simbot.qguild.requestBy
import love.forte.simbot.qguild.requestRaw
import love.forte.simbot.qguild.requestRawBy
import love.forte.simbot.utils.runInNoScopeBlocking
import java.util.concurrent.CompletableFuture


/**
 * 直接通过bot进行请求。
 *
 * @throws love.forte.simbot.qguild.QQGuildApiException 如果返回状态码不在 200..300之间。
 */
@Api4J
public fun <R> QQGuildApi<R>.requestBlockingBy(bot: QGBot): R = runInNoScopeBlocking {
    requestBy(bot.source)
}

/**
 * 直接通过bot进行请求。
 *
 * @throws love.forte.simbot.qguild.QQGuildApiException 如果返回状态码不在 200..300之间。
 */
@Api4J
public fun <R> QQGuildApi<R>.requestAsyncBy(bot: QGBot): CompletableFuture<out R> = bot.source.future {
    requestBy(bot.source)
}

/**
 * 直接通过bot进行请求。
 *
 * @throws love.forte.simbot.qguild.QQGuildApiException 如果返回状态码不在 200..300之间。
 */
@Api4J
public fun <R> QGBot.requestBlocking(api: QQGuildApi<R>): R = runInNoScopeBlocking {
    source.request(api)
}

/**
 * 直接通过bot进行请求。
 *
 * @throws love.forte.simbot.qguild.QQGuildApiException 如果返回状态码不在 200..300之间。
 */
@Api4J
public fun <R> QGBot.requestAsync(api: QQGuildApi<R>): CompletableFuture<out R> = source.future {
    source.request(api)
}

/**
 * 直接通过bot进行请求。
 *
 * @throws love.forte.simbot.qguild.QQGuildApiException 如果返回状态码不在 200..300之间。
 */
@Api4J
public fun QGBot.requestRawBlocking(api: QQGuildApi<*>): String = runInNoScopeBlocking {
    source.requestRaw(api)
}

/**
 * 直接通过bot进行请求。
 *
 * @throws love.forte.simbot.qguild.QQGuildApiException 如果返回状态码不在 200..300之间。
 */
@Api4J
public fun QGBot.requestRawAsync(api: QQGuildApi<*>): CompletableFuture<out String> = source.future {
    source.requestRaw(api)
}

/**
 * 直接通过bot进行请求。
 *
 * @throws love.forte.simbot.qguild.QQGuildApiException 如果返回状态码不在 200..300之间。
 */
@Api4J
public fun QQGuildApi<*>.requestRawBlockingBy(bot: QGBot): String = runInNoScopeBlocking {
    requestRawBy(bot.source)
}

/**
 * 直接通过bot进行请求。
 *
 * @throws love.forte.simbot.qguild.QQGuildApiException 如果返回状态码不在 200..300之间。
 */
@Api4J
public fun QQGuildApi<*>.requestRawAsyncBy(bot: QGBot): CompletableFuture<out String> = bot.source.future {
    requestRawBy(bot.source)
}
