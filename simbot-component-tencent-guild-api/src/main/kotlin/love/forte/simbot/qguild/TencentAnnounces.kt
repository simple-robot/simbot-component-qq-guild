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
import love.forte.simbot.qguild.internal.TencentAnnouncesImpl


/**
 *
 * [公告对象(Announces)](https://bot.q.qq.com/wiki/develop/api/openapi/announces/model.html#announces)
 *
 * @author ForteScarlet
 */
public interface TencentAnnounces {
    /**
     * 	频道 id
     */
    public val guildId: ID


    /**
     * 	子频道 id
     */
    public val channelId: ID

    /**
     * 	消息 id
     */
    public val messageId: ID

    public companion object {
        internal val serializer: KSerializer<out TencentAnnouncesImpl> = TencentAnnouncesImpl.serializer()
    }
}
