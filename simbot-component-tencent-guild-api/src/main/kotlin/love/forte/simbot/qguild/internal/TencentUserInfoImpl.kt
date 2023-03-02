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

package love.forte.simbot.qguild.internal

import kotlinx.serialization.*
import love.forte.simbot.*
import love.forte.simbot.qguild.*

/**
 * [User](https://bot.q.qq.com/wiki/develop/api/openapi/user/model.html)
 *
 */
@Serializable
internal data class TencentUserInfoImpl(
    override val id: CharSequenceID,
    override val username: String,
    override val avatar: String,
    @SerialName("bot")
    override val isBot: Boolean,
    @SerialName("union_openid")
    override val unionOpenid: String? = null,
    @SerialName("union_user_account")
    override val unionUserAccount: String? = null
) : TencentUserInfo
