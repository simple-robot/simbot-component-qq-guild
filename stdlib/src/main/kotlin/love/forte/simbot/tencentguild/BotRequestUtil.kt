/*
 *  Copyright (c) 2022-2022 ForteScarlet <ForteScarlet@163.com>
 *
 *  根据 GNU LESSER GENERAL PUBLIC LICENSE 3 获得许可；
 *  除非遵守许可，否则您不得使用此文件。
 *  您可以在以下网址获取许可证副本：
 *
 *       https://www.gnu.org/licenses/lgpl-3.0-standalone.html
 *
 *   有关许可证下的权限和限制的具体语言，请参见许可证。
 */

@file:JvmName("BotRequestUtil")

package love.forte.simbot.tencentguild

import kotlinx.coroutines.runBlocking
import love.forte.simbot.Api4J
import love.forte.simbot.tencentguild.api.TencentApi
import love.forte.simbot.tencentguild.api.request



/**
 * 直接通过bot进行请求。
 *
 * @throws love.forte.simbot.tencentguild.TencentApiException 如果返回状态码不在 200..300之间。
 */
@JvmSynthetic
public suspend fun <R> TencentApi<R>.request(bot: TencentBot): R {
    return request(
        client = bot.configuration.httpClient,
        server = bot.configuration.serverUrl,
        token = bot.ticket.botToken,
        decoder = bot.configuration.decoder,
    )
}


@OptIn(InternalSrTcgApi::class)
@Api4J
public fun <R> doRequest(bot: TencentBot, api: TencentApi<R>): R = runBlocking {
    api.request(bot)
}