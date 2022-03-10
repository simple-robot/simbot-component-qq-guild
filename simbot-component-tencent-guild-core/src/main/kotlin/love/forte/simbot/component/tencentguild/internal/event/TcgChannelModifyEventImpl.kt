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
import love.forte.simbot.component.tencentguild.*
import love.forte.simbot.component.tencentguild.event.*
import love.forte.simbot.component.tencentguild.internal.*
import love.forte.simbot.component.tencentguild.util.*
import love.forte.simbot.event.*
import love.forte.simbot.tencentguild.*
import love.forte.simbot.tencentguild.api.guild.*
import love.forte.simbot.utils.*


internal class TcgChannelCreate
constructor(
    override val sourceEventEntity: TencentChannelInfo,
    override val bot: TencentGuildComponentBotImpl,
    override val channel: TencentChannelImpl,
) : TcgChannelModifyEvent.Create() {
    override val after: TencentChannelImpl get() = channel
    override val changedTime: Timestamp = Timestamp.now()
    override val timestamp: Timestamp get() = changedTime
    override val id: ID = tcgChannelModifyId(0, bot.id, channel.id, changedTime)
    override val eventSignal: EventSignals.Guilds.ChannelCreate
        get() = EventSignals.Guilds.ChannelCreate

    override suspend fun after(): TencentChannel = after
    override suspend fun source(): TencentGuild = channel.guild()

    internal object Parser : BaseSignalToEvent<TencentChannelInfo>() {
        override val key: Event.Key<out Create> get() = Create
        override val type: EventSignals<TencentChannelInfo> = EventSignals.Guilds.ChannelCreate
        override suspend fun doParser(data: TencentChannelInfo, bot: TencentGuildComponentBotImpl): TcgChannelCreate {
            val guildId = data.guildId

            return TcgChannelCreate(
                data,
                bot,
                TencentChannelImpl(bot, data, bot.lazyValue {
                    TencentGuildImpl(bot, GetGuildApi(guildId).requestBy(bot))
                })
            )
        }
    }
}


internal class TcgChannelUpdate
constructor(
    override val sourceEventEntity: TencentChannelInfo,
    override val bot: TencentGuildComponentBotImpl,
    override val channel: TencentChannelImpl,
) : TcgChannelModifyEvent.Update() {
    override val after: TencentChannelImpl get() = channel
    override val changedTime: Timestamp = Timestamp.now()
    override val timestamp: Timestamp get() = changedTime
    override val id: ID = tcgChannelModifyId(1, bot.id, channel.id, changedTime)
    override val eventSignal: EventSignals.Guilds.ChannelUpdate
        get() = EventSignals.Guilds.ChannelUpdate

    override suspend fun source(): TencentGuild = channel.guild()
    override suspend fun after(): TencentChannel = after

    internal object Parser : BaseSignalToEvent<TencentChannelInfo>() {
        override val key: Event.Key<out Update> get() = Update
        override val type: EventSignals<TencentChannelInfo> = EventSignals.Guilds.ChannelUpdate

        override suspend fun doParser(data: TencentChannelInfo, bot: TencentGuildComponentBotImpl): TcgChannelUpdate {
            val guildId = data.guildId

            return TcgChannelUpdate(
                data,
                bot,
                TencentChannelImpl(bot, data, bot.lazyValue {
                    TencentGuildImpl(bot, GetGuildApi(guildId).requestBy(bot))
                })
            )
        }
    }
}


internal class TcgChannelDelete
constructor(
    override val sourceEventEntity: TencentChannelInfo,
    override val bot: TencentGuildComponentBotImpl,
    override val channel: TencentChannelImpl,
) : TcgChannelModifyEvent.Delete() {
    override val before: TencentChannel get() = channel
    override val changedTime: Timestamp = Timestamp.now()
    override val timestamp: Timestamp get() = changedTime
    override val id: ID = tcgChannelModifyId(2, bot.id, channel.id, changedTime)
    override val eventSignal: EventSignals.Guilds.ChannelDelete
        get() = EventSignals.Guilds.ChannelDelete

    override suspend fun before(): TencentChannel = before
    override suspend fun source(): TencentGuild = channel.guild()

    internal object Parser : BaseSignalToEvent<TencentChannelInfo>() {
        override val key: Event.Key<out Delete> get() = Delete
        override val type: EventSignals<TencentChannelInfo> = EventSignals.Guilds.ChannelDelete

        override suspend fun doParser(data: TencentChannelInfo, bot: TencentGuildComponentBotImpl): TcgChannelDelete {
            val guildId = data.guildId

            return TcgChannelDelete(
                data,
                bot,
                TencentChannelImpl(bot, data, bot.lazyValue {
                    TencentGuildImpl(bot, GetGuildApi(guildId).requestBy(bot))
                })
            )
        }
    }
}

private fun tcgChannelModifyId(t: Int, sourceBot: ID, sourceChannel: ID, timestamp: Timestamp): ID =
    "$t$sourceBot.${timestamp.second}.$sourceChannel".ID
