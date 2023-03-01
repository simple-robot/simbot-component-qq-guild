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

package love.forte.simbot.tencentguild.api.role

import io.ktor.http.*
import love.forte.simbot.tencentguild.api.RouteInfoBuilder
import love.forte.simbot.tencentguild.api.TencentApiWithoutResult

/**
 *
 * [删除频道身份组](https://bot.q.qq.com/wiki/develop/api/openapi/guild/delete_guild_role.html#%E5%88%A0%E9%99%A4%E9%A2%91%E9%81%93%E8%BA%AB%E4%BB%BD%E7%BB%84)
 *
 * 用于删除频道 `guild_id` 下 `role_id` 对应的身份组。
 *
 * 需要使用的 `token` 对应的用户具备删除身份组权限。如果是机器人，要求被添加为管理员。
 * @author ForteScarlet
 */
public class DeleteGuildRoleApi(guildId: String, roleId: String) : TencentApiWithoutResult() {
    public companion object Factory {
        
        /**
         * 构造 [DeleteGuildRoleApi]
         */
        @JvmStatic
        public fun create(guildId: String, roleId: String): DeleteGuildRoleApi = DeleteGuildRoleApi(guildId, roleId)
    }

    // DELETE /guilds/{guild_id}/roles/{role_id}
    private val path = arrayOf("guilds", guildId, "roles", roleId)
    
    override val method: HttpMethod
        get() = HttpMethod.Delete
    
    override fun route(builder: RouteInfoBuilder) {
        builder.apiPath = path
    }
    
    override val body: Any? get() = null
}
