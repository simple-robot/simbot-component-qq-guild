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

package love.forte.simbot.tencentguild

import kotlinx.serialization.KSerializer
import love.forte.simbot.Api4J
import love.forte.simbot.ID
import love.forte.simbot.definition.IDContainer
import love.forte.simbot.tencentguild.internal.TencentChannelPermissionsInfoImpl
import love.forte.simbot.tencentguild.model.Permissions

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

