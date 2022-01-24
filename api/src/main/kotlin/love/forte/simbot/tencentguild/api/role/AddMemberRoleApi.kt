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

package love.forte.simbot.tencentguild.api.role

import io.ktor.http.*
import kotlinx.serialization.Serializable
import love.forte.simbot.ID
import love.forte.simbot.tencentguild.api.RouteInfoBuilder
import love.forte.simbot.tencentguild.api.TencentApiWithoutResult

/**
 *
 * [增加频道身份组成员](https://bot.q.qq.com/wiki/develop/api/openapi/guild/put_guild_member_role.html)
 *
 * @author ForteScarlet
 */
public class AddMemberRoleApi(
    channelId: ID, guildId: ID,
    userId: ID, roleId: ID,
) : TencentApiWithoutResult() {
    private val _body = Body(channelId.toString())

    // PUT /guilds/{guild_id}/members/{user_id}/roles/{role_id}
    private val path = listOf(
        "guilds",
        guildId.toString(),
        "members",
        userId.toString(),
        "roles",
        roleId.toString(),
    )

    override val method: HttpMethod
        get() = HttpMethod.Put

    override fun route(builder: RouteInfoBuilder) {
        builder.apiPath = path
    }

    override val body: Any get() = _body

    /** 接收一个只填充了子频道id字段的对象 */
    @Serializable
    private data class Body(val id: String)


}