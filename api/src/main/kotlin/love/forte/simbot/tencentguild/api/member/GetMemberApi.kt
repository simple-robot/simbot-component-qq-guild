package love.forte.simbot.tencentguild.api.member

import kotlinx.serialization.DeserializationStrategy
import love.forte.simbot.ID
import love.forte.simbot.tencentguild.TencentMemberInfo
import love.forte.simbot.tencentguild.api.GetTencentApi
import love.forte.simbot.tencentguild.api.RouteInfoBuilder


/**
 * [获取某个成员信息](https://bot.q.qq.com/wiki/develop/api/openapi/member/get_member.html)
 *
 */
public class GetMemberApi(
    guildId: ID,
    userId: ID
) : GetTencentApi<TencentMemberInfo>() {
    // GET /guilds/{guild_id}/members/{user_id}
    private val path = listOf("guilds", guildId.toString(), "members", userId.toString())

    override val resultDeserializer: DeserializationStrategy<out TencentMemberInfo>
        get() = TencentMemberInfo.serializer

    override fun route(builder: RouteInfoBuilder) {
        builder.apiPath = path
    }
}