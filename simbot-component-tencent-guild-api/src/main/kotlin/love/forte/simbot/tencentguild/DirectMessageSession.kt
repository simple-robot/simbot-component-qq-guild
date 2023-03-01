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
import love.forte.simbot.Timestamp
import love.forte.simbot.tencentguild.internal.DirectMessageSessionImpl

/**
 * [私信会话对象（DMS）](https://bot.q.qq.com/wiki/develop/api/openapi/dms/model.html)
 *
 * @author ForteScarlet
 */
public interface DirectMessageSession {
    
    /**
     * 私信会话关联的频道 id
     */
    public val guildId: ID
    
    /**
     * 私信会话关联的子频道 id
     */
    public val channelId: ID
    
    
    /**
     * 创建私信会话时间戳
     */
    public val createTime: Timestamp
    
    public companion object {
        internal val serializer: KSerializer<out DirectMessageSessionImpl> = DirectMessageSessionImpl.serializer()
    }
}
