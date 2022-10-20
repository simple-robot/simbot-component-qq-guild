/*
 *  Copyright (c) 2022 ForteScarlet <ForteScarlet@163.com>
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

import love.forte.plugin.suspendtrans.annotation.JvmAsync
import love.forte.plugin.suspendtrans.annotation.JvmBlocking
import love.forte.simbot.ID
import love.forte.simbot.component.tencentguild.*
import love.forte.simbot.event.EventProcessor
import love.forte.simbot.message.Image
import love.forte.simbot.tencentguild.TencentGuildBot
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
    override val source: TencentGuildBot get() = bot.source
    
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