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

package love.forte.simbot.tencentguild.api.user

import kotlinx.serialization.DeserializationStrategy
import love.forte.simbot.tencentguild.TencentBotInfo
import love.forte.simbot.tencentguild.api.GetTencentApi
import love.forte.simbot.tencentguild.api.RouteInfoBuilder


/**
 *
 * [获取当前用户信息](https://bot.q.qq.com/wiki/develop/api/openapi/user/me.html)
 * @author ForteScarlet
 */
public object GetBotInfoApi : GetTencentApi<TencentBotInfo>() {
    // GET /users/@me
    private val path = listOf("users", "@me")
    override val resultDeserializer: DeserializationStrategy<out TencentBotInfo> = TencentBotInfo.serializer
    override fun route(builder: RouteInfoBuilder) {
        builder.apiPath = path
    }
}