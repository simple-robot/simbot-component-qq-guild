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

package love.forte.simbot.component.tencentguild.internal.event

import love.forte.simbot.ID
import love.forte.simbot.Timestamp
import love.forte.simbot.component.tencentguild.TencentGuild
import love.forte.simbot.component.tencentguild.event.TcgGuildModifyEvent
import love.forte.simbot.component.tencentguild.internal.TencentGuildComponentBotImpl
import love.forte.simbot.component.tencentguild.internal.TencentGuildImpl
import love.forte.simbot.component.tencentguild.internal.TencentGuildImpl.Companion.tencentGuildImpl
import love.forte.simbot.literal
import love.forte.simbot.tencentguild.EventSignals
import love.forte.simbot.tencentguild.TencentGuildInfo

private suspend fun TencentGuildComponentBotImpl.findOrCreateGuild(data: TencentGuildInfo): TencentGuildImpl {
    return getInternalGuild(data.id) ?: tencentGuildImpl(this, data).also {
        internalGuilds[data.id.literal] = it
    }
}

internal class TcgGuildCreate(
    override val sourceEventEntity: TencentGuildInfo,
    override val bot: TencentGuildComponentBotImpl,
    private val _guild: TencentGuildImpl,
) : TcgGuildModifyEvent.Create() {
    override val timestamp: Timestamp get() = changedTime
    override val id: ID = tcgGuildModifyId(0, bot.id, _guild.id, timestamp)
    
    override suspend fun guild(): TencentGuild = _guild
    
    internal object Parser : BaseSignalToEvent<TencentGuildInfo>() {
        override val key = Create
        override val type: EventSignals<TencentGuildInfo>
            get() = EventSignals.Guilds.GuildCreate
        
        override suspend fun doParser(data: TencentGuildInfo, bot: TencentGuildComponentBotImpl): TcgGuildCreate {
            val guild = bot.findOrCreateGuild(data)
            return TcgGuildCreate(data, bot, guild)
        }
    }
}


internal class TcgGuildUpdate(
    override val sourceEventEntity: TencentGuildInfo,
    override val bot: TencentGuildComponentBotImpl,
    private val _guild: TencentGuildImpl,
) : TcgGuildModifyEvent.Update() {
    override val id: ID = tcgGuildModifyId(1, bot.id, _guild.id, timestamp)
    override val timestamp: Timestamp get() = changedTime
    override suspend fun guild(): TencentGuild = _guild
    
    internal object Parser : BaseSignalToEvent<TencentGuildInfo>() {
        override val key = Update
        override val type: EventSignals<TencentGuildInfo>
            get() = EventSignals.Guilds.GuildUpdate
        
        override suspend fun doParser(data: TencentGuildInfo, bot: TencentGuildComponentBotImpl): TcgGuildUpdate {
            val guild = bot.findOrCreateGuild(data)
            return TcgGuildUpdate(data, bot, guild)
        }
    }
}


internal class TcgGuildDelete(
    override val sourceEventEntity: TencentGuildInfo,
    override val bot: TencentGuildComponentBotImpl,
    private val _guild: TencentGuildImpl,
) : TcgGuildModifyEvent.Delete() {
    override val timestamp: Timestamp
        get() = changedTime
    override val id: ID = tcgGuildModifyId(2, bot.id, _guild.id, timestamp)
    override suspend fun guild(): TencentGuild = _guild
    
    internal object Parser : BaseSignalToEvent<TencentGuildInfo>() {
        override val key = Delete
        override val type: EventSignals<TencentGuildInfo>
            get() = EventSignals.Guilds.GuildDelete
        
        override suspend fun doParser(data: TencentGuildInfo, bot: TencentGuildComponentBotImpl): TcgGuildDelete {
            val guild = bot.findOrCreateGuild(data)
            return TcgGuildDelete(data, bot, guild)
        }
    }
}


private fun tcgGuildModifyId(t: Int, sourceBot: ID, sourceGuild: ID, timestamp: Timestamp): ID =
    "$t$sourceBot.${timestamp.second}.$sourceGuild".ID
