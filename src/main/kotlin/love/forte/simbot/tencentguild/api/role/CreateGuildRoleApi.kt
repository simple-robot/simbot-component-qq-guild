package love.forte.simbot.tencentguild.api.role

import io.ktor.http.*
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.ID
import love.forte.simbot.LongID
import love.forte.simbot.tencentguild.TencentRoleInfo
import love.forte.simbot.tencentguild.api.BooleanToNumber
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
        guildId: ID, filter: Filter = defFilter, info: Info = defInfo
    ) : this(
        guildId,
        if (filter === defFilter && info === defInfo) defBody
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
        private val defFilter = Filter()
        private val defInfo = Info()
        private val defBody = Body(defFilter, defInfo)
    }


    /**
     * [Filter](https://bot.q.qq.com/wiki/develop/api/openapi/guild/post_guild_role.html#filter)
     *
     * 标识需要设置哪些字段
     *
     */
    @Serializable
    public data class Filter @JvmOverloads constructor(
        /** 是否设置名称 */
        @SerialName("name") @Serializable(BooleanToNumber::class)
        public var isName: Boolean = false,

        /** 是否设置颜色 */
        @SerialName("color") @Serializable(BooleanToNumber::class)
        public var isColor: Boolean = false,

        /** 是否设置在成员列表中单独展示 */
        @SerialName("hoist") @Serializable(BooleanToNumber::class)
        public var isHoist: Boolean = false
    )

    /**
     * [Info](https://bot.q.qq.com/wiki/develop/api/openapi/guild/post_guild_role.html#info)
     * 携带需要设置的字段内容
     */
    @Serializable
    public data class Info @JvmOverloads constructor(
        /** 名称 */
        public var name: String? = null,

        /** ARGB的HEX十六进制颜色值转换后的十进制数值 */
        public var color: Int? = null,

        /** 在成员列表中单独展示 */
        @SerialName("hoist") @Serializable(BooleanToNumber::class)
        public var isHoist: Boolean = false
    )

    @Serializable
    internal data class Body(
        val filter: Filter,
        val info: Info
    )

}

@Serializable
public data class GuildRoleCreated(
    @SerialName("role_id")
    public val roleId: LongID,
    public val role: TencentRoleInfo
)
