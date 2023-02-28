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

import kotlinx.serialization.DeserializationStrategy
import love.forte.simbot.tencentguild.api.GetTencentApi
import love.forte.simbot.tencentguild.api.RouteInfoBuilder
import love.forte.simbot.tencentguild.model.ChannelPermissions

/**
 *
 * [获取指定子频道的权限](https://bot.q.qq.com/wiki/develop/api/openapi/channel_permissions/get_channel_permissions.html)
 *
 * 用于获取 子频道 `channel_id` 下用户 `user_id` 的权限。
 *
 * - 获取子频道用户权限。
 * - 要求操作人具有管理子频道的权限，如果是机器人，则需要将机器人设置为管理员。
 *
 * @author ForteScarlet
 */
public class GetChannelMemberPermissionsApi private constructor(
    channelId: String, memberId: String
) : GetTencentApi<ChannelPermissions>() {
    public companion object Factory {
        /**
         * 构造 [GetChannelMemberPermissionsApi]
         */
        @JvmStatic
        public fun create(channelId: String, memberId: String): GetChannelMemberPermissionsApi =
            GetChannelMemberPermissionsApi(channelId, memberId)
    }

    // GET /channels/{channel_id}/members/{user_id}/permissions
    private val path = arrayOf("channels", channelId, "members", memberId, "permissions")

    override val resultDeserializer: DeserializationStrategy<ChannelPermissions>
        get() = ChannelPermissions.serializer()

    override fun route(builder: RouteInfoBuilder) {
        builder.apiPath = path
    }
}
