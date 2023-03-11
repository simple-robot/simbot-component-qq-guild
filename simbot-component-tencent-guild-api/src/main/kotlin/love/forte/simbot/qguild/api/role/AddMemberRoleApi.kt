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

package love.forte.simbot.qguild.api.role

import io.ktor.http.*
import kotlinx.serialization.Serializable
import love.forte.simbot.qguild.api.RouteInfoBuilder
import love.forte.simbot.qguild.api.SimpleApiDescription
import love.forte.simbot.qguild.api.TencentApiWithoutResult

/**
 *
 * [增加频道身份组成员](https://bot.q.qq.com/wiki/develop/api/openapi/guild/put_guild_member_role.html)
 *
 * 用于将频道 `guild_id` 下的用户 `user_id` 添加到身份组 `role_id` 。
 *
 * - 需要使用的 `token` 对应的用户具备增加身份组成员权限。如果是机器人，要求被添加为管理员。
 * - 如果要增加的身份组 `ID` 是 [`5-子频道管理员`][love.forte.simbot.qguild.model.Role.DEFAULT_ID_CHANNEL_ADMIN]，
 * 需要增加 `channel` 对象来指定具体是哪个子频道。
 *
 * @author ForteScarlet
 */
public class AddMemberRoleApi private constructor(
    guildId: String,
    userId: String,
    roleId: String,
    channelId: String?,
) : TencentApiWithoutResult() {
    public companion object Factory : SimpleApiDescription(
        HttpMethod.Put, "/guilds/{guild_id}/members/{user_id}/roles/{role_id}"
    ) {

        /**
         * 构造 [AddMemberRoleApi]
         *
         * @param channelId 如果要增加的身份组 [roleId] 是 [`5-子频道管理员`][love.forte.simbot.qguild.model.Role.DEFAULT_ID_CHANNEL_ADMIN]，
         * 需要增加 `channel` 对象来指定具体是哪个子频道。
         */
        @JvmStatic
        @JvmOverloads
        public fun create(
            guildId: String,
            userId: String,
            roleId: String,
            channelId: String? = null
        ): AddMemberRoleApi =
            AddMemberRoleApi(guildId, userId, roleId, channelId)
    }

    private val path = arrayOf(
        "guilds", guildId,
        "members", userId,
        "roles", roleId,
    )

    override val method: HttpMethod
        get() = HttpMethod.Put

    override fun route(builder: RouteInfoBuilder) {
        builder.apiPath = path
    }

    override val body: Any? = channelId?.let { cid -> Body(ChannelId(cid)) }

    @Serializable
    private data class Body(val channel: ChannelId)

    @Serializable
    private data class ChannelId(val id: String)

}
