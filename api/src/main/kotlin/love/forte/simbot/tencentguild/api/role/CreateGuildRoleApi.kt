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
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.CharSequenceID
import love.forte.simbot.ID
import love.forte.simbot.tencentguild.TencentRoleInfo
import love.forte.simbot.tencentguild.api.RouteInfoBuilder
import love.forte.simbot.tencentguild.api.TencentApi
import love.forte.simbot.tencentguild.internal.TencentRoleInfoImpl

/**
 *
 * [创建频道身份组](https://bot.q.qq.com/wiki/develop/api/openapi/guild/post_guild_role.html#%E5%88%9B%E5%BB%BA%E9%A2%91%E9%81%93%E8%BA%AB%E4%BB%BD%E7%BB%84)
 * @author ForteScarlet
 */
public class CreateGuildRoleApi internal constructor(
    guildId: ID,
    private val _body: Body
) : TencentApi<GuildRoleCreated>() {
    @JvmOverloads
    public constructor(
        guildId: ID,
        filter: GuildRoleFilter = defBody.filter,
        info: GuildRoleInfo = defBody.info
    ) : this(
        guildId,
        if (filter === defBody.filter && info === defBody.info) defBody
        else Body(filter, info)
    )

    private val path = listOf("guilds", guildId.toString(), "roles")


    override val resultDeserializer: DeserializationStrategy<out GuildRoleCreated> get() = GuildRoleCreated.serializer
    override val method: HttpMethod get() = HttpMethod.Post

    override fun route(builder: RouteInfoBuilder) {
        builder.apiPath = path
    }

    override val body: Any get() = _body

    public companion object {
        private val defBody = Body(GuildRoleFilter.default, GuildRoleInfo.default)
    }


    @Serializable
    internal data class Body(
        val filter: GuildRoleFilter,
        val info: GuildRoleInfo
    )

}

/**
 * [CreateGuildRoleApi] 创建成功后的返回值
 */
public interface GuildRoleCreated {
    public val roleId: CharSequenceID
    public val role: TencentRoleInfo

    public companion object {
        public val serializer: KSerializer<out GuildRoleCreated> = GuildRoleCreatedImpl.serializer()
    }
}


@Serializable
internal data class GuildRoleCreatedImpl(
    @SerialName("role_id")
    override val roleId: CharSequenceID,
    override val role: TencentRoleInfoImpl
) : GuildRoleCreated


