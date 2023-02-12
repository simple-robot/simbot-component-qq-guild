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

import kotlinx.serialization.*
import love.forte.simbot.*
import love.forte.simbot.tencentguild.*
import love.forte.simbot.tencentguild.api.*

/**
 *
 * [获取频道身份组列表](https://bot.q.qq.com/wiki/develop/api/openapi/guild/get_guild_roles.html#%E8%8E%B7%E5%8F%96%E9%A2%91%E9%81%93%E8%BA%AB%E4%BB%BD%E7%BB%84%E5%88%97%E8%A1%A8)
 * @author ForteScarlet
 */
public class GetGuildRoleListApi(guildId: ID) : GetTencentApi<GuildRoleList>() {

    override val resultDeserializer: DeserializationStrategy<GuildRoleList> = serializer

    override fun route(builder: RouteInfoBuilder) {
        builder.apiPath = path
    }

    private val path = listOf("/guilds", guildId.toString(), "roles")

    public companion object {
        private val serializer = GuildRoleList.serializer()
    }
}

@Serializable
public data class GuildRoleList(
    @SerialName("guild_id")
    public val guildId: CharSequenceID,
    /**
     *  一组频道身份组对象
     */
    public val roles: List<TencentRoleInfo>,
    /**
     * 	默认分组上限
     */
    @SerialName("role_num_limit")
    public val roleNumLimit: String // Int?
)

