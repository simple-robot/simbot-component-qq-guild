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

package love.forte.simbot.component.qguild.internal.event

import love.forte.simbot.FragileSimbotApi
import love.forte.simbot.ID
import love.forte.simbot.Timestamp
import love.forte.simbot.component.qguild.QGGuild
import love.forte.simbot.component.qguild.event.QGGuildCreateEvent
import love.forte.simbot.component.qguild.event.QGGuildDeleteEvent
import love.forte.simbot.component.qguild.event.QGGuildUpdateEvent
import love.forte.simbot.component.qguild.internal.QGBotImpl
import love.forte.simbot.component.qguild.internal.QGGuildImpl
import love.forte.simbot.qguild.event.EventGuild
import kotlin.random.Random
import kotlin.random.nextUInt

internal class QGGuildCreateEventImpl(
    override val sourceEventEntity: EventGuild,
    override val bot: QGBotImpl,
    private val _guild: QGGuildImpl,
) : QGGuildCreateEvent() {
    override val id: ID = tcgGuildModifyId(0, bot.id, sourceEventEntity.id, timestamp)
    override val operatorId: ID = sourceEventEntity.opUserId.ID
    override val timestamp: Timestamp get() = changedTime
    override suspend fun guild(): QGGuild = _guild
}


internal class QGGuildUpdateEventImpl(
    override val sourceEventEntity: EventGuild,
    override val bot: QGBotImpl,
    private val _guild: QGGuildImpl,
) : QGGuildUpdateEvent() {
    override val id: ID = tcgGuildModifyId(1, bot.id, sourceEventEntity.id, timestamp)
    override val operatorId: ID = sourceEventEntity.opUserId.ID
    override val timestamp: Timestamp get() = changedTime
    override suspend fun guild(): QGGuild = _guild
}


internal class QGGuildDeleteEventImpl(
    override val sourceEventEntity: EventGuild,
    override val bot: QGBotImpl,
    private val _guild: QGGuildImpl?,
) : QGGuildDeleteEvent() {
    override val id: ID = tcgGuildModifyId(2, bot.id, sourceEventEntity.id, timestamp)
    override val operatorId: ID = sourceEventEntity.opUserId.ID

    @FragileSimbotApi
    override val guild: QGGuild? get() = _guild
}


private fun tcgGuildModifyId(t: Int, sourceBot: ID, sourceGuild: String, timestamp: Timestamp): ID =
    "$t$sourceBot.${timestamp.second}.$sourceGuild.${Random.nextUInt()}".ID
