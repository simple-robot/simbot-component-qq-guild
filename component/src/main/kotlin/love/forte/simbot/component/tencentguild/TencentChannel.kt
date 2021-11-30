package love.forte.simbot.component.tencentguild

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import love.forte.simbot.*
import love.forte.simbot.definition.Channel
import love.forte.simbot.definition.Guild
import love.forte.simbot.definition.Member
import love.forte.simbot.message.Message
import love.forte.simbot.message.MessageReceipt
import love.forte.simbot.tencentguild.TencentChannelInfo
import java.util.stream.Stream

/**
 *
 * @author ForteScarlet
 */
public interface TencentChannel : Channel, TencentChannelInfo {
    override suspend fun send(message: Message): MessageReceipt

    override val bot: Bot
    override val createTime: Timestamp
    override val currentMember: Int
    override val description: String
    override val guildId: ID
    override val icon: String
    override val id: ID
    override val maximumMember: Int
    override val name: String
    override val ownerId: ID

    override suspend fun guild(): Guild

    override suspend fun owner(): Member

    @Api4J
    override fun getMembers(groupingId: ID?, limiter: Limiter): Stream<out Member> {
        return Stream.empty()
    }

    override suspend fun members(groupingId: ID?, limiter: Limiter): Flow<Member> {
        return emptyFlow()
    }

    @Api4J
    override fun getRoles(groupingId: ID?, limiter: Limiter): Stream<TencentRole>

    override suspend fun mute(): Boolean

    override suspend fun previous(): TencentGuild?

    override suspend fun roles(groupingId: ID?, limiter: Limiter): Flow<TencentRole>

    override val channelTypeValue: Int
    override val channelSubTypeValue: Int
    override val position: Int
    override val parentId: String
}