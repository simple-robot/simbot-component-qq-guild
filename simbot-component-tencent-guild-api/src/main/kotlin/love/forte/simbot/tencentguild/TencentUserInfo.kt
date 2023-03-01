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
import love.forte.simbot.ID
import love.forte.simbot.definition.UserInfo
import love.forte.simbot.tencentguild.internal.TencentUserInfoImpl

/**
 *
 * @author ForteScarlet
 */
public interface TencentUserInfo : UserInfo {
    /**
     * 用户 id
     */
    override val id: ID

    /**
     * 用户名
     */
    override val username: String

    /**
     * 用户头像地址
     */
    override val avatar: String

    /**
     * 是否是机器人
     */
    public val isBot: Boolean

    /**
     * 特殊关联应用的 openid，需要特殊申请并配置后才会返回。如需申请，请联系平台运营人员。
     */
    public val unionOpenid: String?

    /**
     * 机器人关联的互联应用的用户信息，与union_openid关联的应用是同一个。如需申请，请联系平台运营人员。
     */
    public val unionUserAccount: String?

    public companion object {
        public val serializer: KSerializer<out TencentUserInfo> = TencentUserInfoImpl.serializer()
    }
}
