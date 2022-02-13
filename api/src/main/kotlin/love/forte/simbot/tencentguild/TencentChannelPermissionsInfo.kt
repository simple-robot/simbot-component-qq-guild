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
import kotlinx.serialization.Serializable
import love.forte.simbot.Api4J
import love.forte.simbot.ID
import love.forte.simbot.definition.IDContainer
import love.forte.simbot.tencentguild.internal.TencentChannelPermissionsInfoImpl

/**
 *
 * [子频道权限对象](https://bot.q.qq.com/wiki/develop/api/openapi/channel_permissions/model.html)
 * @author ForteScarlet
 */
public interface TencentChannelPermissionsInfo : IDContainer {

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
    public val permissionsValue: Long
        get() = permissions.value

    public companion object {
        internal val serializer: KSerializer<out TencentChannelPermissionsInfo> =
            TencentChannelPermissionsInfoImpl.serializer()
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

    public val isAdmin: Boolean get() = value != 0L

    // public val status: PermissionStatus
    //     get() = PermissionStatus.builder().also {
    //         if (value != 0L) {
    //             it.admin()
    //             it.organizationAdmin()
    //             it.channelAdmin()
    //         }
    //     }.build()

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

