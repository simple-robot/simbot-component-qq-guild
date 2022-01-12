package love.forte.simbot.component.tencentguild.internal

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.runBlocking
import love.forte.simbot.Api4J
import love.forte.simbot.ID
import love.forte.simbot.Limiter
import love.forte.simbot.component.tencentguild.TencentChannel
import love.forte.simbot.component.tencentguild.TencentGuild
import love.forte.simbot.component.tencentguild.TencentRole
import love.forte.simbot.definition.Organization
import love.forte.simbot.message.Message
import love.forte.simbot.message.MessageReceipt
import love.forte.simbot.tencentguild.TencentChannelInfo
import love.forte.simbot.tencentguild.api.message.MessageSendApi
import love.forte.simbot.tencentguild.request
import java.util.stream.Stream

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
        bot: TencentGuildBotImpl,
        info: TencentChannelInfo,
        from: TencentGuildImpl
    ) : this(bot, info, { from })

    override suspend fun send(message: Message): MessageReceipt {
        val messageForSend = MessageParsers.parse(message)
        return MessageSendApi(info.id, messageForSend).request(bot).asReceipt()
    }


    override suspend fun guild(): TencentGuildImpl = from()

    @Api4J
    override val guild: TencentGuildImpl
        get() = runBlocking { from() }

    override suspend fun owner(): TencentMemberImpl = guild().owner()

    @Api4J
    override val owner: TencentMemberImpl
        get() = guild.owner

    @Api4J
    override fun getRoles(groupingId: ID?, limiter: Limiter): Stream<out TencentRole> = guild.getRoles(groupingId, limiter)

    override suspend fun previous(): TencentGuild = guild()

    @Api4J
    override fun getPrevious(): Organization = guild

    override suspend fun roles(groupingId: ID?, limiter: Limiter): Flow<TencentRole> =
        guild().roles(groupingId, limiter)

}