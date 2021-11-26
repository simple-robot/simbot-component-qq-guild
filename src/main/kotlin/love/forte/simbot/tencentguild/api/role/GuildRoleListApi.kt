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
 * [获取频道身份组列表](https://bot.q.qq.com/wiki/develop/api/openapi/guild/get_guild_roles.html#%E8%8E%B7%E5%8F%96%E9%A2%91%E9%81%93%E8%BA%AB%E4%BB%BD%E7%BB%84%E5%88%97%E8%A1%A8)
 * @author ForteScarlet
 */
public class GuildRoleListApi(guildId: ID) : TencentApi<GuildRoleList> {

    override val resultDeserializer: DeserializationStrategy<out GuildRoleList> = serializer
    override val method: HttpMethod
        get() = HttpMethod.Get

    override fun route(builder: RouteInfoBuilder) {
        builder.apiPath = path
    }

    override val body: Any? get() = null

    private val path = listOf("/guilds", guildId.toString(), "roles")

    public companion object {
        private val serializer = GuildRoleList.serializer()
    }
}

@Serializable
public data class GuildRoleList(
    @SerialName("guild_id")
    public val guildId: LongID,
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

