/*
 * Copyright (c) 2023-2024. ForteScarlet.
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

@file:JvmName("BotRequests")
@file:JvmMultifileClass

package love.forte.simbot.qguild

import io.ktor.client.statement.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.future.future
import love.forte.simbot.annotations.Api4J
import love.forte.simbot.qguild.api.QQGuildApi
import love.forte.simbot.suspendrunner.runInNoScopeBlocking
import java.util.concurrent.CompletableFuture


/**
 * 直接通过bot进行请求。
 *
 * 异步请求使用的作用域 [scope] 默认为 [Bot.apiClient]。
 */
@Api4J
@JvmOverloads
public fun Bot.requestAsync(api: QQGuildApi<*>, scope: CoroutineScope? = null): CompletableFuture<HttpResponse> =
    (scope ?: apiClient).future {
        request(api)
    }

/**
 * 直接通过bot进行请求。
 *
 * 异步请求使用的作用域 [scope] 默认为 [Bot.apiClient]。
 */
@Api4J
@JvmOverloads
public fun Bot.requestTextAsync(api: QQGuildApi<*>, scope: CoroutineScope? = null): CompletableFuture<String> =
    (scope ?: apiClient).future {
        requestText(api)
    }

/**
 * 直接通过bot进行请求。
 *
 * 异步请求使用的作用域 [scope] 默认为 [Bot.apiClient]。
 *
 * @throws love.forte.simbot.qguild.QQGuildApiException 如果返回状态码不在 200..300之间。
 */
@Api4J
@JvmOverloads
public fun <R : Any> Bot.requestDataAsync(
    api: QQGuildApi<R>,
    scope: CoroutineScope? = null
): CompletableFuture<R> = (scope ?: apiClient).future {
    requestData(api)
}

/**
 * 直接通过bot进行请求。
 */
@Api4J
public fun Bot.requestByBlocking(api: QQGuildApi<*>): HttpResponse = runInNoScopeBlocking {
    request(api)
}

/**
 * 直接通过bot进行请求。
 */
@Api4J
public fun Bot.requestTextByBlocking(api: QQGuildApi<*>): String = runInNoScopeBlocking {
    requestText(api)
}

/**
 * 直接通过bot进行请求。
 *
 * @throws love.forte.simbot.qguild.QQGuildApiException 如果返回状态码不在 200..300之间。
 */
@Api4J
public fun <R : Any> Bot.requestDataByBlocking(api: QQGuildApi<R>): R = runInNoScopeBlocking {
    requestData(api)
}
