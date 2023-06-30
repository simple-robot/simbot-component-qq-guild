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

package love.forte.simbot.qguild.api

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.*
import kotlinx.coroutines.future.asCompletableFuture
import kotlinx.serialization.StringFormat
import kotlinx.serialization.json.Json
import love.forte.simbot.qguild.Api4J
import love.forte.simbot.qguild.ErrInfo
import love.forte.simbot.qguild.InternalApi
import java.util.concurrent.CompletableFuture

/**
 * 用于多平台实现的最小目标。
 *
 * 提供兼容Java的blocking/async函数
 */
@InternalApi
public actual abstract class PlatformQQGuildApi<out R> actual constructor() {

    /**
     * 使用此api发起一次请求，并得到预期中的结果。     *
     * @see ErrInfo
     *
     * @throws Exception see [HttpClient.request], 可能会抛出任何ktor请求过程中的异常。
     * @throws love.forte.simbot.qguild.QQGuildApiException 请求过程中出现了错误。
     */
    @JvmSynthetic
    public actual abstract suspend fun doRequest(
        client: HttpClient,
        server: Url,
        token: String,
        decoder: StringFormat
    ): R


    /**
     * 使用此api发起一次请求，并得到预期中的结果。     *
     * @see ErrInfo
     *
     * @throws Exception see [HttpClient.request], 可能会抛出任何ktor请求过程中的异常。
     * @throws love.forte.simbot.qguild.QQGuildApiException 请求过程中出现了错误。
     */
    @JvmSynthetic
    public actual abstract suspend fun doRequestRaw(
        client: HttpClient,
        server: Url,
        token: String,
    ): String


    /**
     * 使用此api发起一次请求，并得到预期中的结果。
     *
     * 为Java服务的阻塞API，内部使用 [runBlocking] 阻塞并等待 [doRequest] 的结果。
     * 其中，阻塞代码块上下文的调度器为 [Dispatchers.IO]。
     *
     * @see ErrInfo
     *
     * @throws Exception see [HttpClient.request], 可能会抛出任何ktor请求过程中的异常。
     * @throws love.forte.simbot.qguild.QQGuildApiException 请求过程中出现了错误。
     */
    @Api4J
    @JvmOverloads
    public fun doRequestBlocking(
        client: HttpClient = QQGuildApi.DefaultHttpClient,
        server: Url,
        token: String,
        decoder: StringFormat = Json,
    ): R = runBlocking(Dispatchers.IO) {
        doRequest(client, server, token, decoder)
    }


    /**
     * 使用此api发起一次请求，并得到预期中的结果。
     *
     * 为Java服务的异步API，内部使用 [CoroutineScope.async] 异步执行 [doRequest] 并返回
     * [CompletableFuture] 结果。
     *
     * @see ErrInfo
     *
     * @throws Exception see [HttpClient.request], 可能会抛出任何ktor请求过程中的异常。
     * @throws love.forte.simbot.qguild.QQGuildApiException 请求过程中出现了错误。
     */
    @Api4J
    @JvmOverloads
    public fun doRequestAsync(
        client: HttpClient = QQGuildApi.DefaultHttpClient,
        server: Url,
        token: String,
        decoder: StringFormat = Json,
    ): CompletableFuture<out R> {
        return client.async(COROUTINE_NAME) { doRequest(client, server, token, decoder) }.asCompletableFuture()
    }

    /**
     * 使用此api发起一次请求，并得到预期中的结果。
     *
     * 为Java服务的阻塞API，内部使用 [runBlocking] 阻塞并等待 [doRequest] 的结果。
     * 其中，阻塞代码块上下文的调度器为 [Dispatchers.IO]。
     *
     * @see ErrInfo
     *
     * @throws Exception see [HttpClient.request], 可能会抛出任何ktor请求过程中的异常。
     * @throws love.forte.simbot.qguild.QQGuildApiException 请求过程中出现了错误。
     */
    @Api4J
    @JvmOverloads
    public fun doRequestRawBlocking(
        client: HttpClient = QQGuildApi.DefaultHttpClient,
        server: Url,
        token: String,
    ): String = runBlocking(Dispatchers.IO) {
        doRequestRaw(client, server, token)
    }


    /**
     * 使用此api发起一次请求，并得到预期中的结果。
     *
     * 为Java服务的异步API，内部使用 [CoroutineScope.async] 异步执行 [doRequest] 并返回
     * [CompletableFuture] 结果。
     *
     * @see ErrInfo
     *
     * @throws Exception see [HttpClient.request], 可能会抛出任何ktor请求过程中的异常。
     * @throws love.forte.simbot.qguild.QQGuildApiException 请求过程中出现了错误。
     */
    @Api4J
    @JvmOverloads
    public fun doRequestRawAsync(
        client: HttpClient = QQGuildApi.DefaultHttpClient,
        server: Url,
        token: String,
    ): CompletableFuture<out String> {
        return client.async(COROUTINE_NAME) { doRequestRaw(client, server, token) }.asCompletableFuture()
    }


    public actual companion object {
        private val COROUTINE_NAME = CoroutineName("Api-Async-Scope")
    }
}


// 急切初始化
private val logNameProcessor: HttpMethod.() -> String = run {
    // default true
    val enable = System.getProperty("simbot.qguild.api.logger.color.enable")?.toBoolean() ?: true

    // 以 DELETE 为最长标准
    // API基本不存在 HEAD OPTIONS

    if (enable) {
        {
            when (this) {
                /*
                    GET        绿色
                    POST       蓝色
                    PUT, PATCH 紫色
                    DELETE     红色
                 */
                HttpMethod.Get ->     "\u001B[32m   GET\u001B[0m"
                HttpMethod.Post ->    "\u001B[34m  POST\u001B[0m"
                HttpMethod.Put ->     "\u001B[35m   PUT\u001B[0m"
                HttpMethod.Patch ->   "\u001B[35m PATCH\u001B[0m"
                HttpMethod.Delete ->  "\u001B[31mDELETE\u001B[0m"
                else -> value
            }
        }
    } else {
        {
            defaultForLogName()
        }
    }
}

/**
 * 日志对齐
 */
@PublishedApi
internal actual val HttpMethod.logName: String
    get() = logNameProcessor()
