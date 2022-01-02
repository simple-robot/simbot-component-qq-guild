package love.forte.simbot.tencentguild.api.role

import io.ktor.http.*
import love.forte.simbot.ID
import love.forte.simbot.tencentguild.api.RouteInfoBuilder
import love.forte.simbot.tencentguild.api.TencentApiWithoutResult

/**
 *
 * [删除频道身份组](https://bot.q.qq.com/wiki/develop/api/openapi/guild/delete_guild_role.html#%E5%88%A0%E9%99%A4%E9%A2%91%E9%81%93%E8%BA%AB%E4%BB%BD%E7%BB%84)
 *
 * 需要使用的token对应的用户具备删除身份组权限。如果是机器人，要求被添加为管理员。
 *
 * @author ForteScarlet
 */
public class DeleteGuildRoleApi(guildId: ID, roleId: ID) : TencentApiWithoutResult() {
    private val path = listOf("guilds", guildId.toString(), "roles", roleId.toString())

    override val method: HttpMethod
        get() = HttpMethod.Delete

    override fun route(builder: RouteInfoBuilder) {
        builder.apiPath = path
    }

    override val body: Any? get() = null
}