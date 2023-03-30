/*
 * Copyright (c) 2023. ForteScarlet.
 *
 * This file is part of simbot-component-qq-guild.
 *
 * simbot-component-qq-guild is free software: you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * simbot-component-qq-guild is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with simbot-component-qq-guild.
 * If not, see <https://www.gnu.org/licenses/>.
 */

package love.forte.simbot.qguild.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.qguild.ApiModel
import kotlin.jvm.JvmStatic

/**
 * [频道身份组对象(Role)](https://bot.q.qq.com/wiki/develop/api/openapi/guild/role_model.html)
 *
 *
 *
 * @author ForteScarlet
 */
@ApiModel
@Serializable
public data class Role(
    /** 身份组ID */
    val id: String,
    /** 名称 */
    val name: String,
    /** ARGB的HEX十六进制颜色值转换后的十进制数值 */
    val color: Int,
    /** 是否在成员列表中单独展示: 0-否, 1-是 */
    val hoist: Int,
    /** 人数 */
    val number: Int,
    /** 成员上限 */
    @SerialName("member_limit") val memberLimit: Int,
) {

    /**
     * 是否在成员列表中单独展示
     *
     * @see hoist
     */
    val isHoistBool: Boolean get() = hoist == 1

    public companion object {
        /**
         * [DefaultRoleIDs(系统默认生成下列身份组ID)](https://bot.q.qq.com/wiki/develop/api/openapi/guild/role_model.html)
         *
         * 1: 全体成员
         */
        public const val DEFAULT_ID_ALL_MEMBER: String = "1"

        /**
         * [DefaultRoleIDs(系统默认生成下列身份组ID)](https://bot.q.qq.com/wiki/develop/api/openapi/guild/role_model.html)
         *
         * 2: 管理员
         */
        public const val DEFAULT_ID_ADMIN: String = "2"

        /**
         * [DefaultRoleIDs(系统默认生成下列身份组ID)](https://bot.q.qq.com/wiki/develop/api/openapi/guild/role_model.html)
         *
         * 4: 群主/创建者
         */
        public const val DEFAULT_ID_OWNER: String = "4"

        /**
         * [DefaultRoleIDs(系统默认生成下列身份组ID)](https://bot.q.qq.com/wiki/develop/api/openapi/guild/role_model.html)
         *
         * 5: 子频道管理员
         */
        public const val DEFAULT_ID_CHANNEL_ADMIN: String = "5"

        private val defaults = arrayOf(
            DEFAULT_ID_ALL_MEMBER,
            DEFAULT_ID_ADMIN,
            DEFAULT_ID_OWNER,
            DEFAULT_ID_CHANNEL_ADMIN
        )

        /**
         * 判断 [id] 是否为如下所列的默认身分组ID:
         * - [`"1"`: 全体成员][DEFAULT_ID_ALL_MEMBER]
         * - [`"2"`: 管理员][DEFAULT_ID_ADMIN]
         * - [`"4"`: 群主/创建者][DEFAULT_ID_OWNER]
         * - [`"5"`: 子频道管理员][DEFAULT_ID_CHANNEL_ADMIN]
         *
         */
        @JvmStatic
        public fun isDefault(id: String): Boolean = id in defaults


        /**
         * 判断 [id] 是否为如下所列的默认身分组ID:
         * - [`"1"`: 全体成员][DEFAULT_ID_ALL_MEMBER]
         * - [`"2"`: 管理员][DEFAULT_ID_ADMIN]
         * - [`"4"`: 群主/创建者][DEFAULT_ID_OWNER]
         * - [`"5"`: 子频道管理员][DEFAULT_ID_CHANNEL_ADMIN]
         *
         */
        @get:JvmStatic
        public val Role.isDefault: Boolean get() = isDefault(id)

    }
}


