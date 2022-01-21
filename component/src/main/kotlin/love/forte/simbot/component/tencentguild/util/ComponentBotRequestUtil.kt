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
