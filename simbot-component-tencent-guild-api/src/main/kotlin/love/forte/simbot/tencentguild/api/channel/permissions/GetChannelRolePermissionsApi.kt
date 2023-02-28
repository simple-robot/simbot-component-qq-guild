package love.forte.simbot.tencentguild.api.channel.permissions

import kotlinx.serialization.DeserializationStrategy
import love.forte.simbot.tencentguild.api.GetTencentApi
import love.forte.simbot.tencentguild.api.RouteInfoBuilder
import love.forte.simbot.tencentguild.model.ChannelPermissions


/**
 * [获取子频道身份组权限](https://bot.q.qq.com/wiki/develop/api/openapi/channel_permissions/get_channel_roles_permissions.html)
 *
 * 用于获取子频道 `channel_id` 下身份组 `role_id` 的权限。
 *
 * - 要求操作人具有管理子频道的权限，如果是机器人，则需要将机器人设置为管理员。
 *
 * @author ForteScarlet
 */
public class GetChannelRolePermissionsApi(
    channelId: String, roleId: String
) : GetTencentApi<ChannelPermissions>() {
    public companion object Factory {

        /**
         * 构造 [GetChannelRolePermissionsApi]
         */
        @JvmStatic
        public fun create(channelId: String, roleId: String): GetChannelRolePermissionsApi =
            GetChannelRolePermissionsApi(channelId, roleId)

    }

    override val resultDeserializer: DeserializationStrategy<ChannelPermissions>
        get() = ChannelPermissions.serializer()

    // GET /channels/{channel_id}/roles/{role_id}/permissions
    private val path = arrayOf("channels", channelId, "roles", roleId, "permissions")

    override fun route(builder: RouteInfoBuilder) {
        builder.apiPath = path
    }
}
