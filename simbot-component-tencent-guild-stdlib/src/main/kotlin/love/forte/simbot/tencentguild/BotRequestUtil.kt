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

@file:JvmName("BotRequestUtil")

package love.forte.simbot.tencentguild

import kotlinx.coroutines.*
import love.forte.simbot.*
import love.forte.simbot.tencentguild.api.*
import love.forte.simbot.utils.runInNoScopeBlocking


/**
 * 直接通过bot进行请求。
 *
 * @throws love.forte.simbot.tencentguild.TencentApiException 如果返回状态码不在 200..300之间。
 */
@JvmSynthetic
@Deprecated("使用更符合语义的Api.requestBy(Bot) 或 Bot.request(Api)", ReplaceWith("requestBy(bot)"))
public suspend fun <R> TencentApi<R>.request(bot: TencentGuildBot): R = requestBy(bot)


/**
 * 直接通过bot进行请求。
 *
 * @throws love.forte.simbot.tencentguild.TencentApiException 如果返回状态码不在 200..300之间。
 */
@JvmSynthetic
public suspend fun <R> TencentApi<R>.requestBy(bot: TencentGuildBot): R {
    return request(
        client = bot.configuration.httpClient,
        server = bot.configuration.serverUrl,
        token = bot.ticket.botToken,
        decoder = bot.configuration.decoder,
    )
}
/**
 * 直接通过bot进行请求。
 *
 * @throws love.forte.simbot.tencentguild.TencentApiException 如果返回状态码不在 200..300之间。
 */
@JvmSynthetic
public suspend fun <R> TencentGuildBot.request(api: TencentApi<R>): R = api.requestBy(this)


/**
 * 直接通过bot进行请求。
 *
 * @throws love.forte.simbot.tencentguild.TencentApiException 如果返回状态码不在 200..300之间。
 */
@Api4J
public fun <R> doRequest(bot: TencentGuildBot, api: TencentApi<R>): R = runInNoScopeBlocking {
    api.requestBy(bot)
}

