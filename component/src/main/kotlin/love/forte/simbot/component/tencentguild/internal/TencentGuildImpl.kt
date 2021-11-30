package love.forte.simbot.component.tencentguild.internal

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import love.forte.simbot.Api4J
import love.forte.simbot.ID
import love.forte.simbot.Limiter
import love.forte.simbot.component.tencentguild.TencentChannel
import love.forte.simbot.component.tencentguild.TencentGuild
import love.forte.simbot.component.tencentguild.TencentMember
import love.forte.simbot.component.tencentguild.TencentRole
import love.forte.simbot.definition.Member
import love.forte.simbot.definition.Organization
import love.forte.simbot.tencentguild.TencentChannelInfo
import love.forte.simbot.tencentguild.TencentGuildInfo
import love.forte.simbot.tencentguild.TencentRoleInfo
import love.forte.simbot.tencentguild.api.channel.GetGuildChannelListApi
import love.forte.simbot.tencentguild.api.member.GetMemberApi
import love.forte.simbot.tencentguild.api.role.GetGuildRoleListApi
import love.forte.simbot.tencentguild.request
import java.util.stream.Stream
import kotlin.streams.asStream

/**
 *
 * @author ForteScarlet
 */
internal class TencentGuildImpl(
    override val bot: TencentGuildBotImpl,
    private val guildInfo: TencentGuildInfo
) : TencentGuild, TencentGuildInfo by guildInfo {

    @Api4J
    override fun getRoles(groupingId: ID?, limiter: Limiter): Stream<TencentRole> {
        return getRoleSequence(limiter.offset, limiter.limit, guildInfo.id).map { info ->
            TencentRoleImpl(bot, info)
        }.asStream()
    }


    override suspend fun roles(groupingId: ID?, limiter: Limiter): Flow<TencentRole> {
        return getRoleFlow(limiter.offset, limiter.limit, guildInfo.id).map { info ->
            TencentRoleImpl(bot, info)
        }
    }

    private fun getRoleFlow(skip: Int, limit: Int, guildId: ID): Flow<TencentRoleInfo> = flow {
        flowForLimiter(skip, limit) {
            GetGuildRoleListApi(guildId).request(bot).roles
        }
    }

    private fun getRoleSequence(skip: Int, limit: Int, guildId: ID): Sequence<TencentRoleInfo> = sequence {
        sequenceForLimiter(skip, limit) {
            runBlocking { GetGuildRoleListApi(guildId).request(bot).roles }
        }
    }


    override suspend fun children(groupingId: ID?, limiter: Limiter): Flow<TencentChannel> {
        return getChildrenFlow(limiter.offset, limiter.limit, guildInfo.id).map { info ->
            TencentChannelImpl(bot, info, this)
        }
    }


    @Api4J
    override fun getChildren(groupingId: ID?, limiter: Limiter): Stream<TencentChannel> {
        return getChildrenSequence(limiter.offset, limiter.limit, guildInfo.id).map { info ->
            TencentChannelImpl(bot, info, this)
        }.asStream()
    }

    override suspend fun mute(): Boolean = false // not support
    override suspend fun previous(): Organization? = null
    @Api4J
    override fun getPrevious(): Organization? = null

    private lateinit var _owner: TencentMember

    override suspend fun owner(): TencentMember {
        // 暂时不管线程安全问题
        if (::_owner.isInitialized) return _owner
        val member = GetMemberApi(guildInfo.id, guildInfo.ownerId).request(bot)
        return TencentMemberImpl(bot, member, this).also {
            if (!::_owner.isInitialized) {
                _owner = it
            }
        }
    }

    private fun getChildrenFlow(skip: Int, limit: Int, guildId: ID): Flow<TencentChannelInfo> = flow {
        flowForLimiter(skip, limit) {
            GetGuildChannelListApi(guildId = guildId).request(bot)
        }
    }
    private fun getChildrenSequence(skip: Int, limit: Int, guildId: ID): Sequence<TencentChannelInfo> = sequence {
        sequenceForLimiter(skip, limit) {
            runBlocking { GetGuildChannelListApi(guildId = guildId).request(bot) }
        }
    }


    @Api4J
    override fun getMembers(groupingId: ID?, limiter: Limiter): Stream<out Member> {
        return Stream.empty()
    }
    override suspend fun members(groupingId: ID?, limiter: Limiter): Flow<Member> {
        return emptyFlow()
    }


}