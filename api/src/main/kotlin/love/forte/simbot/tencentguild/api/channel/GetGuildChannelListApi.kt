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

package love.forte.simbot.tencentguild.api.channel

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.builtins.ListSerializer
import love.forte.simbot.ID
import love.forte.simbot.tencentguild.TencentChannelInfo
import love.forte.simbot.tencentguild.api.GetTencentApi
import love.forte.simbot.tencentguild.api.RouteInfoBuilder


/**
 *
 * [获取频道下的子频道列表](https://bot.q.qq.com/wiki/develop/api/openapi/channel/get_channels.html)
 *
 * @author ForteScarlet
 */
public class GetGuildChannelListApi(guildId: ID) : GetTencentApi<List<TencentChannelInfo>>() {
    // GET /guilds/{guild_id}/channels
    private val path = listOf("guilds", guildId.toString(), "channels")

    override val resultDeserializer: DeserializationStrategy<out List<TencentChannelInfo>>
        get() = serializer

    override fun route(builder: RouteInfoBuilder) {
        builder.apiPath = path
    }

    public companion object {
        private val serializer = ListSerializer(TencentChannelInfo.serializer)
    }
}