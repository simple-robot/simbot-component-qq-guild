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

import kotlinx.serialization.json.*
import love.forte.simbot.*
import love.forte.simbot.component.tencentguild.internal.*
import love.forte.simbot.event.*
import love.forte.simbot.tencentguild.*


internal interface SignalToEvent {
    val key: Event.Key<*>
    suspend operator fun invoke(
        bot: TencentGuildBotImpl,
        decoder: Json,
        dispatch: Signal.Dispatch, decoded: () -> Any
    ): Event
}

internal abstract class BaseSignalToEvent<S : Any> : SignalToEvent {
    abstract val type: EventSignals<S>
    override suspend fun invoke(
        bot: TencentGuildBotImpl, decoder: Json,
        dispatch: Signal.Dispatch, decoded: () -> Any
    ): Event {
        Simbot.check(dispatch.type == type.type) {
            "Event type does not match: ${dispatch.type} != ${type.type}"
        }

        @Suppress("UNCHECKED_CAST")
        return doParser(decoded() as S, bot)
    }

    protected abstract suspend fun doParser(data: S, bot: TencentGuildBotImpl): Event
}

internal val eventSignalParsers =
    mapOf<EventSignals<*>, SignalToEvent>(
        EventSignals.Guilds.GuildCreate to TcgGuildCreate.Parser,
        EventSignals.Guilds.GuildUpdate to TcgGuildUpdate.Parser,
        EventSignals.Guilds.GuildDelete to TcgGuildDelete.Parser,

        EventSignals.Guilds.ChannelCreate to TcgChannelCreate.Parser,
        EventSignals.Guilds.ChannelUpdate to TcgChannelUpdate.Parser,
        EventSignals.Guilds.ChannelDelete to TcgChannelDelete.Parser,

        EventSignals.GuildMembers.GuildMemberAdd to TcgGuildMemberIncrease.Parser,
        // EventSignals.GuildMembers.GuildMemberUpdate to TODO(),
        EventSignals.GuildMembers.GuildMemberRemove to TcgGuildMemberDecrease.Parser,

        // EventSignals.DirectMessage.DirectMessageCreate to TODO(),

        EventSignals.AudioAction.AudioStart to TcgAudioStart.Parser,
        EventSignals.AudioAction.AudioFinish to TcgAudioFinish.Parser,
        EventSignals.AudioAction.AudioOnMic to TcgAudioOnMic.Parser,
        EventSignals.AudioAction.AudioOffMic to TcgAudioOffMic.Parser,

        EventSignals.AtMessages.AtMessageCreate to TcgChannelAtMessageEventImpl.Parser,
    )

