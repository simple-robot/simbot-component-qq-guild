/*
 *  Copyright (c) 2022-2022 ForteScarlet <ForteScarlet@163.com>
 *
 *  根据 GNU LESSER GENERAL PUBLIC LICENSE 3 获得许可；
 *  除非遵守许可，否则您不得使用此文件。
 *  您可以在以下网址获取许可证副本：
 *
 *       https://www.gnu.org/licenses/lgpl-3.0-standalone.html
 *
 *   有关许可证下的权限和限制的具体语言，请参见许可证。
 */

package love.forte.simbot.component.tencentguild.internal.event

import love.forte.simbot.ID
import love.forte.simbot.Timestamp
import love.forte.simbot.component.tencentguild.event.TcgGuildModifyEvent
import love.forte.simbot.component.tencentguild.internal.TencentGuildBotImpl
import love.forte.simbot.component.tencentguild.internal.TencentGuildImpl
import love.forte.simbot.event.Event
import love.forte.simbot.tencentguild.EventSignals
import love.forte.simbot.tencentguild.TencentGuildInfo


internal class TcgGuildCreate(
    override val sourceEventEntity: TencentGuildInfo,
    override val source: TencentGuildBotImpl,
    override val after: TencentGuildImpl
) : TcgGuildModifyEvent.Create() {
    override val changedTime: Timestamp = Timestamp.now()
    override val timestamp: Timestamp get() = changedTime
    override val bot: TencentGuildBotImpl get() = source
    override val metadata: Event.Metadata = TcgGuildModifyMetadata(0, source.id, after.id, timestamp)

    internal object Parser : BaseSignalToEvent<TencentGuildInfo>() {
        override val key = Create
        override val type: EventSignals<TencentGuildInfo>
            get() = EventSignals.Guilds.GuildCreate

        override suspend fun doParser(data: TencentGuildInfo, bot: TencentGuildBotImpl): TcgGuildCreate {
            return TcgGuildCreate(data, bot, TencentGuildImpl(bot, data))
        }
    }
}


internal class TcgGuildUpdate(
    override val sourceEventEntity: TencentGuildInfo,
    override val source: TencentGuildBotImpl,
    override val after: TencentGuildImpl
) : TcgGuildModifyEvent.Update() {
    override val changedTime: Timestamp = Timestamp.now()
    override val timestamp: Timestamp get() = changedTime
    override val bot: TencentGuildBotImpl get() = source
    override val metadata: Event.Metadata = TcgGuildModifyMetadata(1, source.id, after.id, timestamp)

    internal object Parser : BaseSignalToEvent<TencentGuildInfo>() {
        override val key = Update
        override val type: EventSignals<TencentGuildInfo>
            get() = EventSignals.Guilds.GuildUpdate

        override suspend fun doParser(data: TencentGuildInfo, bot: TencentGuildBotImpl): TcgGuildUpdate {
            return TcgGuildUpdate(data, bot, TencentGuildImpl(bot, data))
        }
    }
}


internal class TcgGuildDelete(
    override val sourceEventEntity: TencentGuildInfo,
    override val source: TencentGuildBotImpl,
    override val before: TencentGuildImpl
) : TcgGuildModifyEvent.Delete() {
    override val changedTime: Timestamp = Timestamp.now()
    override val timestamp: Timestamp get() = changedTime
    override val bot: TencentGuildBotImpl get() = source
    override val metadata: Event.Metadata = TcgGuildModifyMetadata(2, source.id, before.id, timestamp)

    internal object Parser : BaseSignalToEvent<TencentGuildInfo>() {
        override val key = Delete
        override val type: EventSignals<TencentGuildInfo>
            get() = EventSignals.Guilds.GuildDelete

        override suspend fun doParser(data: TencentGuildInfo, bot: TencentGuildBotImpl): TcgGuildDelete {
            return TcgGuildDelete(data, bot, TencentGuildImpl(bot, data))
        }
    }
}


private class TcgGuildModifyMetadata(t: Int, sourceBot: ID, sourceGuild: ID, timestamp: Timestamp) : Event.Metadata {
    override val id: ID = "$t$sourceBot.${timestamp.second}.$sourceGuild".ID
}
