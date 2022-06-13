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

package love.forte.simbot.component.tencentguild.internal

import love.forte.simbot.component.tencentguild.internal.TencentGuildImpl.Companion.tencentGuildImpl
import love.forte.simbot.component.tencentguild.internal.event.eventSignalParsers
import love.forte.simbot.component.tencentguild.internal.event.findOrCreateGuildImpl
import love.forte.simbot.event.pushIfProcessable
import love.forte.simbot.literal
import love.forte.simbot.tencentguild.EventSignals
import love.forte.simbot.tencentguild.TencentChannelInfo
import love.forte.simbot.tencentguild.TencentGuildInfo


internal fun TencentGuildComponentBotImpl.registerEventProcessor() {
    registerEventPreProcessor()
    registerNormalEventProcessor()
}

/**
 * 注册预处理事件，用于监听各类'变化'事件并同步数据。
 */
private fun TencentGuildComponentBotImpl.registerEventPreProcessor() {
    sourceBot.preProcessor { _, decoded ->
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
            guild.guildInfo = eventData
        }
    }
}

private fun TencentGuildComponentBotImpl.onGuildDelete(decoded: () -> Any) {
    val eventData = decoded()
    if (eventData is TencentGuildInfo) {
        internalGuilds.remove(eventData.id.literal)
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
        
        guild.internalChannels.compute(eventData.id.literal) { _, old ->
            if (old != null) {
                old.channel = eventData
                old
            } else {
                TencentChannelImpl(this, eventData, guild)
            }
        }
    }
}

private fun TencentGuildComponentBotImpl.onChannelUpdate(decoded: () -> Any) {
    val eventData = decoded()
    if (eventData is TencentChannelInfo) {
        getInternalGuild(eventData.guildId)?.getInternalChannel(eventData.id)?.also { channel ->
            channel.channel = eventData
        }
    }
}

private fun TencentGuildComponentBotImpl.onChannelDelete(decoded: () -> Any) {
    val eventData = decoded()
    if (eventData is TencentChannelInfo) {
        getInternalGuild(eventData.guildId)?.internalChannels?.remove(eventData.id.literal)
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
    sourceBot.processor { json, decoded ->
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
