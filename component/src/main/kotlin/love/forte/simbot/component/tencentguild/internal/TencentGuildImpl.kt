/*
 *  Copyright (c) 2022-2022 ForteScarlet <ForteScarlet@163.com>
 *
 *  根据 GNU LESSER GENERAL PUBLIC LICENSE 3 获得许可；
 *  除非遵守许可，否则您不得使用此文件。
 *  您可以在以下网址获取许可证副本：
 *
 *       https://www.gnu.org/licenses/lgpl-3.0-standalone.html
 *
 *   有关许可证下的权限和限制的具体语言，请参见许可证。
 */

package love.forte.simbot.component.tencentguild.internal

import kotlinx.coroutines.async
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.future.asCompletableFuture
import kotlinx.coroutines.runBlocking
import love.forte.simbot.Api4J
import love.forte.simbot.ID
import love.forte.simbot.Limiter
import love.forte.simbot.action.NotSupportActionException
import love.forte.simbot.component.tencentguild.TencentGuild
import love.forte.simbot.component.tencentguild.TencentRole
import love.forte.simbot.component.tencentguild.util.request
import love.forte.simbot.tencentguild.TencentChannelInfo
import love.forte.simbot.tencentguild.TencentGuildInfo
import love.forte.simbot.tencentguild.TencentRoleInfo
import love.forte.simbot.tencentguild.api.channel.GetGuildChannelListApi
import love.forte.simbot.tencentguild.api.member.GetMemberApi
import love.forte.simbot.tencentguild.api.role.GetGuildRoleListApi
import love.forte.simbot.utils.LazyValue
import love.forte.simbot.utils.lazyValue
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

    override val currentChannel: Int by lazy {
        bot.async { getChildrenFlow(guildInfo.id).count() }
            .asCompletableFuture().join()
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
        }.withLimiter(limiter).asStream()
    }

    override suspend fun mute(duration: Duration): Boolean =
        throw NotSupportActionException("mute not support") // false // not support


    private var _owner: LazyValue<TencentMemberImpl> = lazyValue {
        val member = GetMemberApi(guildInfo.id, guildInfo.ownerId).request(bot)
        TencentMemberImpl(bot, member, this)
    }

    override suspend fun owner(): TencentMemberImpl = _owner()

    @Api4J
    override val owner: TencentMemberImpl
        get() = runBlocking { owner() }

    private suspend fun getChildrenFlow(guildId: ID): Flow<TencentChannelInfo> =
        GetGuildChannelListApi(guildId = guildId).request(bot).asFlow()


    private fun getChildrenSequence(guildId: ID): Sequence<TencentChannelInfo> =
        runBlocking { GetGuildChannelListApi(guildId = guildId).request(bot).asSequence() }

}