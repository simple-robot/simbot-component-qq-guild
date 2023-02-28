package love.forte.simbot.tencentguild.api.channel.permissions

import io.ktor.http.*
import kotlinx.serialization.Serializable
import love.forte.simbot.tencentguild.api.RouteInfoBuilder
import love.forte.simbot.tencentguild.api.TencentApiWithoutResult
import love.forte.simbot.tencentguild.model.Permissions


/**
 * [修改子频道身份组权限](https://bot.q.qq.com/wiki/develop/api/openapi/channel_permissions/put_channel_roles_permissions.html)
 *
 * 用于修改子频道 `channel_id` 下身份组 `role_id` 的权限。
 *
 * - 要求操作人具有管理子频道的权限，如果是机器人，则需要将机器人设置为管理员。
 * - 参数包括 `add` 和 `remove` 两个字段，分别表示授予的权限以及删除的权限。
 * 要授予身份组权限即把 `add` 对应位置 1，删除身份组权限即把 `remove` 对应位置 1。当两个字段同一位都为 1，表现为删除权限。
 * - 本接口不支持修改 `可管理子频道` 权限。
 *
 * @author ForteScarlet
 */
public class ModifyChannelRolePermissionsApi private constructor(
    channelId: String, roleId: String,
    private val _body: Body
) : TencentApiWithoutResult() {
    public companion object Factory {

        /**
         * 构造 [ModifyChannelRolePermissionsApi]
         */
        @JvmStatic
        @JvmName("create") // support value class for Java
        public fun create(
            channelId: String,
            roleId: String,
            add: Permissions? = null,
            remove: Permissions? = null
        ): ModifyChannelRolePermissionsApi =
            ModifyChannelRolePermissionsApi(channelId, roleId, Body(add, remove))


        /**
         * 构造 [ModifyChannelRolePermissionsApi].
         * @param add 需要追加的权限的位图
         */
        @JvmStatic
        @JvmName("createForAdd") // support value class for Java
        public fun createForAdd(
            channelId: String, memberId: String, add: Permissions
        ): ModifyChannelRolePermissionsApi = create(channelId, memberId, add = add)

        /**
         * 构造 [ModifyChannelRolePermissionsApi].
         * @param remove 需需要移除的权限的位图
         */
        @JvmStatic
        @JvmName("createForRemove") // support value class for Java
        public fun createForRemove(
            channelId: String, memberId: String, remove: Permissions
        ): ModifyChannelRolePermissionsApi = create(channelId, memberId, remove = remove)
    }

    // PUT /channels/{channel_id}/roles/{role_id}/permissions
    private val path = arrayOf("channels", channelId, "roles", roleId, "permissions")

    override fun route(builder: RouteInfoBuilder) {
        builder.apiPath = path
    }

    override val method: HttpMethod
        get() = HttpMethod.Put

    override val body: Any
        get() = _body

    @Serializable
    private data class Body(val add: Permissions?, val remove: Permissions?) {
        init {
            require(add != null || remove != null) {
                "At least one of the parameters should not be null"
            }
        }
    }
}
