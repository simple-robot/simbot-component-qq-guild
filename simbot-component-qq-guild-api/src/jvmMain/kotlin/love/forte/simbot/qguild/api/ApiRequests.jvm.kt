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

@file:JvmName("ApiRequests")
@file:JvmMultifileClass

package love.forte.simbot.qguild.api

import io.ktor.client.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonBuilder
import love.forte.simbot.qguild.QGApi4J
import love.forte.simbot.qguild.QQGuild
import love.forte.simbot.suspendrunner.runInNoScopeBlocking
import java.util.function.Consumer


/**
 * 用于在Java中构建一个默认的 [HttpClient]。
 */
@QGApi4J
public fun newHttpClient(): HttpClient = HttpClient()

/**
 * 用于在Java中构建一个 [Json]。
 */
@QGApi4J
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


/**
 * [QQGuildApi.request] for Java
 */
@QGApi4J
@JvmOverloads
public fun <R : Any> QQGuildApi<R>.requestBlocking(
    client: HttpClient,
    token: String,
    server: Url = QQGuild.URL,
    decoder: Json = QQGuild.DefaultJson,
): HttpResponse = runInNoScopeBlocking {
    request(client, token, server)
}
