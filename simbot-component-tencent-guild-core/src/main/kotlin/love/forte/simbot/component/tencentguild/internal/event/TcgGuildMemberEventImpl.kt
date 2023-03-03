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
import love.forte.simbot.component.tencentguild.TencentMember
import love.forte.simbot.component.tencentguild.event.TcgGuildMemberEvent
import love.forte.simbot.component.tencentguild.internal.TencentGuildComponentBotImpl
import love.forte.simbot.component.tencentguild.internal.TencentMemberImpl
import love.forte.simbot.event.Event
import love.forte.simbot.qguild.event.EventSignals


private fun tcgGuildMemberEventId(type: Int, botId: ID, memberId: ID, timestamp: Timestamp): ID =
    "$type$botId.$memberId.${timestamp.second}".ID


/**
 *
 * @see TcgGuildMemberEvent.Increase
 */
internal class TcgGuildMemberIncrease constructor(
    override val bot: TencentGuildComponentBotImpl,
    override val sourceEventEntity: TencentMemberInfo,
    private val _member: TencentMemberImpl,
) : TcgGuildMemberEvent.Increase() {
    override val id: ID = tcgGuildMemberEventId(0, bot.id, _member.id, timestamp)
    override val changedTime: Timestamp = Timestamp.now()
    override val timestamp: Timestamp
        get() = changedTime
    
    override suspend fun member(): TencentMember = _member
    
    override suspend fun source(): TencentGuild = _member.guild()
    
    
    internal object Parser : BaseSignalToEvent<TencentMemberInfo>() {
        override val type: EventSignals<TencentMemberInfo>
            get() = EventSignals.GuildMembers.GuildMemberAdd
        
        override val key: Event.Key<*>
            get() = Increase
        
        override suspend fun doParser(
            data: TencentMemberInfo,
            bot: TencentGuildComponentBotImpl,
        ): TcgGuildMemberIncrease {
            val guildId = data.guildId!!
            val guild = bot.findOrCreateGuildImpl(guildId)
            val member = TencentMemberImpl(bot, data, guild)
            
            return TcgGuildMemberIncrease(bot, data, member)
        }
    }
}

/**
 *
 * @see TcgGuildMemberEvent.Decrease
 */
internal class TcgGuildMemberDecrease constructor(
    override val bot: TencentGuildComponentBotImpl,
    override val sourceEventEntity: TencentMemberInfo,
    private val _member: TencentMemberImpl,
) : TcgGuildMemberEvent.Decrease() {
    override val id: ID = tcgGuildMemberEventId(2, bot.id, _member.id, timestamp)
    override val changedTime: Timestamp = Timestamp.now()
    override val timestamp: Timestamp
        get() = changedTime
    
    override suspend fun source() = _member.guild()
    
    override suspend fun member(): TencentMember = _member
    
    internal object Parser : BaseSignalToEvent<TencentMemberInfo>() {
        override val type: EventSignals<TencentMemberInfo>
            get() = EventSignals.GuildMembers.GuildMemberRemove
        
        override val key: Event.Key<*>
            get() = Decrease
        
        override suspend fun doParser(
            data: TencentMemberInfo,
            bot: TencentGuildComponentBotImpl,
        ): TcgGuildMemberDecrease {
            val guildId = data.guildId!!
            val guild = bot.findOrCreateGuildImpl(guildId)
            val member = TencentMemberImpl(bot, data, guild)
            
            return TcgGuildMemberDecrease(bot, data, member)
        }
    }
}
