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