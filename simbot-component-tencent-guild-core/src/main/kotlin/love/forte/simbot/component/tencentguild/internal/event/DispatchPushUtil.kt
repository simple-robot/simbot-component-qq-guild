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

import kotlinx.serialization.json.Json
import love.forte.simbot.ID
import love.forte.simbot.Simbot
import love.forte.simbot.component.tencentguild.internal.TencentChannelCategoryImpl
import love.forte.simbot.component.tencentguild.internal.TencentChannelImpl
import love.forte.simbot.component.tencentguild.internal.TencentGuildComponentBotImpl
import love.forte.simbot.component.tencentguild.internal.TencentGuildImpl
import love.forte.simbot.component.tencentguild.internal.TencentGuildImpl.Companion.tencentGuildImpl
import love.forte.simbot.event.Event
import love.forte.simbot.literal
import love.forte.simbot.qguild.api.channel.GetChannelApi
import love.forte.simbot.qguild.api.guild.GetGuildApi
import love.forte.simbot.qguild.event.EventSignals
import love.forte.simbot.qguild.event.Signal
import love.forte.simbot.qguild.requestBy


internal interface SignalToEvent {
    val key: Event.Key<*>
    suspend operator fun invoke(
        bot: TencentGuildComponentBotImpl,
        decoder: Json,
        dispatch: Signal.Dispatch, decoded: () -> Any,
    ): Event
}

internal abstract class BaseSignalToEvent<S : Any> : SignalToEvent {
    abstract val type: EventSignals<S>
    override suspend fun invoke(
        bot: TencentGuildComponentBotImpl, decoder: Json,
        dispatch: Signal.Dispatch, decoded: () -> Any,
    ): Event {
        Simbot.check(dispatch.type == type.type) {
            "Event type does not match: ${dispatch.type} != ${type.type}"
        }
        
        @Suppress("UNCHECKED_CAST")
        return doParser(decoded() as S, bot)
    }
    
    protected abstract suspend fun doParser(data: S, bot: TencentGuildComponentBotImpl): Event
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


internal suspend fun TencentGuildComponentBotImpl.createGuildImpl(
    guildId: ID,
): TencentGuildImpl {
    val guild = GetGuildApi.create(guildId).requestBy(source)
    return tencentGuildImpl(this, guild)
}

internal suspend fun TencentGuildComponentBotImpl.createChannelImpl(
    guild: TencentGuildImpl, channelId: ID,
): TencentChannelImpl {
    val channel = GetChannelApi.create(channelId).requestBy(source)
    val category = resolveCategory(guild, channel.parentId.ID)
    return TencentChannelImpl(this, channel, guild, category)
}

internal suspend fun TencentGuildComponentBotImpl.resolveCategory(
    guild: TencentGuildImpl,
    id: ID,
): TencentChannelCategoryImpl {
    val idValue = id.literal
    return guild.internalChannelCategories[idValue] ?: run {
        val categoryInfo = GetChannelApi.create(id).requestBy(source)
        guild.internalChannelCategories.compute(id.literal) { _, current ->
            current?.also {
                it.channel = categoryInfo
            } ?: TencentChannelCategoryImpl(this, guild, categoryInfo)
        }!!
    }
}

internal suspend inline fun TencentGuildComponentBotImpl.findOrCreateGuildImpl(
    guildId: ID,
    onCreate: (created: TencentGuildImpl) -> Unit = {},
): TencentGuildImpl {
    return getInternalGuild(guildId) ?: createGuildImpl(guildId).also(onCreate)
    
}

internal suspend inline fun TencentGuildComponentBotImpl.findOrCreateChannelImpl(
    guildId: ID,
    channelId: ID,
    onCreateGuild: (created: TencentGuildImpl) -> Unit = {},
    onCreateChannel: (created: TencentChannelImpl) -> Unit = {},
): TencentChannelImpl {
    val guild = findOrCreateGuildImpl(guildId, onCreateGuild)
    
    return guild.getInternalChannel(channelId) ?: run {
        createChannelImpl(guild, channelId).also(onCreateChannel)
    }
}

