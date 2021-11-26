package love.forte.simbot.tencentguild

import kotlinx.serialization.KSerializer
import love.forte.simbot.ID
import love.forte.simbot.LongID
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
    public val isDefault: Boolean get() = id in defaultRoleIds

    public companion object Defaults {
        internal val serializer: KSerializer<out TencentRoleInfo> = TencentRoleInfoImpl.serializer()

        public val defaultRoleIds: Map<LongID, String> = mapOf(
            0L.ID to "普通成员",
            1L.ID to "管理员",
            2L.ID to "群主",
            3L.ID to "机器人",
            5L.ID to "子频道管理员"
        )
    }
}