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

@file:JvmName("ApiRequestUtil")

package love.forte.simbot.qguild.api

import io.ktor.client.*
import io.ktor.http.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.StringFormat
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonBuilder
import love.forte.simbot.qguild.Api4J
import love.forte.simbot.qguild.QQGuild
import love.forte.simbot.utils.runInNoScopeBlocking
import java.util.function.Consumer


private val simbotInClasspath: Boolean by lazy {
    runCatching {
        val runnerClass = Class.forName("love.forte.simbot.utils.BlockingRunnerKt")
        runnerClass.methods.any { it.name == "runInNoScopeBlocking" }.also {
            if (it) {
                logger.debug("simbot-api is contains in current classpath. Blocking request will use simbot runInNoScopeBlocking.")
            } else {
                logger.debug("simbot-api is contains in current classpath, but 'runInNoScopeBlocking' method not found. Blocking request will use runBlocking with Dispatchers.IO")
            }
        }
    }.getOrElse {
        if (it is ClassNotFoundException) {
            logger.debug("simbot-api is not contains in current classpath. Blocking request will use runBlocking with Dispatchers.IO")
        } else {
            logger.debug("simbot-api classpath check failure: {}. Blocking request will use runBlocking with Dispatchers.IO", it.localizedMessage, it)
        }
        false
    }
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
): R {
    val url = when (server) {
        QQGuild.URL_STRING -> QQGuild.URL
        QQGuild.SANDBOX_URL_STRING -> QQGuild.SANDBOX_URL
        else -> Url(server)
    }

    return if (simbotInClasspath) {
        runInNoScopeBlocking {
            api.request(client, url, token, decoder)
        }
    } else {
        runBlocking(Dispatchers.IO) {
            api.request(client, url, token, decoder)
        }
    }
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
