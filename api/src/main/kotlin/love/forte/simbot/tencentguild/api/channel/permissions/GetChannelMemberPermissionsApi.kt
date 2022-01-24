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