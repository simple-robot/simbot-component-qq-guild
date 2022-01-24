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

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.tencentguild.api.BooleanToNumber

/**
 * [Filter](https://bot.q.qq.com/wiki/develop/api/openapi/guild/post_guild_role.html#filter)
 *
 * 标识需要设置哪些字段
 *
 */
@Serializable
public data class GuildRoleFilter @JvmOverloads constructor(
    /** 是否设置名称 */
    @SerialName("name") @Serializable(BooleanToNumber::class)
    public var isName: Boolean = false,

    /** 是否设置颜色 */
    @SerialName("color") @Serializable(BooleanToNumber::class)
    public var isColor: Boolean = false,

    /** 是否设置在成员列表中单独展示 */
    @SerialName("hoist") @Serializable(BooleanToNumber::class)
    public var isHoist: Boolean = false
) {
    public companion object {
        public val default: GuildRoleFilter = GuildRoleFilter()
    }
}

/**
 * [Info](https://bot.q.qq.com/wiki/develop/api/openapi/guild/post_guild_role.html#info)
 * 携带需要设置的字段内容
 */
@Serializable
public data class GuildRoleInfo @JvmOverloads constructor(
    /** 名称 */
    public var name: String? = null,

    /** ARGB的HEX十六进制颜色值转换后的十进制数值 */
    public var color: Int? = null,

    /** 在成员列表中单独展示 */
    @SerialName("hoist") @Serializable(BooleanToNumber::class)
    public var isHoist: Boolean = false

) {
    public companion object {
        public val default: GuildRoleInfo = GuildRoleInfo()
    }
}