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
import love.forte.simbot.component.tencentguild.event.TcgChannelModifyEvent
import love.forte.simbot.component.tencentguild.internal.TencentChannelImpl
import love.forte.simbot.component.tencentguild.internal.TencentGuildComponentBotImpl
import love.forte.simbot.event.Event
import love.forte.simbot.qguild.TencentChannelInfo
import love.forte.simbot.qguild.event.EventSignals


internal class TcgChannelCreate(
    override val sourceEventEntity: TencentChannelInfo,
    override val bot: TencentGuildComponentBotImpl,
    override val sourceInternal: TencentChannelImpl,
) : TcgChannelModifyEvent.Create() {
    override val id: ID = tcgChannelModifyId(0, bot.id, sourceInternal.id, changedTime)
    override val eventSignal: EventSignals.Guilds.ChannelCreate
        get() = EventSignals.Guilds.ChannelCreate
    
    
    internal object Parser : BaseSignalToEvent<TencentChannelInfo>() {
        override val key: Event.Key<out Create> get() = Create
        override val type: EventSignals<TencentChannelInfo> = EventSignals.Guilds.ChannelCreate
        override suspend fun doParser(data: TencentChannelInfo, bot: TencentGuildComponentBotImpl): TcgChannelCreate {
            val channel = bot.findOrCreateChannelImpl(data.guildId, data.id)
            return TcgChannelCreate(data, bot, channel)
        }
    }
}


internal class TcgChannelUpdate
constructor(
    override val sourceEventEntity: TencentChannelInfo,
    override val bot: TencentGuildComponentBotImpl,
    override val sourceInternal: TencentChannelImpl,
) : TcgChannelModifyEvent.Update() {
    override val id: ID = tcgChannelModifyId(1, bot.id, sourceInternal.id, changedTime)
    override val eventSignal: EventSignals.Guilds.ChannelUpdate
        get() = EventSignals.Guilds.ChannelUpdate
    
    
    internal object Parser : BaseSignalToEvent<TencentChannelInfo>() {
        override val key: Event.Key<out Update> get() = Update
        override val type: EventSignals<TencentChannelInfo> = EventSignals.Guilds.ChannelUpdate
        
        override suspend fun doParser(data: TencentChannelInfo, bot: TencentGuildComponentBotImpl): TcgChannelUpdate {
            val channel = bot.findOrCreateChannelImpl(data.guildId, data.id)
            return TcgChannelUpdate(data, bot, channel)
        }
    }
}


internal class TcgChannelDelete
constructor(
    override val sourceEventEntity: TencentChannelInfo,
    override val bot: TencentGuildComponentBotImpl,
    override val sourceInternal: TencentChannelImpl,
) : TcgChannelModifyEvent.Delete() {
    override val id: ID = tcgChannelModifyId(2, bot.id, sourceInternal.id, changedTime)
    override val eventSignal: EventSignals.Guilds.ChannelDelete
        get() = EventSignals.Guilds.ChannelDelete
    
    internal object Parser : BaseSignalToEvent<TencentChannelInfo>() {
        override val key: Event.Key<out Delete> get() = Delete
        override val type: EventSignals<TencentChannelInfo> = EventSignals.Guilds.ChannelDelete
        
        override suspend fun doParser(data: TencentChannelInfo, bot: TencentGuildComponentBotImpl): TcgChannelDelete {
            val channel = bot.findOrCreateChannelImpl(data.guildId, data.id)
            return TcgChannelDelete(data, bot, channel)
        }
    }
}

private fun tcgChannelModifyId(t: Int, sourceBot: ID, sourceChannel: ID, timestamp: Timestamp): ID =
    "$t$sourceBot.${timestamp.second}.$sourceChannel".ID
