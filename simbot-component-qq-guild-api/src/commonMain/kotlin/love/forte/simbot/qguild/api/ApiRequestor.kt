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


package love.forte.simbot.qguild.api

import io.ktor.client.*
import io.ktor.http.*
import kotlinx.serialization.StringFormat
import love.forte.simbot.logger.LoggerFactory
import love.forte.simbot.qguild.QQGuild
import kotlin.jvm.JvmSynthetic

internal val apiRequestorLogger = LoggerFactory.getLogger("love.forte.simbot.qguild.api.ApiRequestor")

/**
 *
 * 通过提供的参数，对此api进行请求并得到最终结果。
 *
 * @param client 提供一个 http client，默认使用 [QQGuildApi.DefaultHttpClient]。
 * @param server 提供一个目标服务器路径。See also: [QQGuild.URL]、[QQGuild.SANDBOX_URL]
 * @param token 提供一个 token.
 * @param decoder 如果有，提供一个decoder用于通过 [QQGuildApi.resultDeserializer] 进行反序列化，而不是通过 [client] 进行。
 *
 * @throws love.forte.simbot.qguild.QQGuildApiException 如果响应码不在 200..300 范围内。
 *
 */
@JvmSynthetic
public suspend fun <R> QQGuildApi<R>.request(
    client: HttpClient = QQGuildApi.DefaultHttpClient,
    server: Url,
    token: String,
    decoder: StringFormat = QQGuildApi.DefaultJsonDecoder,
): R {
    return doRequest(client, server, token, decoder)
}
/**
 *
 * 通过提供的参数，对此api进行请求并得到最终结果。
 *
 * @param client 提供一个 http client，默认使用 [QQGuildApi.DefaultHttpClient]。
 * @param server 提供一个目标服务器路径。See also: [QQGuild.URL]、[QQGuild.SANDBOX_URL]
 * @param token 提供一个 token.
 *
 * @throws love.forte.simbot.qguild.QQGuildApiException 如果响应码不在 200..300 范围内。
 *
 */
@JvmSynthetic
public suspend fun QQGuildApi<*>.requestRaw(
    client: HttpClient = QQGuildApi.DefaultHttpClient,
    server: Url,
    token: String,
): String {
    return doRequestRaw(client, server, token)
}


