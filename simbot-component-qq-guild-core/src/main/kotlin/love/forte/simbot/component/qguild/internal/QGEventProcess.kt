/*
 * Copyright (c) 2022-2023. ForteScarlet.
 *
 * This file is part of simbot-component-qq-guild.
 *
 * simbot-component-qq-guild is free software: you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * simbot-component-qq-guild is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with simbot-component-qq-guild.
 * If not, see <https://www.gnu.org/licenses/>.
 */

package love.forte.simbot.component.qguild.internal

import kotlinx.coroutines.DisposableHandle
import love.forte.simbot.ID
import love.forte.simbot.component.qguild.event.*
import love.forte.simbot.component.qguild.internal.QGGuildImpl.Companion.qgGuild
import love.forte.simbot.component.qguild.internal.event.*
import love.forte.simbot.component.qguild.util.requestBy
import love.forte.simbot.event.Event
import love.forte.simbot.qguild.api.channel.GetChannelApi
import love.forte.simbot.qguild.event.*
import love.forte.simbot.qguild.model.isCategory

private fun QGBotImpl.guild0(eventGuild: EventGuild): QGGuildImpl = qgGuild(this, eventGuild)

private suspend fun QGBotImpl.channel0(eventChannel: EventChannel): QGChannelImpl {
    val channel = GetChannelApi.create(eventChannel.id).requestBy(this)
    return QGChannelImpl(this.inGuild(channel.guildId), channel)
}

private fun QGBotImpl.member0(eventMember: EventMember): QGMemberImpl =
    QGMemberImpl(this, eventMember, eventMember.guildId.ID)

/**
 * simbot针对QQ频道注册的时候只注册一个普通处理函数。
 */
internal fun QGBotImpl.registerEventProcessor(): DisposableHandle {
    val bot = this
    return source.registerProcessor { raw ->
        when (val event = this) {
            //region Guild相关
            is GuildCreate -> {
                val guild = guild0(event.data)
                pushEvent(QGGuildCreateEvent) {
                    QGGuildCreateEventImpl(raw, event.data, bot, guild)
                }
            }

            is GuildUpdate -> {
                val guild = guild0(event.data)
                pushEvent(QGGuildUpdateEvent) {
                    QGGuildUpdateEventImpl(raw, event.data, bot, guild)
                }
            }

            is GuildDelete -> {
                val guild = guild0(event.data)
                pushEvent(QGGuildDeleteEvent) {
                    QGGuildDeleteEventImpl(raw, event.data, bot, guild)
                }
            }
            //endregion

            //region Channel相关
            is ChannelCreate -> {
                if (event.data.type.isCategory) {
                    bot.logger.warn(
                        "Received category create event [raw={}]. report this log to issues https://github.com/simple-robot/simbot-component-qq-guild/issues/new/choose",
                        raw
                    )
                }

                val channel = channel0(event.data)
                pushEvent(QGChannelCreateEvent) {
                    QGChannelCreateEventImpl(raw, event.data, bot, channel)
                }

            }

            is ChannelUpdate -> {
                if (event.data.type.isCategory) {
                    bot.logger.warn(
                        "Received category update event [raw={}]. report this log to issues https://github.com/simple-robot/simbot-component-qq-guild/issues/new/choose",
                        raw
                    )
                }
                val channel = channel0(event.data)
                pushEvent(QGChannelUpdateEvent) {
                    QGChannelUpdateEventImpl(raw, event.data, bot, channel)
                }
            }

            is ChannelDelete -> {
                if (event.data.type.isCategory) {
                    bot.logger.warn(
                        "Received category delete event [raw={}]. report this log to issues https://github.com/simple-robot/simbot-component-qq-guild/issues/new/choose",
                        raw
                    )
                }

                val channel = channel0(event.data)
                pushEvent(QGChannelDeleteEvent) {
                    QGChannelDeleteEventImpl(raw, event.data, bot, channel)
                }
            }
            //endregion

            //region Member相关
            is GuildMemberAdd -> {
                val member = member0(event.data)
                pushEvent(QGMemberAddEvent) {
                    QGMemberAddEventImpl(bot, raw, event.data, member)
                }
            }

            is GuildMemberUpdate -> {
                val member = member0(event.data)
                pushEvent(QGMemberUpdateEvent) {
                    QGMemberUpdateEventImpl(bot, raw, event.data, member)
                }
            }

            is GuildMemberRemove -> {
                val member = member0(event.data)
                pushEvent(QGMemberRemoveEvent) {
                    QGMemberRemoveEventImpl(bot, raw, event.data, member)
                }
            }
            //endregion


            is AtMessageCreate -> {

            }


            // unsupported
            else -> {
                pushEvent(QGUnsupportedEvent) { QGUnsupportedEventImpl(bot, event, raw) }
            }
        }
    }


}


private suspend inline fun QGBotImpl.pushEvent(eventKey: Event.Key<*>, crossinline block: () -> Event) {
    if (eventProcessor.isProcessable(eventKey)) {
        eventProcessor.push(block())
//        launch {
//            eventProcessor.push(block())
//        }
    }
}
