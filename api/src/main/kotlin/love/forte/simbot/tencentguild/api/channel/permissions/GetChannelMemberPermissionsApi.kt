package love.forte.simbot.tencentguild.api.channel.permissions

import kotlinx.serialization.DeserializationStrategy
import love.forte.simbot.ID
import love.forte.simbot.tencentguild.TencentChannelPermissionsInfo
import love.forte.simbot.tencentguild.api.GetTencentApi
import love.forte.simbot.tencentguild.api.RouteInfoBuilder

/**
 *
 * [获取指定子频道的权限](https://bot.q.qq.com/wiki/develop/api/openapi/channel_permissions/get_channel_permissions.html)
 * @author ForteScarlet
 */
public class GetChannelMemberPermissionsApi(
    channelId: ID,
    memberId: ID
) : GetTencentApi<TencentChannelPermissionsInfo>() {

    // GET /channels/{channel_id}/members/{user_id}/permissions
    private val path = listOf("channels", channelId.toString(), "members", memberId.toString(), "permissions")

    override val resultDeserializer: DeserializationStrategy<out TencentChannelPermissionsInfo>
        get() = TencentChannelPermissionsInfo.serializer

    override fun route(builder: RouteInfoBuilder) {
        builder.apiPath = path
    }
}