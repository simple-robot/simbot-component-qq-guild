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

package love.forte.simbot.tencentguild.api.channel.permissions

import io.ktor.http.*
import kotlinx.serialization.Serializable
import love.forte.simbot.tencentguild.api.RouteInfoBuilder
import love.forte.simbot.tencentguild.api.TencentApiWithoutResult
import love.forte.simbot.tencentguild.model.Permissions

/**
 * [修改子频道权限](https://bot.q.qq.com/wiki/develop/api/openapi/channel_permissions/put_channel_permissions.html)
 *
 * 用于修改子频道 `channel_id` 下用户 `user_id` 的权限。
 *
 * - 要求操作人具有 `管理子频道` 的权限，如果是机器人，则需要将机器人设置为管理员。
 * - 参数包括 `add` 和 `remove` 两个字段，分别表示授予的权限以及删除的权限。
 * 要授予用户权限即把 `add` 对应位置 1，删除用户权限即把 `remove` 对应位置 1。当两个字段同一位都为 1，表现为删除权限。
 * - 本接口不支持修改 `可管理子频道` 权限。
 *
 * @author ForteScarlet
 */
public class ModifyChannelMemberPermissionsApi private constructor(
    channelId: String, memberId: String,
    private val _body: Body
) : TencentApiWithoutResult() {
    public companion object Factory {
        /**
         * 构造 [ModifyChannelMemberPermissionsApi].
         * @param add 需要追加的权限的位图
         * @param remove 需要移除的权限的位图
         */
        @JvmStatic
        @JvmName("create") // support value class for Java
        public fun create(
            channelId: String,
            memberId: String,
            add: Permissions? = null,
            remove: Permissions? = null,
        ): ModifyChannelMemberPermissionsApi = ModifyChannelMemberPermissionsApi(channelId, memberId, Body(add, remove))

        /**
         * 构造 [ModifyChannelMemberPermissionsApi].
         * @param add 需要追加的权限的位图
         */
        @JvmStatic
        @JvmName("createForAdd") // support value class for Java
        public fun createForAdd(
            channelId: String, memberId: String, add: Permissions
        ): ModifyChannelMemberPermissionsApi = create(channelId, memberId, add = add)

        /**
         * 构造 [ModifyChannelMemberPermissionsApi].
         * @param remove 需需要移除的权限的位图
         */
        @JvmStatic
        @JvmName("createForRemove") // support value class for Java
        public fun createForRemove(
            channelId: String, memberId: String, remove: Permissions
        ): ModifyChannelMemberPermissionsApi = create(channelId, memberId, remove = remove)
    }


    // GET /channels/{channel_id}/members/{user_id}/permissions
    private val path = arrayOf("channels", channelId, "members", memberId, "permissions")

    override fun route(builder: RouteInfoBuilder) {
        builder.apiPath = path
    }

    override val method: HttpMethod
        get() = HttpMethod.Put

    override val body: Any
        get() = _body

    @Serializable
    private data class Body(
        val add: Permissions?,
        val remove: Permissions?
    ) {
        init {
            require(add != null || remove != null) {
                "At least one of the parameters should not be null"
            }
        }
    }
}
