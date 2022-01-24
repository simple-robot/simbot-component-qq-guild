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
