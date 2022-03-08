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

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import love.forte.simbot.*
import love.forte.simbot.component.tencentguild.*
import love.forte.simbot.component.tencentguild.event.*
import love.forte.simbot.component.tencentguild.util.*
import love.forte.simbot.event.*
import love.forte.simbot.message.*
import love.forte.simbot.tencentguild.*
import love.forte.simbot.tencentguild.api.message.*
import java.util.stream.*

/**
 *
 * @author ForteScarlet
 */
internal class TencentChannelImpl internal constructor(
    override val bot: TencentGuildBotImpl,
    private val info: TencentChannelInfo,
    private val from: suspend () -> TencentGuildImpl
) : TencentChannel, TencentChannelInfo by info {

    internal constructor(
        bot: TencentGuildBotImpl, info: TencentChannelInfo, from: TencentGuildImpl
    ) : this(bot, info, { from })

    override suspend fun send(message: Message): MessageReceipt {
        val currentEvent =
            currentCoroutineContext()[EventProcessingContext]?.event?.takeIf { it is TcgChannelAtMessageEvent } as? TcgChannelAtMessageEvent

        val msgId = currentEvent?.sourceEventEntity?.id

        val messageForSend = MessageParsers.parse(message) {
            this.msgId = msgId
        }
        return MessageSendApi(info.id, messageForSend).requestBy(bot).asReceipt()
    }


    override suspend fun guild(): TencentGuildImpl = from()
    override suspend fun owner(): TencentMemberImpl = guild().owner()

    @Api4J
    override fun getRoles(groupingId: ID?, limiter: Limiter): Stream<out TencentRole> =
        guild.getRoles(groupingId, limiter)

    override suspend fun previous(): TencentGuildImpl = guild()

    @Api4J
    override val previous: TencentGuild
        get() = guild

    override suspend fun roles(groupingId: ID?, limiter: Limiter): Flow<TencentRole> =
        guild().roles(groupingId, limiter)

}