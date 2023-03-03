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

package love.forte.simbot.component.tencentguild.internal

import love.forte.simbot.component.tencentguild.internal.TencentGuildImpl.Companion.tencentGuildImpl
import love.forte.simbot.component.tencentguild.internal.event.eventSignalParsers
import love.forte.simbot.component.tencentguild.internal.event.findOrCreateGuildImpl
import love.forte.simbot.event.pushIfProcessable
import love.forte.simbot.literal
import love.forte.simbot.qguild.event.EventSignals
import love.forte.simbot.qguild.isGrouping


internal fun TencentGuildComponentBotImpl.registerEventProcessor() {
    registerEventPreProcessor()
    registerNormalEventProcessor()
}

/**
 * 注册预处理事件，用于监听各类'变化'事件并同步数据。
 */
private fun TencentGuildComponentBotImpl.registerEventPreProcessor() {
    source.registerPreProcessor { _, decoded ->
        when (type) {
            EventSignals.Guilds.GuildCreate.type -> onGuildCreate(decoded)
            EventSignals.Guilds.GuildUpdate.type -> onGuildUpdate(decoded)
            EventSignals.Guilds.GuildDelete.type -> onGuildDelete(decoded)
            EventSignals.Guilds.ChannelCreate.type -> onChannelCreate(decoded)
            EventSignals.Guilds.ChannelUpdate.type -> onChannelUpdate(decoded)
            EventSignals.Guilds.ChannelDelete.type -> onChannelDelete(decoded)
            EventSignals.GuildMembers.GuildMemberAdd.type -> onMemberAdd(decoded)
            EventSignals.GuildMembers.GuildMemberUpdate.type -> onMemberUpdate(decoded)
            EventSignals.GuildMembers.GuildMemberRemove.type -> onMemberRemove(decoded)
        }
    }
    
}

// region guilds
private suspend fun TencentGuildComponentBotImpl.onGuildCreate(decoded: () -> Any) {
    val eventData = decoded()
    if (eventData is TencentGuildInfo) {
        internalGuilds[eventData.id.literal] = tencentGuildImpl(this, eventData).also {
            logger.debug("OnGuildCreate sync: {}", it)
        }
    }
}

private fun TencentGuildComponentBotImpl.onGuildUpdate(decoded: () -> Any) {
    val eventData = decoded()
    if (eventData is TencentGuildInfo) {
        getInternalGuild(eventData.id)?.also { guild ->
            guild.source = eventData
            logger.debug("OnGuildUpdate sync: {}", eventData)
        }
    }
}

private fun TencentGuildComponentBotImpl.onGuildDelete(decoded: () -> Any) {
    val eventData = decoded()
    if (eventData is TencentGuildInfo) {
        internalGuilds.remove(eventData.id.literal)
        logger.debug("OnGuildDelete sync: {}", eventData)
    }
}
// endregion

// region channels
@Suppress("RedundantSuspendModifier")
private suspend fun TencentGuildComponentBotImpl.onChannelCreate(decoded: () -> Any) {
    val eventData = decoded()
    if (eventData is TencentChannelInfo) {
        val guild = findOrCreateGuildImpl(eventData.guildId) {
            logger.debug(
                "No existing guild with id [{}] found in [onChannelCreate]. Build and save guild {}",
                it.id,
                it
            )
            internalGuilds[it.id.literal] = it
        }
        
        guild.internalChannels.compute(eventData.id.literal) { _, current ->
            current?.also {
                it.source = eventData
            } ?: run {
                // TODO warn or err log if not found?
                val category = guild.internalChannelCategories[eventData.parentId]!!
                TencentChannelImpl(this, eventData, guild, category)
            }
        }
    }
}

private fun TencentGuildComponentBotImpl.onChannelUpdate(decoded: () -> Any) {
    val eventData = decoded()
    if (eventData is TencentChannelInfo) {
        val guild = getInternalGuild(eventData.guildId) ?: return
        if (eventData.channelType.isGrouping) {
            guild.internalChannelCategories[eventData.id.literal]?.also { category ->
                category.channel = eventData
                logger.debug("OnChannelUpdate sync: {}", eventData)
            } ?: run {
                logger.debug(
                    "OnChannelUpdate, but not sync. guild or channel by event data: [{}] not found.",
                    eventData
                )
            }
        } else {
            // TODO update category?
            guild.getInternalChannel(eventData.id)?.also { channel ->
                channel.source = eventData
                logger.debug("OnChannelUpdate sync: {}", eventData)
            } ?: kotlin.run {
                logger.debug(
                    "OnChannelUpdate, but not sync. guild or channel by event data: [{}] not found.",
                    eventData
                )
            }
        }
    }
}

private fun TencentGuildComponentBotImpl.onChannelDelete(decoded: () -> Any) {
    val eventData = decoded()
    if (eventData is TencentChannelInfo) {
        val guild = getInternalGuild(eventData.guildId) ?: return
        if (eventData.channelType.isGrouping) {
            val categoryId = eventData.id.literal
            val removedCategory = guild.internalChannelCategories.remove(eventData.id.literal)
            logger.debug(
                "OnChannelDelete sync: removed channel category [{}] from event data {}",
                removedCategory,
                eventData
            )
            val values = guild.internalChannels.values.iterator()
            while (values.hasNext()) {
                val next = values.next()
                if (next.source.parentId == categoryId) {
                    values.remove()
                    logger.debug(
                        "OnChannelDelete sync: removed channel [{}] (by category(id={})) from event data {}",
                        removedCategory,
                        categoryId,
                        eventData
                    )
                }
            }
        } else {
            val removed = guild.internalChannels.remove(eventData.id.literal)
            logger.debug("OnChannelDelete sync: removed channel [{}] by event data {}", removed, eventData)
        }
    }
}
// endregion

// region members
@Suppress("unused", "UNUSED_PARAMETER")
private fun TencentGuildComponentBotImpl.onMemberAdd(decoded: () -> Any) {
    // nothing.
}

@Suppress("unused", "UNUSED_PARAMETER")
private fun TencentGuildComponentBotImpl.onMemberUpdate(decoded: () -> Any) {
    // nothing.
}

@Suppress("unused", "UNUSED_PARAMETER")
private fun TencentGuildComponentBotImpl.onMemberRemove(decoded: () -> Any) {
    // nothing.
}
// endregion


/**
 * 注册普通的事件处理器。
 */
private fun TencentGuildComponentBotImpl.registerNormalEventProcessor() {
    // process event.
    source.registerProcessor { json, decoded ->
        // event processor
        logger.trace("EventSignals.events[{}]: {}", type, EventSignals.events[type])
        EventSignals.events[this.type]?.let { signals ->
            logger.trace("eventSignalParsers[{}]: {}", signals, eventSignalParsers[signals])
            
            eventSignalParsers[signals]?.let { parser ->
                
                logger.trace(
                    "eventProcessor.isProcessable({}): {}", parser.key, eventProcessor.isProcessable(parser.key)
                )
                eventProcessor.pushIfProcessable(parser.key) {
                    parser(
                        bot = this@registerNormalEventProcessor, decoder = json, decoded = decoded, dispatch = this
                    )
                }
            }
        }
    }
}
