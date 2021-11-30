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
        internal val serializer: KSerializer<out TencentRoleInfo> = TencentRoleInfoImpl.serializer()

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