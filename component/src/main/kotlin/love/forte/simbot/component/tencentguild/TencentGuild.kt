package love.forte.simbot.component.tencentguild

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.runBlocking
import love.forte.simbot.*
import love.forte.simbot.action.NotSupportActionException
import love.forte.simbot.definition.Guild
import love.forte.simbot.definition.GuildMember
import love.forte.simbot.definition.Organization
import love.forte.simbot.utils.runInBlocking
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

    override suspend fun children(groupingId: ID?, limiter: Limiter): Flow<TencentChannel>
    override suspend fun children(groupingId: ID?): Flow<TencentChannel> = children(groupingId, Limiter)

    @Api4J
    override fun getChildren(groupingId: ID?, limiter: Limiter): Stream<out TencentChannel>


    override suspend fun owner(): TencentMember

    @Api4J
    override val owner: TencentMember
        get() = runBlocking { owner() }


    /**
     * 频道无法获取成员列表。
     */
    override suspend fun members(groupingId: ID?, limiter: Limiter): Flow<TencentMember> = emptyFlow()

    /**
     * 频道无法获取成员列表
     */
    override suspend fun member(id: ID): TencentMember? = null

    /**
     * 频道无法获取成员列表。
     */
    @OptIn(Api4J::class)
    override fun getMembers(groupingId: ID?, limiter: Limiter): Stream<out TencentMember> = Stream.empty()

    @OptIn(Api4J::class)
    override fun getMembers(): Stream<out TencentMember> = Stream.empty()

    @OptIn(Api4J::class)
    override fun getMembers(groupingId: ID?): Stream<out TencentMember> = Stream.empty()

    @OptIn(Api4J::class)
    override fun getMembers(limiter: Limiter): Stream<out TencentMember> = Stream.empty()


    override suspend fun roles(groupingId: ID?, limiter: Limiter): Flow<TencentRole>

    @Api4J
    override fun getRoles(groupingId: ID?, limiter: Limiter): Stream<out TencentRole>

    //// Impls

    override suspend fun unmute(): Boolean = throw NotSupportActionException("unmute not support")
    override suspend fun previous(): Organization? = null
    override fun getMember(id: ID): GuildMember? = runInBlocking { member(id) }

    @OptIn(Api4J::class)
    override val previous: Organization?
        get() = null
}