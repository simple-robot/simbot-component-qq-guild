package love.forte.simbot.component.tencentguild.internal

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.emptyFlow
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
        return getRoleSequence(guildInfo.id).map { info ->
            TencentRoleImpl(bot, info)
        }.asStream()
    }


    override suspend fun roles(groupingId: ID?, limiter: Limiter): Flow<TencentRole> {
        return getRoleFlow(guildInfo.id).map { info ->
            TencentRoleImpl(bot, info)
        }
    }

    private suspend fun getRoleFlow(guildId: ID): Flow<TencentRoleInfo> =
        GetGuildRoleListApi(guildId).request(bot).roles.asFlow()

    private fun getRoleSequence(guildId: ID): Sequence<TencentRoleInfo> = runBlocking {
        GetGuildRoleListApi(guildId).request(bot).roles.asSequence()
    }


    override suspend fun children(groupingId: ID?, limiter: Limiter): Flow<TencentChannel> {
        return getChildrenFlow(guildInfo.id).map { info ->
            TencentChannelImpl(bot, info, this)
        }
    }


    @Api4J
    override fun getChildren(groupingId: ID?, limiter: Limiter): Stream<TencentChannel> {
        return getChildrenSequence(guildInfo.id).map { info ->
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

    private suspend fun getChildrenFlow(guildId: ID): Flow<TencentChannelInfo> =
        GetGuildChannelListApi(guildId = guildId).request(bot).asFlow()


    private fun getChildrenSequence(guildId: ID): Sequence<TencentChannelInfo> =
        runBlocking { GetGuildChannelListApi(guildId = guildId).request(bot).asSequence() }


    @Api4J
    override fun getMembers(groupingId: ID?, limiter: Limiter): Stream<out Member> {
        return Stream.empty()
    }

    override suspend fun members(groupingId: ID?, limiter: Limiter): Flow<Member> {
        return emptyFlow()
    }


}