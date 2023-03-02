/*
 * Copyright (c) 2022-2023. ForteScarlet.
 *
 * This file is part of simbot-component-tencent-guild.
 *
 * simbot-component-tencent-guild is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * simbot-component-tencent-guild is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with simbot-component-tencent-guild. If not, see <https://www.gnu.org/licenses/>.
 */

package love.forte.simbot.qguild

import kotlinx.serialization.KSerializer
import love.forte.simbot.ID
import love.forte.simbot.qguild.internal.TencentRoleInfoImpl

/**
 *
 * @author ForteScarlet
 */
public interface TencentRoleInfo {

    /**
     * 身份组ID, 默认值可参考DefaultRoles
     */
    public val id: ID

    /**
     * 名称
     */
    public val name: String

    /**
     * ARGB的HEX十六进制颜色值转换后的十进制数值
     */
    public val color: Int

    /**
     * 是否在成员列表中单独展示: 0-否, 1-是
     */
    public val isHoist: Boolean

    /**
     * 人数
     */
    public val number: Int

    /**
     * 成员上限
     */
    public val memberLimit: Int

    /**
     * ID是否属于默认权限组
     */
    public val isDefault: Boolean get() = id.toString() in defaultRoles

    public companion object {
        public val serializer: KSerializer<out TencentRoleInfo> = TencentRoleInfoImpl.serializer()

        public val defaultRoles: Map<String, DefaultRole> = mapOf(
            "1" to DefaultRole.ALL_MEMBER,
            "2" to DefaultRole.ADMIN,
            "4" to DefaultRole.OWNER,
            "5" to DefaultRole.CHANNEL_ADMIN
        )
    }

    public enum class DefaultRole(public val code: Int) {
        /** 全体成员身份组 */
        ALL_MEMBER(1),

        /** 管理员身份组 */
        ADMIN(2),

        /** 群主/创建者身份组 */
        OWNER(4),

        /** 子频道管理员身份组 */
        CHANNEL_ADMIN(5);
    }

}
