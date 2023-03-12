/*
 * Copyright (c) 2023. ForteScarlet.
 *
 * This file is part of simbot-component-tencent-guild.
 *
 * simbot-component-tencent-guild is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * simbot-component-tencent-guild is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with simbot-component-tencent-guild. If not, see <https://www.gnu.org/licenses/>.
 */

package love.forte.simbot.qguild.api.apipermission

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.Serializable
import love.forte.simbot.qguild.ApiModel
import love.forte.simbot.qguild.api.ApiDescription
import love.forte.simbot.qguild.api.GetTencentApi
import love.forte.simbot.qguild.api.RouteInfoBuilder
import love.forte.simbot.qguild.api.SimpleGetApiDescription
import love.forte.simbot.qguild.model.ApiPermission

/**
 * [获取频道可用权限列表](https://bot.q.qq.com/wiki/develop/api/openapi/api_permissions/get_guild_api_permission.html)
 *
 * 用于获取机器人在频道 `guild_id` 内可以使用的权限列表。
 *
 * @author ForteScarlet
 */
public class GetApiPermissionListApi private constructor(
    guildId: String
) : GetTencentApi<ApiPermissions>() {

    public companion object Factory : SimpleGetApiDescription(
        "/guilds/{guild_id}/api_permission"
    ) {

        /**
         * 构造 [GetApiPermissionListApi].
         *
         * @param guildId 频道服务器ID
         */
        @JvmStatic
        public fun create(guildId: String): GetApiPermissionListApi = GetApiPermissionListApi(guildId)
    }

    private val path = arrayOf("guilds", guildId, "api_permission")

    override val resultDeserializer: DeserializationStrategy<ApiPermissions>
        get() = ApiPermissions.serializer()

    override fun route(builder: RouteInfoBuilder) {
        builder.apiPath = path
    }
}

/**
 * [GetApiPermissionListApi] 的API响应体。
 */
@ApiModel
@Serializable
public class ApiPermissions(public val apis: List<ApiPermission>) : List<ApiPermission> by apis {

    /**
     * 在当前权限列表中寻找是否与 [description] 描述相同的权限.
     */
    public operator fun contains(description: ApiDescription): Boolean {
        return apis.any { p ->
            description.method == p.method
                    && description.path == p.path
        }
    }

    /**
     * 在当前权限列表中寻找与 [description] 描述相同的权限.
     */
    public fun find(description: ApiDescription): ApiPermission? = apis.find { p ->
        description.method == p.method
                && description.path == p.path
    }
}
