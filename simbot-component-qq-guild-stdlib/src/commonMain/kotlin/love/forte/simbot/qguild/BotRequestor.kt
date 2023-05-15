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

package love.forte.simbot.qguild

import love.forte.simbot.qguild.api.QQGuildApi
import love.forte.simbot.qguild.api.request
import love.forte.simbot.qguild.api.requestRaw
import love.forte.simbot.qguild.internal.BotImpl
import kotlin.jvm.JvmSynthetic


/**
 * 直接通过bot进行请求。
 *
 * @throws love.forte.simbot.qguild.QQGuildApiException 如果返回状态码不在 200..300之间。
 */
@JvmSynthetic
public suspend fun <R> QQGuildApi<R>.requestBy(bot: Bot): R {
    val botToken = if (bot is BotImpl) bot.botToken else "Bot ${bot.ticket.appId}.${bot.ticket.token}"
    return request(
        client = bot.apiClient,
        server = bot.apiServer,
        token = botToken,
        decoder = bot.apiDecoder,
    )
}


/**
 * 直接通过bot进行请求。
 *
 * @throws love.forte.simbot.qguild.QQGuildApiException 如果返回状态码不在 200..300之间。
 */
@JvmSynthetic
public suspend fun <R> QQGuildApi<R>.requestRawBy(bot: Bot): String {
    val botToken = if (bot is BotImpl) bot.botToken else "Bot ${bot.ticket.appId}.${bot.ticket.token}"
    return requestRaw(
        client = bot.apiClient,
        server = bot.apiServer,
        token = botToken,
    )
}

/**
 * 直接通过bot进行请求。
 *
 * @throws love.forte.simbot.qguild.QQGuildApiException 如果返回状态码不在 200..300之间。
 */
@JvmSynthetic
public suspend fun <R> Bot.request(api: QQGuildApi<R>): R = api.requestBy(this)


/**
 * 直接通过bot进行请求。
 *
 * @throws love.forte.simbot.qguild.QQGuildApiException 如果返回状态码不在 200..300之间。
 */
@JvmSynthetic
public suspend fun <R> Bot.requestRaw(api: QQGuildApi<R>): String = api.requestRawBy(this)



