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

@file:JvmName("ApiRequestUtil")

package love.forte.simbot.qguild.api

import io.ktor.client.*
import io.ktor.http.*
import kotlinx.serialization.StringFormat
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonBuilder
import love.forte.simbot.Api4J
import love.forte.simbot.logger.LoggerFactory
import love.forte.simbot.utils.runInNoScopeBlocking
import java.util.function.Consumer

internal val logger = LoggerFactory.getLogger("love.forte.simbot.tencentguild.api.request")

/**
 *
 * 通过提供的参数，对此api进行请求并得到最终结果。
 *
 * @param client 提供一个 http client
 * @param server 提供一个目标服务器路径
 * @param token 提供一个 token.
 * @param decoder 如果有，提供一个decoder用于通过 [QQGuildApi.resultDeserializer] 进行反序列化，而不是通过 [client] 进行。
 *
 * @throws love.forte.simbot.qguild.TencentApiException 如果响应码不在 200..300 范围内。
 */
@JvmSynthetic
public suspend fun <R> QQGuildApi<R>.request(
    client: HttpClient,
    server: Url,
    token: String,
    decoder: StringFormat = defaultJson,
): R {
    return doRequest(client, server, token, decoder)
}


/**
 * [request] for Java
 */
@Api4J
@JvmOverloads
public fun <R> doRequest(
    api: QQGuildApi<R>,
    client: HttpClient,
    server: String,
    token: String,
    decoder: StringFormat = defaultJson,
): R = runInNoScopeBlocking {
    api.request(client, Url(server), token, decoder)
}

@OptIn(Api4J::class)
private val defaultJson = newJson()

@Api4J
public fun newHttpClient(): HttpClient = HttpClient()

@Api4J
@JvmOverloads
public fun newJson(
    build: Consumer<JsonBuilder> = Consumer {
        it.apply {
            isLenient = true
            ignoreUnknownKeys = true
        }
    },
): Json = Json {
    build.accept(this)
}
