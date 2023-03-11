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

package love.forte.simbot.qguild.api.member

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.Serializable
import love.forte.simbot.qguild.ApiModel
import love.forte.simbot.qguild.PrivateDomainOnly
import love.forte.simbot.qguild.api.GetTencentApi
import love.forte.simbot.qguild.api.RouteInfoBuilder
import love.forte.simbot.qguild.api.SimpleGetApiDescription
import love.forte.simbot.qguild.model.SimpleMember
import org.slf4j.LoggerFactory


/**
 * [获取频道身份组成员列表](https://bot.q.qq.com/wiki/develop/api/openapi/member/get_role_members.html)
 *
 * 用于获取 `guild_id` 频道中指定 `role_id` 身份组下所有成员的详情列表，支持分页。
 *
 * ### 有关返回结果的说明
 * 1. 每次返回的member数量与limit不一定完全相等。特定管理身份组下的成员可能存在一次性返回全部的情况
 *
 * @author ForteScarlet
 */
@PrivateDomainOnly
public class GetGuildRoleMemberListApi private constructor(
    guildId: String,
    roleId: String,
    /**
     * 将上一次回包中 `next` 填入， 如果是第一次请求填 `0`，默认为 `0`
     */
    private val startIndex: String? = null,
    /**
     * 分页大小，`1-400`，默认是 `1`。成员较多的频道尽量使用较大的 `limit` 值，以减少请求数
     */
    private val limit: Int,
) : GetTencentApi<GuildRoleMemberList>() {
    init {
        require(limit > 0) { "limit must > 0, but $limit" }
        if (limit > 400) {
            // or throw error? or ignore?
            logger.warn("The maximum value of the limit is 400, but {}", limit)
        }
    }

    public companion object Factory : SimpleGetApiDescription(
        "/guilds/{guild_id}/roles/{role_id}/members"
    ) {
        private val logger = LoggerFactory.getLogger(GetGuildRoleMemberListApi::class.java)

        /**
         * 构造 [GetGuildRoleMemberListApi]
         */
        @JvmStatic
        @JvmOverloads
        public fun create(
            guildId: String, roleId: String, startIndex: String? = null, limit: Int = 1
        ): GetGuildRoleMemberListApi = GetGuildRoleMemberListApi(guildId, roleId, startIndex, limit)

        /**
         * 构造 [GetGuildRoleMemberListApi]
         */
        @JvmStatic
        public fun create(
            guildId: String, roleId: String, limit: Int
        ): GetGuildRoleMemberListApi = GetGuildRoleMemberListApi(guildId, roleId, null, limit)
    }

    private val path = arrayOf("guilds", guildId, "roles", roleId, "members")

    override val resultDeserializer: DeserializationStrategy<GuildRoleMemberList>
        get() = GuildRoleMemberList.serializer()

    override fun route(builder: RouteInfoBuilder) {
        builder.apiPath = path
        startIndex?.also { builder.parametersAppender.append("start_index", startIndex) }
        builder.parametersAppender.append("limit", limit)
    }
}


/**
 * [GetGuildRoleMemberListApi] 的响应体包装类。
 */
@ApiModel
@Serializable
public data class GuildRoleMemberList(
    /**
     * 一组用户信息对象
     */
    val data: List<SimpleMember>,

    /**
     * 下一次请求的分页标识
     */
    val next: String
)

