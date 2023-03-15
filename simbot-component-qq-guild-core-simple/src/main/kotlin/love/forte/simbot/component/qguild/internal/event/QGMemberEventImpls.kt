/*
 * Copyright (c) 2023. ForteScarlet.
 *
 * This file is part of simbot-component-qq-guild.
 *
 * simbot-component-qq-guild is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * simbot-component-qq-guild is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with simbot-component-qq-guild. If not, see <https://www.gnu.org/licenses/>.
 */

package love.forte.simbot.component.qguild.internal.event

import love.forte.simbot.ID
import love.forte.simbot.Timestamp
import love.forte.simbot.action.ActionType
import love.forte.simbot.component.qguild.QGBot
import love.forte.simbot.component.qguild.event.QGMemberAddEvent
import love.forte.simbot.component.qguild.event.QGMemberRemoveEvent
import love.forte.simbot.component.qguild.event.QGMemberUpdateEvent
import love.forte.simbot.component.qguild.internal.QGGuildImpl
import love.forte.simbot.component.qguild.internal.QGMemberImpl
import love.forte.simbot.qguild.QQGuildApiException
import love.forte.simbot.qguild.copyCurrent
import love.forte.simbot.qguild.event.EventMember
import love.forte.simbot.qguild.isUnauthorized
import kotlin.random.Random
import kotlin.random.nextUInt


internal class QGMemberAddEventImpl(
    override val bot: QGBot,
    override val eventRaw: String,
    override val sourceEventEntity: EventMember,
    private val _member: QGMemberImpl,
) : QGMemberAddEvent() {
    override val actionType: ActionType
        get() = if (sourceEventEntity.opUserId == sourceEventEntity.user.id) ActionType.PROACTIVE else ActionType.PASSIVE

    override val id: ID = memberEventId(0, bot.id, sourceEventEntity.user.id, timestamp)
    override suspend fun member(): QGMemberImpl = _member
    override suspend fun operator(): QGMemberImpl? {
        return try {
            _member._guild.member(sourceEventEntity.opUserId)
        } catch (apiEx: QQGuildApiException) {
            // process no auth
            if (apiEx.isUnauthorized) null else throw apiEx.copyCurrent()
        }
    }

    override suspend fun guild(): QGGuildImpl = _member._guild
}

internal class QGMemberUpdateEventImpl(
    override val bot: QGBot,
    override val eventRaw: String,
    override val sourceEventEntity: EventMember,
    private val _member: QGMemberImpl,
) : QGMemberUpdateEvent() {
    override val id: ID = memberEventId(1, bot.id, sourceEventEntity.user.id, timestamp)
    override suspend fun member(): QGMemberImpl = _member
    override suspend fun operator(): QGMemberImpl? {
        return try {
            _member._guild.member(sourceEventEntity.opUserId)
        } catch (apiEx: QQGuildApiException) {
            // process no auth
            if (apiEx.isUnauthorized) null else throw apiEx.copyCurrent()
        }
    }

    override suspend fun guild(): QGGuildImpl = _member._guild
}

internal class QGMemberRemoveEventImpl(
    override val bot: QGBot,
    override val eventRaw: String,
    override val sourceEventEntity: EventMember,
    private val _member: QGMemberImpl,
) : QGMemberRemoveEvent() {
    override val actionType: ActionType
        get() = if (sourceEventEntity.opUserId == sourceEventEntity.user.id) ActionType.PROACTIVE else ActionType.PASSIVE

    override val id: ID = memberEventId(2, bot.id, sourceEventEntity.user.id, timestamp)
    override suspend fun member(): QGMemberImpl = _member
    override suspend fun operator(): QGMemberImpl? {
        return try {
            _member._guild.member(sourceEventEntity.opUserId)
        } catch (apiEx: QQGuildApiException) {
            // process no auth
            if (apiEx.isUnauthorized) null else throw apiEx.copyCurrent()
        }
    }

    override suspend fun guild(): QGGuildImpl = _member._guild
}

private fun memberEventId(t: Int, sourceBot: ID, sourceUserId: String, timestamp: Timestamp): ID =
    "$t$sourceBot.${timestamp.second}.$sourceUserId.${Random.nextUInt()}".ID
