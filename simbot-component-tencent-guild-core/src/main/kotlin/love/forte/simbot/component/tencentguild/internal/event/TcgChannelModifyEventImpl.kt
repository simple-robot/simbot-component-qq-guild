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

package love.forte.simbot.component.tencentguild.internal.event

import love.forte.simbot.ID
import love.forte.simbot.Timestamp
import love.forte.simbot.component.tencentguild.event.TcgChannelModifyEvent
import love.forte.simbot.component.tencentguild.internal.TencentChannelImpl
import love.forte.simbot.component.tencentguild.internal.TencentGuildComponentBotImpl
import love.forte.simbot.event.Event
import love.forte.simbot.tencentguild.EventSignals
import love.forte.simbot.tencentguild.TencentChannelInfo


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
