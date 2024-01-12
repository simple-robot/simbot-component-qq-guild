/*
 * Copyright (c) 2022-2024. ForteScarlet.
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

package love.forte.simbot.qguild.stdlib

import io.ktor.client.statement.*
import love.forte.simbot.qguild.api.QQGuildApi
import love.forte.simbot.qguild.api.request
import love.forte.simbot.qguild.api.requestData
import love.forte.simbot.qguild.api.requestText
import love.forte.simbot.qguild.stdlib.internal.BotImpl
import kotlin.jvm.JvmMultifileClass
import kotlin.jvm.JvmName
import kotlin.jvm.JvmSynthetic


/**
 * 直接通过bot进行请求。
 */
@JvmSynthetic
public suspend fun QQGuildApi<*>.requestBy(bot: Bot): HttpResponse {
    val botToken = bot.botToken()
    return request(
        client = bot.apiClient,
        token = botToken,
        server = bot.apiServer,
    )
}


/**
 * 直接通过bot进行请求。
 */
@JvmSynthetic
public suspend inline fun QQGuildApi<*>.requestTextBy(
    bot: Bot,
    useResp: (HttpResponse) -> Unit = {}
): String {
    val botToken = bot.botToken()
    return requestText(
        client = bot.apiClient,
        token = botToken,
        server = bot.apiServer,
        useResp = useResp
    )
}

/**
 * 直接通过bot进行请求。
 *
 * @throws love.forte.simbot.qguild.QQGuildApiException 如果返回状态码不在 200..300之间。
 */
@JvmSynthetic
public suspend inline fun <R : Any> QQGuildApi<R>.requestDataBy(bot: Bot): R {
    val botToken = bot.botToken()
    return requestData(
        client = bot.apiClient,
        token = botToken,
        server = bot.apiServer,
        decoder = bot.apiDecoder
    )
}

/**
 * 直接通过bot进行请求。
 */
@JvmSynthetic
public suspend fun Bot.request(api: QQGuildApi<*>): HttpResponse = api.requestBy(this)

/**
 * 直接通过bot进行请求。
 */
@JvmSynthetic
public suspend fun Bot.requestText(api: QQGuildApi<*>): String = api.requestTextBy(this)

/**
 * 直接通过bot进行请求。
 *
 * @throws love.forte.simbot.qguild.QQGuildApiException 如果返回状态码不在 200..300之间。
 */
@JvmSynthetic
public suspend fun <R : Any> Bot.requestData(api: QQGuildApi<R>): R = api.requestDataBy(this)


@PublishedApi
internal fun Bot.botToken(): String =
    if (this is BotImpl) botToken else "Bot ${ticket.appId}.${ticket.token}"
