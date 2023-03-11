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
import kotlinx.serialization.builtins.ListSerializer
import love.forte.simbot.qguild.PrivateDomainOnly
import love.forte.simbot.qguild.api.GetTencentApi
import love.forte.simbot.qguild.api.RouteInfoBuilder
import love.forte.simbot.qguild.api.SimpleGetApiDescription
import love.forte.simbot.qguild.model.SimpleMember
import org.slf4j.LoggerFactory


/**
 *
 * [获取频道成员列表](https://bot.q.qq.com/wiki/develop/api/openapi/member/get_members.html)
 *
 * 用于获取 guild_id 指定的频道中所有成员的详情列表，支持分页。
 *
 * ### 有关返回结果的说明
 * 1. 在每次翻页的过程中，可能会返回上一次请求已经返回过的 `member` 信息，需要调用方自己根据 `user id` 来进行去重。
 * 2. 每次返回的 `member` 数量与 `limit` 不一定完全相等。翻页请使用最后一个 `member` 的 `user id` 作为下一次请求的 `after` 参数，直到回包为空，拉取结束。
 *
 * @throws IllegalArgumentException [limit] 不大于0时
 *
 * @author ForteScarlet
 */
@PrivateDomainOnly
public class GetGuildMemberListApi private constructor(
    guildId: String,
    /**
     * 上一次回包中最后一个member的 `user id`， 如果是第一次请求填 0，默认为 0
     */
    private val after: String?,

    /**
     * 分页大小，1-400，默认是 1。成员较多的频道尽量使用较大的limit值，以减少请求数.
     */
    private val limit: Int,
) : GetTencentApi<List<SimpleMember>>() {
    init {
        require(limit > 0) { "limit must > 0, but $limit" }
        if (limit > 400) {
            // or throw error? or ignore?
            logger.warn("The maximum value of the limit is 400, but {}", limit)
        }
    }

    public companion object Factory : SimpleGetApiDescription(
        "/guilds/{guild_id}/members"
    ) {
        private val deserializer = ListSerializer(SimpleMember.serializer())
        private val logger = LoggerFactory.getLogger(GetGuildMemberListApi::class.java)

        /**
         * 构造 [GetGuildMemberListApi]
         *
         */
        @JvmStatic
        @JvmOverloads
        public fun create(guildId: String, after: String? = null, limit: Int = 1): GetGuildMemberListApi =
            GetGuildMemberListApi(guildId, after, limit)

        /**
         * 构造 [GetGuildMemberListApi]
         *
         */
        @JvmStatic
        public fun create(guildId: String, limit: Int): GetGuildMemberListApi =
            GetGuildMemberListApi(guildId, null, limit)
    }

    private val path = arrayOf("guilds", guildId, "members")

    override val resultDeserializer: DeserializationStrategy<List<SimpleMember>>
        get() = deserializer

    override fun route(builder: RouteInfoBuilder) {
        builder.apiPath = path

        after?.also { builder.parametersAppender.append("after", it) }
        builder.parametersAppender.append("limit", limit)
    }
}
