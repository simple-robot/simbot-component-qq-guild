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

package love.forte.simbot.tencentguild.api.member

import kotlinx.serialization.DeserializationStrategy
import love.forte.simbot.tencentguild.api.GetTencentApi
import love.forte.simbot.tencentguild.api.RouteInfoBuilder
import love.forte.simbot.tencentguild.model.Member


/**
 * [获取某个成员信息](https://bot.q.qq.com/wiki/develop/api/openapi/member/get_member.html)
 *
 * 用于获取 `guild_id` 指定的频道中 `user_id` 对应成员的详细信息。
 *
 * @author ForteScarlet
 */
public class GetMemberApi private constructor(
    guildId: String, userId: String
) : GetTencentApi<Member>() {
    public companion object Factory {
        /**
         * 构造 [GetMemberApi]
         *
         */
        @JvmStatic
        public fun create(guildId: String, userId: String): GetMemberApi = GetMemberApi(guildId, userId)
    }

    // GET /guilds/{guild_id}/members/{user_id}
    private val path = arrayOf("guilds", guildId, "members", userId)

    override val resultDeserializer: DeserializationStrategy<Member>
        get() = Member.serializer()

    override fun route(builder: RouteInfoBuilder) {
        builder.apiPath = path
    }
}
