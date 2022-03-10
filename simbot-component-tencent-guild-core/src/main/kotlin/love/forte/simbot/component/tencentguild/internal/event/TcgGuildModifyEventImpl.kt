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

import love.forte.simbot.*
import love.forte.simbot.component.tencentguild.event.*
import love.forte.simbot.component.tencentguild.internal.*
import love.forte.simbot.tencentguild.*


internal class TcgGuildCreate(
    override val sourceEventEntity: TencentGuildInfo,
    override val source: TencentGuildComponentBotImpl,
    override val after: TencentGuildImpl
) : TcgGuildModifyEvent.Create() {
    override val changedTime: Timestamp = Timestamp.now()
    override val timestamp: Timestamp get() = changedTime
    override val bot: TencentGuildComponentBotImpl get() = source
    override val id: ID = tcgGuildModifyId(0, source.id, after.id, timestamp)

    internal object Parser : BaseSignalToEvent<TencentGuildInfo>() {
        override val key = Create
        override val type: EventSignals<TencentGuildInfo>
            get() = EventSignals.Guilds.GuildCreate

        override suspend fun doParser(data: TencentGuildInfo, bot: TencentGuildComponentBotImpl): TcgGuildCreate {
            return TcgGuildCreate(data, bot, TencentGuildImpl(bot, data))
        }
    }
}


internal class TcgGuildUpdate(
    override val sourceEventEntity: TencentGuildInfo,
    override val source: TencentGuildComponentBotImpl,
    override val after: TencentGuildImpl
) : TcgGuildModifyEvent.Update() {
    override val changedTime: Timestamp = Timestamp.now()
    override val timestamp: Timestamp get() = changedTime
    override val bot: TencentGuildComponentBotImpl get() = source
    override val id: ID = tcgGuildModifyId(1, source.id, after.id, timestamp)

    internal object Parser : BaseSignalToEvent<TencentGuildInfo>() {
        override val key = Update
        override val type: EventSignals<TencentGuildInfo>
            get() = EventSignals.Guilds.GuildUpdate

        override suspend fun doParser(data: TencentGuildInfo, bot: TencentGuildComponentBotImpl): TcgGuildUpdate {
            return TcgGuildUpdate(data, bot, TencentGuildImpl(bot, data))
        }
    }
}


internal class TcgGuildDelete(
    override val sourceEventEntity: TencentGuildInfo,
    override val source: TencentGuildComponentBotImpl,
    override val before: TencentGuildImpl
) : TcgGuildModifyEvent.Delete() {
    override val changedTime: Timestamp = Timestamp.now()
    override val timestamp: Timestamp get() = changedTime
    override val bot: TencentGuildComponentBotImpl get() = source
    override val id: ID = tcgGuildModifyId(2, source.id, before.id, timestamp)

    internal object Parser : BaseSignalToEvent<TencentGuildInfo>() {
        override val key = Delete
        override val type: EventSignals<TencentGuildInfo>
            get() = EventSignals.Guilds.GuildDelete

        override suspend fun doParser(data: TencentGuildInfo, bot: TencentGuildComponentBotImpl): TcgGuildDelete {
            return TcgGuildDelete(data, bot, TencentGuildImpl(bot, data))
        }
    }
}


private fun tcgGuildModifyId(t: Int, sourceBot: ID, sourceGuild: ID, timestamp: Timestamp): ID =
    "$t$sourceBot.${timestamp.second}.$sourceGuild".ID
