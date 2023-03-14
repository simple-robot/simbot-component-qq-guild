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

package love.forte.simbot.component.qguild.internal

import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import love.forte.simbot.component.qguild.event.*
import love.forte.simbot.component.qguild.internal.event.*
import love.forte.simbot.component.qguild.util.requestBy
import love.forte.simbot.event.Event
import love.forte.simbot.event.EventProcessor
import love.forte.simbot.qguild.api.guild.GetGuildApi
import love.forte.simbot.qguild.event.*
import love.forte.simbot.qguild.model.isCategory

/**
 * simbot针对QQ频道注册的时候只注册一个 **预处理** 函数。
 * 其中对于内建缓存的操作会在事件前后顺序执行，
 * 而对于向 [EventProcessor] 中的事件推送则是异步的。
 */
internal fun QGBotImpl.registerEventProcessor() {
    suspend fun getOrComputeGuild(id: String): QGGuildImpl {
        return getInternalGuild(id) ?: kotlin.run {
            val guildInfo = GetGuildApi.create(id).requestBy(bot)
            updateGuild(guildInfo)
        }
    }

    val bot = this
    source.registerPreProcessor { raw ->
        when (val event = this) {
            //region Guild相关
            is GuildCreate -> {
                val guild = emitGuildCreate(event)
                pushEvent(QGGuildCreateEvent) {
                    QGGuildCreateEventImpl(raw, event.data, bot, guild)
                }
            }

            is GuildUpdate -> {
                val guild = emitGuildUpdate(event)
                pushEvent(QGGuildUpdateEvent) {
                    QGGuildUpdateEventImpl(raw, event.data, bot, guild)
                }
            }

            is GuildDelete -> {
                val guild = emitGuildDelete(event).also { it?.cancel() }
                pushEvent(QGGuildDeleteEvent) {
                    QGGuildDeleteEventImpl(raw, event.data, bot, guild)
                }
            }
            //endregion

            //region Channel相关
            is ChannelCreate -> {
                if (event.data.type.isCategory) {
                    bot.logger.warn(
                        "Received category create event [raw={}]. report this log to issues https://github.com/simple-robot/simbot-component-tencent-guild/issues/new/choose",
                        raw
                    )
                    return@registerPreProcessor
                }

                val guild: QGGuildImpl = getOrComputeGuild(event.data.guildId)
                val channel = guild.emitChannelCreate(event)
                pushEvent(QGChannelCreateEvent) {
                    QGChannelCreateEventImpl(raw, event.data, bot, channel)
                }

            }

            is ChannelUpdate -> {
                if (event.data.type.isCategory) {
                    bot.logger.warn(
                        "Received category update event [raw={}]. report this log to issues https://github.com/simple-robot/simbot-component-tencent-guild/issues/new/choose",
                        raw
                    )
                    return@registerPreProcessor
                }
                val guild: QGGuildImpl = getOrComputeGuild(event.data.guildId)
                val channel = guild.emitChannelUpdate(event)
                pushEvent(QGChannelUpdateEvent) {
                    QGChannelUpdateEventImpl(raw, event.data, bot, channel)
                }
            }

            is ChannelDelete -> {
                if (event.data.type.isCategory) {
                    bot.logger.warn(
                        "Received category delete event [raw={}]. report this log to issues https://github.com/simple-robot/simbot-component-tencent-guild/issues/new/choose",
                        raw
                    )
                }

                val guild: QGGuildImpl = getOrComputeGuild(event.data.guildId)
                guild.emitChannelDelete(event)

                pushEvent(QGChannelDeleteEvent) {
                    QGChannelDeleteEventImpl(raw, event.data, bot, guild)
                }
            }
            //endregion

            //region Member相关
            is GuildMemberAdd -> {
                val guild: QGGuildImpl = getOrComputeGuild(event.data.guildId)
                val member = guild.emitMemberAdd(event)
                pushEvent(QGMemberAddEvent) {
                    QGMemberAddEventImpl(bot, raw, event.data, member)
                }
            }

            is GuildMemberUpdate -> {
                val guild: QGGuildImpl = getOrComputeGuild(event.data.guildId)
                val member = guild.emitMemberUpdate(event)
                pushEvent(QGMemberUpdateEvent) {
                    QGMemberUpdateEventImpl(bot, raw, event.data, member)
                }
            }

            is GuildMemberRemove -> {
                val guild: QGGuildImpl = getOrComputeGuild(event.data.guildId)
                val member = guild.emitMemberRemove(event)
                    ?: QGMemberImpl(event.data, guild)

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
        launch {
            eventProcessor.push(block())
        }
    }
}
