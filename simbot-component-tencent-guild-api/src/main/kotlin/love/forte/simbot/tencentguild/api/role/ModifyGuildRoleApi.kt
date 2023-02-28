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
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.CharSequenceID
import love.forte.simbot.tencentguild.api.RouteInfoBuilder
import love.forte.simbot.tencentguild.api.TencentApi
import love.forte.simbot.tencentguild.model.Role

/**
 * [修改频道身份组](https://bot.q.qq.com/wiki/develop/api/openapi/guild/patch_guild_role.html#%E4%BF%AE%E6%94%B9%E9%A2%91%E9%81%93%E8%BA%AB%E4%BB%BD%E7%BB%84)
 *
 * 用于修改频道 `guild_id` 下 `role_id` 指定的身份组。
 *
 * - 需要使用的 `token` 对应的用户具备修改身份组权限。如果是机器人，要求被添加为管理员。
 * - 接口会修改传入的字段，不传入的默认不会修改，至少要传入一个参数。
 *
 * @author ForteScarlet
 */
public class ModifyGuildRoleApi private constructor(
    guildId: String, roleId: String,
    private val _body: Body,
) : TencentApi<GuildRoleModified>() {
    public companion object Factory {

        /**
         * 构造 [ModifyGuildRoleApi]
         *
         * 参数可选，但是至少需要传其中之一。
         *
         * @param name 名称(非必填)
         * @param color ARGB 的 HEX 十六进制颜色值转换后的十进制数值(非必填)
         * @param hoist 在成员列表中单独展示: 0-否, 1-是(非必填)
         */
        @JvmStatic
        @JvmOverloads
        public fun create(
            guildId: String, roleId: String, name: String? = null, color: Int? = null, hoist: Int? = null
        ): ModifyGuildRoleApi = ModifyGuildRoleApi(guildId, roleId, Body(name, color, hoist))

    }

    private val path = arrayOf("guilds", guildId, "roles", roleId)

    override val resultDeserializer: DeserializationStrategy<GuildRoleModified>
        get() = GuildRoleModified.serializer()

    override val method: HttpMethod
        get() = HttpMethod.Patch

    override fun route(builder: RouteInfoBuilder) {
        builder.apiPath = path
    }

    override val body: Any get() = _body

    @Serializable
    private data class Body(val name: String?, val color: Int?, val hoist: Int?) {
        init {
            require(name != null || color != null || hoist != null) {
                "At least one of the parameters should not be null"
            }
        }
    }
}

/**
 * [ModifyGuildRoleApi] 的响应体类型。
 */
@Serializable
public data class GuildRoleModified(
    /**
     * 频道ID
     */
    @SerialName("guild_id") public val guildId: CharSequenceID,

    /**
     * 身份组ID
     */
    @SerialName("role_id") public val roleId: CharSequenceID,

    /**
     * 修改后的频道身份组对象
     */
    public val role: Role,
)
