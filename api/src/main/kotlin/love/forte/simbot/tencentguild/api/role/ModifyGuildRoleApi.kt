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
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.CharSequenceID
import love.forte.simbot.ID
import love.forte.simbot.tencentguild.TencentRoleInfo
import love.forte.simbot.tencentguild.api.RouteInfoBuilder
import love.forte.simbot.tencentguild.api.TencentApi

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