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
import kotlinx.serialization.StringFormat
import love.forte.simbot.qguild.ErrInfo
import love.forte.simbot.qguild.InternalApi


/**
 * 用于多平台实现的最小目标。
 */
@InternalApi
public actual abstract class PlatformQQGuildApi<out R> actual constructor() {

    /**
     * 使用此api发起一次请求，并得到预期中的结果。如果返回了代表错误的响应值
     *
     * @see love.forte.simbot.qguild.ErrInfo
     *
     * @throws Exception see [HttpClient.request], 可能会抛出任何ktor请求过程中的异常。
     * @throws love.forte.simbot.qguild.QQGuildApiException 请求过程中出现了错误。
     */
    public actual abstract suspend fun doRequest(
        client: HttpClient,
        server: Url,
        token: String,
        decoder: StringFormat
    ): R


    /**
     * 使用此api发起一次请求，并得到响应结果的字符串。
     *
     * @see ErrInfo
     *
     * @throws Exception see [HttpClient.request], 可能会抛出任何ktor请求过程中的异常。
     * @throws love.forte.simbot.qguild.QQGuildApiException 请求过程中出现了错误（http状态码 !in 200 .. 300）
     */
    public actual abstract suspend fun doRequestRaw(
        client: HttpClient,
        server: Url,
        token: String,
    ): String

    public actual companion object
}


/**
 * 日志对齐
 */
internal actual val HttpMethod.logName: String
    get() = defaultForLogName()
