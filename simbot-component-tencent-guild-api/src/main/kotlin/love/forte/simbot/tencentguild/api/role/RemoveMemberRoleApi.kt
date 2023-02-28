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
import love.forte.simbot.tencentguild.api.RouteInfoBuilder
import love.forte.simbot.tencentguild.api.TencentApiWithoutResult

/**
 * [删除频道身份组成员](https://bot.q.qq.com/wiki/develop/api/openapi/guild/delete_guild_member_role.html)
 *
 * 用于将 用户 `user_id` 从 频道 `guild_id` 的 `role_id` 身份组中移除。
 *
 * - 需要使用的 `token` 对应的用户具备删除身份组成员权限。如果是机器人，要求被添加为管理员。
 * - 如果要删除的身份组 `ID` 是 [`5-子频道管理员`][love.forte.simbot.tencentguild.model.Role.DEFAULT_ID_CHANNEL_ADMIN]，
 * 需要增加 `channel` 对象来指定具体是哪个子频道。
 *
 * @author ForteScarlet
 */
public class RemoveMemberRoleApi private constructor(
    guildId: String,
    userId: String,
    roleId: String,
    channelId: String?,
) : TencentApiWithoutResult() {
    public companion object Factory {
        
        /**
         * 构造 [RemoveMemberRoleApi]
         *
         * @param channelId 如果要删除的身份组 [roleId] 是 [`5-子频道管理员`][love.forte.simbot.tencentguild.model.Role.DEFAULT_ID_CHANNEL_ADMIN]，
         * 需要增加 [channelId] 对象来指定具体是哪个子频道。
         */
        @JvmStatic
        @JvmOverloads
        public fun create(
            guildId: String,
            userId: String,
            roleId: String,
            channelId: String? = null,
        ): RemoveMemberRoleApi = RemoveMemberRoleApi(guildId, userId, roleId, channelId)
    }
    
    // DELETE /guilds/{guild_id}/members/{user_id}/roles/{role_id}
    private val path = arrayOf(
        "guilds",
        guildId.toString(),
        "members",
        userId.toString(),
        "roles",
        roleId.toString(),
    )
    
    override val method: HttpMethod
        get() = HttpMethod.Delete
    
    override fun route(builder: RouteInfoBuilder) {
        builder.apiPath = path
    }

    override val body: Any? = channelId?.let { cid -> Body(ChannelId(cid)) }

    @Serializable
    private data class Body(val channel: ChannelId)

    @Serializable
    private data class ChannelId(val id: String)
    
}
