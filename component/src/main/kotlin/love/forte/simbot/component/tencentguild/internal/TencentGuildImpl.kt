package love.forte.simbot.component.tencentguild.internal

import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import love.forte.simbot.Api4J
import love.forte.simbot.ID
import love.forte.simbot.Limiter
import love.forte.simbot.action.NotSupportActionException
import love.forte.simbot.component.tencentguild.TencentGuild
import love.forte.simbot.component.tencentguild.TencentMember
import love.forte.simbot.component.tencentguild.TencentRole
import love.forte.simbot.definition.Organization
import love.forte.simbot.tencentguild.TencentChannelInfo
import love.forte.simbot.tencentguild.TencentGuildInfo
import love.forte.simbot.tencentguild.TencentRoleInfo
import love.forte.simbot.tencentguild.api.channel.GetGuildChannelListApi
import love.forte.simbot.tencentguild.api.member.GetMemberApi
import love.forte.simbot.tencentguild.api.role.GetGuildRoleListApi
import love.forte.simbot.tencentguild.request
import love.forte.simbot.withLimiter
import java.util.stream.Stream
import kotlin.streams.asStream
import kotlin.time.Duration

/**
 *
 * @author ForteScarlet
 */
internal class TencentGuildImpl(
    override val bot: TencentGuildBotImpl,
    private val guildInfo: TencentGuildInfo
) : TencentGuild, TencentGuildInfo by guildInfo {

    @Api4J
    override fun getRoles(groupingId: ID?, limiter: Limiter): Stream<out TencentRole> {
        @Suppress("UNCHECKED_CAST")
        return getRoleSequence(guildInfo.id).map { info ->
            TencentRoleImpl(bot, info)
        }.asStream().withLimiter(limiter)
    }


    override suspend fun roles(groupingId: ID?, limiter: Limiter): Flow<TencentRoleImpl> {
        return getRoleFlow(guildInfo.id).map { info ->
            TencentRoleImpl(bot, info)
        }.withLimiter(limiter)
    }

    private suspend fun getRoleFlow(guildId: ID): Flow<TencentRoleInfo> = flow {
        GetGuildRoleListApi(guildId).request(bot).roles.forEach {
            emit(it)
        }
    }

    private fun getRoleSequence(guildId: ID): Sequence<TencentRoleInfo> = sequence {
        val roles = runBlocking {
            GetGuildRoleListApi(guildId).request(bot).roles
        }
        yieldAll(roles)
    }


    override suspend fun children(groupingId: ID?, limiter: Limiter): Flow<TencentChannelImpl> {
        return getChildrenFlow(guildInfo.id).map { info ->
            TencentChannelImpl(bot, info, this)
        }.withLimiter(limiter)
    }


    @Api4J
    override fun getChildren(groupingId: ID?, limiter: Limiter): Stream<TencentChannelImpl> {
        return getChildrenSequence(guildInfo.id).map { info ->
            TencentChannelImpl(bot, info, this)
        }.asStream().withLimiter(limiter)
    }

    override suspend fun mute(duration: Duration): Boolean =
        throw NotSupportActionException("mute not support") // false // not support

    override suspend fun unmute(): Boolean = throw NotSupportActionException("unmute not support")
    override suspend fun previous(): Organization? = null

    @Api4J
    override fun getPrevious(): Organization? = null

    private lateinit var _owner: TencentMemberImpl

    override suspend fun owner(): TencentMemberImpl {
        // 暂时不管线程安全问题
        if (::_owner.isInitialized) return _owner
        val member = GetMemberApi(guildInfo.id, guildInfo.ownerId).request(bot)
        return TencentMemberImpl(bot, member, this).also {
            if (!::_owner.isInitialized) {
                _owner = it
            }
        }
    }

    @Api4J
    override val owner: TencentMemberImpl
        get() = runBlocking { owner() }

    private suspend fun getChildrenFlow(guildId: ID): Flow<TencentChannelInfo> =
        GetGuildChannelListApi(guildId = guildId).request(bot).asFlow()


    private fun getChildrenSequence(guildId: ID): Sequence<TencentChannelInfo> =
        runBlocking { GetGuildChannelListApi(guildId = guildId).request(bot).asSequence() }


    @Api4J
    override fun getMembers(groupingId: ID?, limiter: Limiter): Stream<out TencentMember> {
        return Stream.empty()
    }

    override suspend fun members(groupingId: ID?, limiter: Limiter): Flow<TencentMember> {
        return emptyFlow()
    }


}