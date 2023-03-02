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

package love.forte.simbot.component.tencentguild

import love.forte.plugin.suspendtrans.annotation.JvmAsync
import love.forte.plugin.suspendtrans.annotation.JvmBlocking
import love.forte.simbot.ID
import love.forte.simbot.bot.Bot
import love.forte.simbot.component.tencentguild.internal.TencentMessageReceipt
import love.forte.simbot.definition.GuildMember
import love.forte.simbot.definition.MemberInfo
import love.forte.simbot.message.Message
import love.forte.simbot.message.MessageContent
import love.forte.simbot.qguild.TencentMemberInfo
import love.forte.simbot.utils.item.Items
import kotlin.time.Duration

/**
 *
 * @author ForteScarlet
 */
public interface TencentMember : GuildMember, MemberInfo, TencentGuildObjectiveContainer<TencentMemberInfo> {
    override val avatar: String
    override val bot: Bot
    override val id: ID
    
    override val username: String
    
    override val roles: Items<TencentRole>
    
    @JvmBlocking
    @JvmAsync
    override suspend fun send(message: Message): TencentMessageReceipt
    
    @JvmBlocking
    @JvmAsync
    override suspend fun send(text: String): TencentMessageReceipt
    
    @JvmBlocking
    @JvmAsync
    override suspend fun send(message: MessageContent): TencentMessageReceipt
    
    @JvmBlocking(asProperty = true, suffix = "")
    @JvmAsync(asProperty = true)
    override suspend fun organization(): TencentGuild = guild()
    
    @JvmBlocking(asProperty = true, suffix = "")
    @JvmAsync(asProperty = true)
    override suspend fun guild(): TencentGuild
    
    
    @Deprecated("子频道不支持禁言", ReplaceWith("false"))
    @JvmSynthetic
    override suspend fun mute(duration: Duration): Boolean = false
    
    @Deprecated("子频道不支持禁言", ReplaceWith("false"))
    @JvmSynthetic
    override suspend fun unmute(): Boolean = false
}
