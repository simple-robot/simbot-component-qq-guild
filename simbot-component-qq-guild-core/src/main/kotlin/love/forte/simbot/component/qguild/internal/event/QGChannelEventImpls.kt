/*
 * Copyright (c) 2022-2023. ForteScarlet.
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
import love.forte.simbot.component.qguild.QGChannel
import love.forte.simbot.component.qguild.QGGuild
import love.forte.simbot.component.qguild.event.QGChannelCreateEvent
import love.forte.simbot.component.qguild.event.QGChannelDeleteEvent
import love.forte.simbot.component.qguild.event.QGChannelUpdateEvent
import love.forte.simbot.component.qguild.internal.QGBotImpl
import love.forte.simbot.component.qguild.internal.QGChannelImpl
import love.forte.simbot.qguild.event.EventChannel
import kotlin.random.Random
import kotlin.random.nextUInt


internal class QGChannelCreateEventImpl(
    override val eventRaw: String,
    override val sourceEventEntity: EventChannel,
    override val bot: QGBotImpl,
    private val _channel: QGChannelImpl,
) : QGChannelCreateEvent() {
    override val id: ID = tcgChannelModifyId(0, bot.id, sourceEventEntity.id, changedTime)
    override val operatorId: ID = sourceEventEntity.opUserId.ID
    override suspend fun channel(): QGChannel = _channel
    override suspend fun source(): QGGuild = _channel.guild()
}


internal class QGChannelUpdateEventImpl(
    override val eventRaw: String,
    override val sourceEventEntity: EventChannel,
    override val bot: QGBotImpl,
    private val _channel: QGChannelImpl,
) : QGChannelUpdateEvent() {
    override val id: ID = tcgChannelModifyId(1, bot.id, sourceEventEntity.id, changedTime)
    override val operatorId: ID = sourceEventEntity.opUserId.ID
    override suspend fun channel(): QGChannel = _channel
    override suspend fun source(): QGGuild = _channel.guild()
}


internal class QGChannelDeleteEventImpl(
    override val eventRaw: String,
    override val sourceEventEntity: EventChannel,
    override val bot: QGBotImpl,
    private val _channel: QGChannelImpl
) : QGChannelDeleteEvent() {
    override val id: ID = tcgChannelModifyId(2, bot.id, sourceEventEntity.id, changedTime)
    override val operatorId: ID = sourceEventEntity.opUserId.ID
    override suspend fun channel(): QGChannel = _channel
    override suspend fun source(): QGGuild = _channel.guild()
}



//internal class QGChannelCategoryCreateEventImpl(
//    override val eventRaw: String,
//    override val sourceEventEntity: EventChannel,
//    override val bot: QGBotImpl,
//    private val _category: QGChannelCategoryImpl,
//    private val _guild: QGGuildImpl,
//) : QGChannelCategoryCreateEvent() {
//    override val id: ID = tcgChannelModifyId(3, bot.id, sourceEventEntity.id, changedTime)
//    override val operatorId: ID = sourceEventEntity.opUserId.ID
//    override suspend fun category(): QGChannelCategory = _category
//    override suspend fun guild(): QGGuild = _guild
//}
//
//
//internal class QGChannelCategoryUpdateEventImpl(
//    override val eventRaw: String,
//    override val sourceEventEntity: EventChannel,
//    override val bot: QGBotImpl,
//    private val _category: QGChannelCategoryImpl,
//    private val _guild: QGGuildImpl,
//) : QGChannelCategoryUpdateEvent() {
//    override val id: ID = tcgChannelModifyId(4, bot.id, sourceEventEntity.id, changedTime)
//    override val operatorId: ID = sourceEventEntity.opUserId.ID
//    override suspend fun category(): QGChannelCategory = _category
//    override suspend fun guild(): QGGuild = _guild
//}
//
//
//internal class QGChannelCategoryDeleteEventImpl(
//    override val eventRaw: String,
//    override val sourceEventEntity: EventChannel,
//    override val bot: QGBotImpl,
//    private val _guild: QGGuildImpl,
//) : QGChannelCategoryDeleteEvent() {
//    override val id: ID = tcgChannelModifyId(5, bot.id, sourceEventEntity.id, changedTime)
//    override val operatorId: ID = sourceEventEntity.opUserId.ID
//    override suspend fun guild(): QGGuildImpl = _guild
//
//}



private fun tcgChannelModifyId(t: Int, sourceBot: ID, sourceChannel: String, timestamp: Timestamp): ID =
    "$t$sourceBot.${timestamp.second}.$sourceChannel.${Random.nextUInt()}".ID
