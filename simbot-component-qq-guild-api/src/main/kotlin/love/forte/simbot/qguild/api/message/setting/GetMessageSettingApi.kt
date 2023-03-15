/*
 * Copyright (c) 2023. ForteScarlet.
 *
 * This file is part of simbot-component-qq-guild.
 *
 * simbot-component-qq-guild is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * simbot-component-qq-guild is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with simbot-component-qq-guild. If not, see <https://www.gnu.org/licenses/>.
 */

package love.forte.simbot.qguild.api.message.setting

import kotlinx.serialization.DeserializationStrategy
import love.forte.simbot.qguild.api.GetQQGuildApi
import love.forte.simbot.qguild.api.RouteInfoBuilder
import love.forte.simbot.qguild.api.SimpleGetApiDescription
import love.forte.simbot.qguild.model.MessageSetting


/**
 * [获取频道消息频率设置](https://bot.q.qq.com/wiki/develop/api/openapi/setting/message_setting.html)
 *
 * 用于获取机器人在频道 `guild_id` 内的消息频率设置。
 * @author ForteScarlet
 */
public class GetMessageSettingApi private constructor(guildId: String) : GetQQGuildApi<MessageSetting>() {
    public companion object Factory : SimpleGetApiDescription(
        "/guilds/{guild_id}/message/setting"
    ) {
        /**
         * 构造 [GetMessageSettingApi]
         *
         */
        @JvmStatic
        public fun create(guildId: String): GetMessageSettingApi = GetMessageSettingApi(guildId)
    }

    private val path = arrayOf("guilds", guildId, "message", "setting")

    override val resultDeserializer: DeserializationStrategy<MessageSetting>
        get() = MessageSetting.serializer()

    override fun route(builder: RouteInfoBuilder) {
        builder.apiPath = path
    }
}
