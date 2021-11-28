package love.forte.simbot.tencentguild.api.role

import io.ktor.http.*
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.ID
import love.forte.simbot.LongID
import love.forte.simbot.tencentguild.TencentRoleInfo
import love.forte.simbot.tencentguild.api.RouteInfoBuilder
import love.forte.simbot.tencentguild.api.TencentApi

/**
 *
 * [创建频道身份组](https://bot.q.qq.com/wiki/develop/api/openapi/guild/post_guild_role.html#%E5%88%9B%E5%BB%BA%E9%A2%91%E9%81%93%E8%BA%AB%E4%BB%BD%E7%BB%84)
 * @author ForteScarlet
 */
public class CreateGuildRoleApi internal constructor(
    guildId: ID,
    private val _body: Body
) : TencentApi<GuildRoleCreated> {
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


    override val resultDeserializer: DeserializationStrategy<out GuildRoleCreated> get() = serializer
    override val method: HttpMethod get() = HttpMethod.Post

    override fun route(builder: RouteInfoBuilder) {
        builder.apiPath = path
    }

    override val body: Any get() = _body

    public companion object {
        private val serializer = GuildRoleCreated.serializer()
        private val defBody = Body(GuildRoleFilter.default, GuildRoleInfo.default)
    }


    @Serializable
    internal data class Body(
        val filter: GuildRoleFilter,
        val info: GuildRoleInfo
    )

}

@Serializable
public data class GuildRoleCreated(
    @SerialName("role_id")
    public val roleId: LongID,
    public val role: TencentRoleInfo
)
