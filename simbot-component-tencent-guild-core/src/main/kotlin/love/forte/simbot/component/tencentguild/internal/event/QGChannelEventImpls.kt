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
import love.forte.simbot.component.tencentguild.QGChannel
import love.forte.simbot.component.tencentguild.event.QGChannelCreateEvent
import love.forte.simbot.component.tencentguild.event.QGChannelDeleteEvent
import love.forte.simbot.component.tencentguild.event.QGChannelUpdateEvent
import love.forte.simbot.component.tencentguild.internal.QGBotImpl
import love.forte.simbot.component.tencentguild.internal.QGChannelImpl
import love.forte.simbot.component.tencentguild.internal.QGGuildImpl
import love.forte.simbot.qguild.event.EventChannel
import kotlin.random.Random
import kotlin.random.nextUInt


internal class QGChannelCreateEventImpl(
    override val sourceEventEntity: EventChannel,
    override val bot: QGBotImpl,
    private val _channel: QGChannelImpl,
) : QGChannelCreateEvent() {
    override val id: ID = tcgChannelModifyId(0, bot.id, sourceEventEntity.id, changedTime)
    override val operatorId: ID = sourceEventEntity.opUserId.ID
    override suspend fun channel(): QGChannel = _channel
}


internal class QGChannelUpdateEventImpl(
    override val sourceEventEntity: EventChannel,
    override val bot: QGBotImpl,
    private val _channel: QGChannelImpl,
) : QGChannelUpdateEvent() {
    override val id: ID = tcgChannelModifyId(1, bot.id, sourceEventEntity.id, changedTime)
    override val operatorId: ID = sourceEventEntity.opUserId.ID
    override suspend fun channel(): QGChannel = _channel
}


internal class QGChannelDeleteEventImpl(
    override val sourceEventEntity: EventChannel,
    override val bot: QGBotImpl,
    private val _guild: QGGuildImpl,
) : QGChannelDeleteEvent() {
    override val id: ID = tcgChannelModifyId(2, bot.id, sourceEventEntity.id, changedTime)
    override val operatorId: ID = sourceEventEntity.opUserId.ID
    override suspend fun source(): QGGuildImpl = _guild
}

private fun tcgChannelModifyId(t: Int, sourceBot: ID, sourceChannel: String, timestamp: Timestamp): ID =
    "$t$sourceBot.${timestamp.second}.$sourceChannel.${Random.nextUInt()}".ID
