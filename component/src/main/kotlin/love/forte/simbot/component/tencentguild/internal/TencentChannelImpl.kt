package love.forte.simbot.component.tencentguild.internal

import kotlinx.coroutines.flow.Flow
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
internal class TencentChannelImpl(
    override val bot: TencentGuildBotImpl,
    private val info: TencentChannelInfo,
    private val from: TencentGuildImpl
) : TencentChannel, TencentChannelInfo by info {
    override suspend fun send(message: Message): MessageReceipt {
        val messageForSend = MessageParsers.parse(message)
        return MessageSendApi(info.id, messageForSend).request(bot).asReceipt()
    }


    override suspend fun guild(): TencentGuildImpl = from

    @Api4J
    override val guild: TencentGuildImpl
        get() = from

    override suspend fun owner(): TencentMemberImpl = from.owner()

    @Api4J
    override val owner: TencentMemberImpl
        get() = from.owner

    @Api4J
    override fun getRoles(groupingId: ID?, limiter: Limiter): Stream<TencentRole> = from.getRoles(groupingId, limiter)

    override suspend fun mute(): Boolean = false

    override suspend fun previous(): TencentGuild = from

    @Api4J
    override fun getPrevious(): Organization = from

    override suspend fun roles(groupingId: ID?, limiter: Limiter): Flow<TencentRole> = from.roles(groupingId, limiter)

}