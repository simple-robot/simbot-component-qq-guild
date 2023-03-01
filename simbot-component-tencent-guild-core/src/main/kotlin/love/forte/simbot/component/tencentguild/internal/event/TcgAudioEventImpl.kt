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
import love.forte.simbot.component.tencentguild.event.TcgAudioEvent
import love.forte.simbot.component.tencentguild.internal.TencentChannelImpl
import love.forte.simbot.component.tencentguild.internal.TencentGuildComponentBotImpl
import love.forte.simbot.event.Event
import love.forte.simbot.tencentguild.EventSignals
import love.forte.simbot.tencentguild.TencentAudioAction


private fun tcgAudioEventId(
    type: Int, guildId: ID, channelId: ID, timestamp: Timestamp,
): ID = "$type.$guildId.$channelId.${timestamp.second}".ID


/**
 *
 */
internal class TcgAudioStart(
    override val bot: TencentGuildComponentBotImpl,
    override val sourceEventEntity: TencentAudioAction,
    override val channelInternal: TencentChannelImpl,
) : TcgAudioEvent.Start() {
    override val id: ID = tcgAudioEventId(0, sourceEventEntity.guildId, sourceEventEntity.channelId, timestamp)
    
    internal object Parser : BaseSignalToEvent<TencentAudioAction>() {
        override val type: EventSignals<TencentAudioAction>
            get() = EventSignals.AudioAction.AudioStart
        override val key: Event.Key<*>
            get() = Start
        
        override suspend fun doParser(data: TencentAudioAction, bot: TencentGuildComponentBotImpl): Event {
            val channel = bot.findOrCreateChannelImpl(data.guildId, data.channelId)
            return TcgAudioStart(bot, data, channel)
        }
        
    }
}

/**
 *
 */
internal class TcgAudioFinish(
    override val bot: TencentGuildComponentBotImpl,
    override val sourceEventEntity: TencentAudioAction,
    override val channelInternal: TencentChannelImpl,
) : TcgAudioEvent.Finish() {
    override val id: ID = tcgAudioEventId(1, sourceEventEntity.guildId, sourceEventEntity.channelId, timestamp)
    
    internal object Parser : BaseSignalToEvent<TencentAudioAction>() {
        override val type: EventSignals<TencentAudioAction>
            get() = EventSignals.AudioAction.AudioFinish
        override val key: Event.Key<*>
            get() = Finish
        
        override suspend fun doParser(data: TencentAudioAction, bot: TencentGuildComponentBotImpl): Event {
            val channel = bot.findOrCreateChannelImpl(data.guildId, data.channelId)
            return TcgAudioFinish(bot, data, channel)
        }
    }
}

/**
 *
 */
internal class TcgAudioOnMic(
    override val bot: TencentGuildComponentBotImpl,
    override val sourceEventEntity: TencentAudioAction,
    override val channelInternal: TencentChannelImpl,
) : TcgAudioEvent.OnMic() {
    override val id: ID = tcgAudioEventId(2, sourceEventEntity.guildId, sourceEventEntity.channelId, timestamp)
    
    internal object Parser : BaseSignalToEvent<TencentAudioAction>() {
        override val type: EventSignals<TencentAudioAction>
            get() = EventSignals.AudioAction.AudioOnMic
        override val key: Event.Key<*>
            get() = OnMic
        
        override suspend fun doParser(data: TencentAudioAction, bot: TencentGuildComponentBotImpl): Event {
            val channel = bot.findOrCreateChannelImpl(data.guildId, data.channelId)
            return TcgAudioOnMic(bot, data, channel)
        }
    }
}

/**
 *
 */
internal class TcgAudioOffMic(
    override val bot: TencentGuildComponentBotImpl,
    override val sourceEventEntity: TencentAudioAction,
    override val channelInternal: TencentChannelImpl,
) : TcgAudioEvent.OffMic() {
    override val id: ID = tcgAudioEventId(3, sourceEventEntity.guildId, sourceEventEntity.channelId, timestamp)
    
    internal object Parser : BaseSignalToEvent<TencentAudioAction>() {
        override val type: EventSignals<TencentAudioAction>
            get() = EventSignals.AudioAction.AudioOffMic
        override val key: Event.Key<*>
            get() = OffMic
        
        override suspend fun doParser(data: TencentAudioAction, bot: TencentGuildComponentBotImpl): Event {
            val channel = bot.findOrCreateChannelImpl(data.guildId, data.channelId)
            return TcgAudioOffMic(bot, data, channel)
        }
    }
}
