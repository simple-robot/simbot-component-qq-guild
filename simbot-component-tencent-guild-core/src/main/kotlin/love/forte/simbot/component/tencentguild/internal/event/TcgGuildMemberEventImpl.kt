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
import love.forte.simbot.action.*
import love.forte.simbot.component.tencentguild.*
import love.forte.simbot.component.tencentguild.event.*
import love.forte.simbot.component.tencentguild.internal.*
import love.forte.simbot.component.tencentguild.util.*
import love.forte.simbot.event.*
import love.forte.simbot.tencentguild.*
import love.forte.simbot.tencentguild.api.guild.*
import love.forte.simbot.utils.*


private fun tcgGuildMemberEventId(type: Int, botId: ID, memberId: ID, timestamp: Timestamp): ID =
    "$type$botId.$memberId.${timestamp.second}".ID


/**
 *
 * @see TcgGuildMemberEvent.Increase
 */
internal class TcgGuildMemberIncrease constructor(
    override val bot: TencentGuildBotImpl,
    override val sourceEventEntity: TencentMemberInfo,
    override val target: TencentMemberImpl,
) : TcgGuildMemberEvent.Increase() {

    override val changedTime: Timestamp = Timestamp.now()

    override val timestamp: Timestamp
        get() = changedTime

    /**
     * 离开频道是主动的。（无法区分）
     */
    override val actionType: ActionType
        get() = ActionType.PROACTIVE


    override suspend fun source(): TencentGuild = target.organization()

    override val id: ID = tcgGuildMemberEventId(0, bot.id, target.id, timestamp)

    override val visibleScope: Event.VisibleScope get() = Event.VisibleScope.INTERNAL

    override suspend fun guild(): TencentGuild = target.organization()
    override suspend fun member(): TencentMember = target


    internal object Parser : BaseSignalToEvent<TencentMemberInfo>() {
        override val type: EventSignals<TencentMemberInfo>
            get() = EventSignals.GuildMembers.GuildMemberAdd

        override val key: Event.Key<*>
            get() = Increase

        override suspend fun doParser(data: TencentMemberInfo, bot: TencentGuildBotImpl): TcgGuildMemberIncrease {
            val guildId = data.guildId!!
            val member = TencentMemberImpl(
                bot, data,
                bot.lazyValue { TencentGuildImpl(bot, GetGuildApi(guildId).requestBy(bot)) }
            )
            return TcgGuildMemberIncrease(bot, data, member)
        }
    }
}

/**
 *
 * @see TcgGuildMemberEvent.Decrease
 */
internal class TcgGuildMemberDecrease constructor(
    override val bot: TencentGuildBotImpl,
    override val sourceEventEntity: TencentMemberInfo,
    override val target: TencentMemberImpl,
) : TcgGuildMemberEvent.Decrease() {

    override val changedTime: Timestamp = Timestamp.now()

    override val timestamp: Timestamp
        get() = changedTime

    /**
     * 离开频道是主动的。（无法区分）
     */
    override val actionType: ActionType
        get() = ActionType.PROACTIVE

    override suspend fun source(): TencentGuild = target.organization()


    override val id: ID = tcgGuildMemberEventId(2, bot.id, target.id, timestamp)

    override val visibleScope: Event.VisibleScope get() = Event.VisibleScope.INTERNAL

    override suspend fun guild(): TencentGuild = target.organization()

    override suspend fun member(): TencentMember = target


    internal object Parser : BaseSignalToEvent<TencentMemberInfo>() {
        override val type: EventSignals<TencentMemberInfo>
            get() = EventSignals.GuildMembers.GuildMemberRemove

        override val key: Event.Key<*>
            get() = Decrease

        override suspend fun doParser(data: TencentMemberInfo, bot: TencentGuildBotImpl): TcgGuildMemberDecrease {
            val guildId = data.guildId!!
            val member = TencentMemberImpl(
                bot, data,
                bot.lazyValue { TencentGuildImpl(bot, GetGuildApi(guildId).requestBy(bot)) }
            )
            return TcgGuildMemberDecrease(bot, data, member)
        }
    }
}