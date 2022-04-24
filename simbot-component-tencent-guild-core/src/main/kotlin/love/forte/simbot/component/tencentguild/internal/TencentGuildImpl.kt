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

import kotlinx.coroutines.async
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.future.asCompletableFuture
import kotlinx.coroutines.runBlocking
import love.forte.simbot.Api4J
import love.forte.simbot.ID
import love.forte.simbot.Limiter
import love.forte.simbot.component.tencentguild.TencentGuild
import love.forte.simbot.component.tencentguild.TencentRole
import love.forte.simbot.component.tencentguild.util.requestBy
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
    override val bot: TencentGuildComponentBotImpl,
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
        GetGuildRoleListApi(guildId).requestBy(bot).roles.forEach {
            emit(it)
        }
    }

    private fun getRoleSequence(guildId: ID): Sequence<TencentRoleInfo> = sequence {
        val roles = runBlocking {
            GetGuildRoleListApi(guildId).requestBy(bot).roles
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

    override suspend fun mute(duration: Duration): Boolean = false
        // throw NotSupportActionException("mute not support") // false // not support


    private var _owner: LazyValue<TencentMemberImpl> = lazyValue {
        val member = GetMemberApi(guildInfo.id, guildInfo.ownerId).requestBy(bot)
        TencentMemberImpl(bot, member, this)
    }

    override suspend fun owner(): TencentMemberImpl = _owner()

    @Api4J
    override val owner: TencentMemberImpl
        get() = runBlocking { owner() }

    private suspend fun getChildrenFlow(guildId: ID): Flow<TencentChannelInfo> =
        GetGuildChannelListApi(guildId = guildId).requestBy(bot).asFlow()


    private fun getChildrenSequence(guildId: ID): Sequence<TencentChannelInfo> =
        runBlocking { GetGuildChannelListApi(guildId = guildId).requestBy(bot).asSequence() }

}