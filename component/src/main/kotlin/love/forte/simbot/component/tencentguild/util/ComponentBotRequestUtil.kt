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

package love.forte.simbot.component.tencentguild.util

import love.forte.simbot.component.tencentguild.TencentGuildBot
import love.forte.simbot.tencentguild.api.TencentApi
import love.forte.simbot.tencentguild.request


/**
 * 直接通过bot进行请求。
 *
 * @throws love.forte.simbot.tencentguild.TencentApiException 如果返回状态码不在 200..300之间。
 */
@JvmSynthetic
public suspend inline fun <R> TencentApi<R>.request(bot: TencentGuildBot): R {
    return request(bot.sourceBot)
}
