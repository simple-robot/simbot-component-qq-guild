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