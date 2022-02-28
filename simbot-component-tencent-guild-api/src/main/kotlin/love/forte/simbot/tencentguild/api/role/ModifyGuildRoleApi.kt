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
import kotlinx.serialization.*
import love.forte.simbot.*
import love.forte.simbot.tencentguild.*
import love.forte.simbot.tencentguild.api.*

/**
 * [修改频道身份组](https://bot.q.qq.com/wiki/develop/api/openapi/guild/patch_guild_role.html#%E4%BF%AE%E6%94%B9%E9%A2%91%E9%81%93%E8%BA%AB%E4%BB%BD%E7%BB%84)
 * @author ForteScarlet
 */
public class ModifyGuildRoleApi private constructor(
    guildId: ID,
    roleId: ID,
    private val _body: Body
) : TencentApi<GuildRoleModified>() {
    public constructor(
        guildId: ID,
        roleId: ID,
        filter: GuildRoleFilter,
        info: GuildRoleInfo
    ) : this(
        guildId,
        roleId,
        if (filter === defBody.filter && info === defBody.info) defBody
        else Body(filter, info)
    )

    private val path = listOf("guilds", guildId.toString(), "roles", roleId.toString())

    override val resultDeserializer: DeserializationStrategy<out GuildRoleModified>
        get() = serializer
    override val method: HttpMethod
        get() = HttpMethod.Patch

    override fun route(builder: RouteInfoBuilder) {
        builder.apiPath = path
    }

    override val body: Any get() = _body

    public companion object {
        private val serializer = GuildRoleModified.serializer()
        private val defBody = Body(GuildRoleFilter.default, GuildRoleInfo.default)
    }

    @Serializable
    private data class Body(val filter: GuildRoleFilter, val info: GuildRoleInfo)
}

@Serializable
public data class GuildRoleModified(
    /**
     * 频道ID
     */
    @SerialName("guild_id")
    public val guildId: CharSequenceID,

    /**
     * 身份组ID
     */
    @SerialName("role_id")
    public val roleId: CharSequenceID,

    /**
     * 修改后的频道身份组对象
     */
    public val role: TencentRoleInfo
)