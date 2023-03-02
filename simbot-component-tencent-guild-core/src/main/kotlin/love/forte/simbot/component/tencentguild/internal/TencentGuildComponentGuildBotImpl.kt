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

import love.forte.plugin.suspendtrans.annotation.JvmAsync
import love.forte.plugin.suspendtrans.annotation.JvmBlocking
import love.forte.simbot.ID
import love.forte.simbot.component.tencentguild.*
import love.forte.simbot.event.EventProcessor
import love.forte.simbot.message.Image
import love.forte.simbot.qguild.Bot
import love.forte.simbot.utils.item.Items
import org.slf4j.Logger
import kotlin.coroutines.CoroutineContext


/**
 *
 * @author ForteScarlet
 */
internal class TencentGuildComponentGuildBotImpl(
    override val bot: TencentGuildComponentBot,
    private val member: TencentMember,
) : TencentGuildComponentGuildBot, TencentGuildComponentBot {
    override suspend fun asMember(): TencentMember = member
    
    @JvmBlocking(baseName = "getGuild", suffix = "")
    @JvmAsync(baseName = "getGuild")
    override suspend fun guild(id: ID): TencentGuild? = bot.guild(id)
    
    @JvmBlocking
    @JvmAsync
    override suspend fun resolveImage(id: ID): Image<*> = bot.resolveImage(id)
    
    override val coroutineContext: CoroutineContext get() = bot.coroutineContext
    override val isCancelled: Boolean get() = bot.isCancelled
    override val isStarted: Boolean get() = bot.isStarted
    override val logger: Logger get() = bot.logger
    
    override suspend fun cancel(reason: Throwable?): Boolean = bot.cancel(reason)
    
    override suspend fun join() = bot.join()
    
    override val component: TencentGuildComponent get() = bot.component
    override val source: Bot get() = bot.source
    
    override fun isMe(id: ID): Boolean = bot.isMe(id)
    
    override suspend fun start(): Boolean = bot.start()
    
    override val manager: TencentGuildBotManager
        get() = bot.manager
    
    override val eventProcessor: EventProcessor
        get() = bot.eventProcessor
    override val guilds: Items<TencentGuild>
        get() = bot.guilds
}

internal fun TencentGuildComponentBot.asMember(member: TencentMember): TencentGuildComponentGuildBotImpl {
    return TencentGuildComponentGuildBotImpl(this, member)
}
