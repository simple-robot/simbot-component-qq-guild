/*
 * Copyright (c) 2022-2023. ForteScarlet.
 *
 * This file is part of simbot-component-tencent-guild.
 *
 * simbot-component-tencent-guild is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * simbot-component-tencent-guild is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with simbot-component-tencent-guild. If not, see <https://www.gnu.org/licenses/>.
 */

package love.forte.simbot.tencentguild.api.user

import kotlinx.serialization.DeserializationStrategy
import love.forte.simbot.tencentguild.api.GetTencentApi
import love.forte.simbot.tencentguild.api.RouteInfoBuilder
import love.forte.simbot.tencentguild.model.User


/**
 *
 * [获取用户详情](https://bot.q.qq.com/wiki/develop/api/openapi/user/me.html)
 *
 * 用于获取当前用户（机器人）详情。
 *
 * @author ForteScarlet
 */
public object GetBotInfoApi : GetTencentApi<User>() {
    // GET /users/@me
    private val path = arrayOf("users", "@me")
    override val resultDeserializer: DeserializationStrategy<User> = User.serializer()
    override fun route(builder: RouteInfoBuilder) {
        builder.apiPath = path
    }
}
