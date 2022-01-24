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

package love.forte.simbot.tencentguild

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import love.forte.simbot.Api4J
import love.forte.simbot.ID
import love.forte.simbot.definition.Permission
import love.forte.simbot.definition.PermissionStatus
import love.forte.simbot.tencentguild.internal.TencentChannelPermissionsInfoImpl

/**
 *
 * [子频道权限对象](https://bot.q.qq.com/wiki/develop/api/openapi/channel_permissions/model.html)
 * @author ForteScarlet
 */
public interface TencentChannelPermissionsInfo : Permission {

    /**
     * 子频道 id
     */
    public val channelId: ID

    /**
     * 用户 id
     */
    public val userId: ID

    /**
     * 用户拥有的子频道权限
     */
    public val permissions: Permissions

    @Api4J
    public val permissionsValue: Long get() = permissions.value

    public companion object {
        internal val serializer: KSerializer<out TencentChannelPermissionsInfo> = TencentChannelPermissionsInfoImpl.serializer()
    }
}

/**
 * [permissions](https://bot.q.qq.com/wiki/develop/api/openapi/channel_permissions/model.html#permissions)
 */
@Serializable
@JvmInline
public value class Permissions(public val value: Long) {

    public val isChannelViewable: Boolean get() = (value and CHANNEL_VIEWABLE) != 0L

    public val isChannelManageable: Boolean get() = (value and CHANNEL_MANAGEABLE) != 0L

    public val status: PermissionStatus get() = PermissionStatus.builder().also {
        if (value != 0L) {
            it.admin()
            it.organizationAdmin()
            it.channelAdmin()
        }
    }.build()

    public operator fun plus(other: Permissions): Permissions = Permissions(value or other.value)
    public operator fun plus(other: Long): Permissions = Permissions(value or other)

    public companion object {
        /**
         * 可查看子频道
         */
        public const val CHANNEL_VIEWABLE: Long = 1 shl 0

        /**
         * 可管理子频道
         */
        public const val CHANNEL_MANAGEABLE: Long = 1 shl 1
    }
}

