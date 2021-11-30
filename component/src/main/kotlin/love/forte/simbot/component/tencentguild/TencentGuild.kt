package love.forte.simbot.component.tencentguild

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.runBlocking
import love.forte.simbot.*
import love.forte.simbot.definition.Guild
import love.forte.simbot.definition.Member
import java.util.stream.Stream

/**
 *
 * @author ForteScarlet
 */
public interface TencentGuild : Guild {
    override val bot: Bot
    override val createTime: Timestamp
    override val currentMember: Int
    override val description: String
    override val icon: String
    override val id: ID
    override val maximumMember: Int
    override val name: String
    override val ownerId: ID
    override suspend fun owner(): TencentMember
    @Api4J
    override val owner: TencentMember get() = runBlocking { owner() }
    override suspend fun members(groupingId: ID?, limiter: Limiter): Flow<Member> = emptyFlow()
    @Api4J
    override fun getMembers(groupingId: ID?, limiter: Limiter): Stream<out Member> = Stream.empty()

    override suspend fun roles(groupingId: ID?, limiter: Limiter): Flow<TencentRole>
    @Api4J
    override fun getRoles(groupingId: ID?, limiter: Limiter): Stream<TencentRole>

}