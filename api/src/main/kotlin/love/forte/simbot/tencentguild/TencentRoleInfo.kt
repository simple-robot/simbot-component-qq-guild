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

package love.forte.simbot.tencentguild

import kotlinx.serialization.KSerializer
import love.forte.simbot.ID
import love.forte.simbot.tencentguild.internal.TencentRoleInfoImpl

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

    public enum class DefaultRole(
        public val code: Int
    ) {
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