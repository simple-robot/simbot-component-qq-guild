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