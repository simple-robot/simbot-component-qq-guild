/*
 *  Copyright (c) 2022-2022 ForteScarlet <ForteScarlet@163.com>
 *
 *  本文件是 simbot-component-tencent-guild 的一部分。
 *
 *  simbot-component-tencent-guild 是自由软件：你可以再分发之和/或依照由自由软件基金会发布的 GNU 通用公共许可证修改之，无论是版本 3 许可证，还是（按你的决定）任何以后版都可以。
 *
 *  发布 simbot-component-tencent-guild 是希望它能有用，但是并无保障;甚至连可销售和符合某个特定的目的都不保证。请参看 GNU 通用公共许可证，了解详情。
 *
 *  你应该随程序获得一份 GNU 通用公共许可证的复本。如果没有，请看:
 *  https://www.gnu.org/licenses
 *  https://www.gnu.org/licenses/gpl-3.0-standalone.html
 *  https://www.gnu.org/licenses/lgpl-3.0-standalone.html
 *
 *
 */

@file:JvmName("BotRequestUtil")

package love.forte.simbot.tencentguild

import kotlinx.coroutines.*
import love.forte.simbot.*
import love.forte.simbot.tencentguild.api.*


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
@OptIn(InternalSrTcgApi::class)
@Api4J
public fun <R> doRequest(bot: TencentGuildBot, api: TencentApi<R>): R = runBlocking {
    api.requestBy(bot)
}

