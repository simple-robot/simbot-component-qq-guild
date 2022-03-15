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

package love.forte.simbot.component.tencentguild

import kotlinx.coroutines.*
import love.forte.simbot.*
import love.forte.simbot.definition.*
import love.forte.simbot.message.*
import love.forte.simbot.tencentguild.*
import love.forte.simbot.utils.*
import java.util.concurrent.*
import java.util.stream.*
import kotlin.time.*

/**
 * 子频道。来自于一个 [TencentGuild]。
 *
 * @see TencentGuild
 * @author ForteScarlet
 */
public interface TencentChannel : Channel, TencentChannelInfo {
    @JvmSynthetic
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
    override val channelTypeValue: Int
    override val channelSubTypeValue: Int
    override val position: Int
    override val parentId: String

    @Api4J
    override val previous: TencentGuild

    @JvmSynthetic
    override suspend fun guild(): TencentGuild

    @JvmSynthetic
    override suspend fun owner(): TencentMember


    @Api4J
    override fun getRoles(groupingId: ID?, limiter: Limiter): Stream<out TencentRole>

    @JvmSynthetic
    override suspend fun previous(): TencentGuild

    @JvmSynthetic
    override suspend fun roles(groupingId: ID?, limiter: Limiter): kotlinx.coroutines.flow.Flow<TencentRole>


    //// Impl

    @Api4J
    override val guild: TencentGuild
        get() = runBlocking { guild() }

    @Api4J
    override val owner: TencentMember
        get() = runBlocking { owner() }


    /**
     * 子频道目前无法直接获取成员列表。将会直接返回 [previous] 的成员列表。
     */
    @JvmSynthetic
    override suspend fun members(groupingId: ID?, limiter: Limiter): kotlinx.coroutines.flow.Flow<TencentMember> {
        return previous().members(groupingId, limiter)
    }

    @JvmSynthetic
    override suspend fun member(id: ID): TencentMember? {
        return previous().member(id)
    }


    @Deprecated("子频道不支持禁言", ReplaceWith("false"))
    @JvmSynthetic
    override suspend fun mute(duration: Duration): Boolean = false

    @OptIn(Api4J::class)
    @Deprecated("子频道不支持禁言", ReplaceWith("false"))
    override fun muteBlocking(time: Long, unit: TimeUnit): Boolean = false

    @Deprecated("子频道不支持禁言", ReplaceWith("false"))
    @JvmSynthetic
    override suspend fun unmute(): Boolean = false

    @OptIn(Api4J::class)
    @Deprecated("子频道不支持禁言", ReplaceWith("false"))
    override fun unmuteBlocking(): Boolean = false

    /**
     * 子频道目前无法直接获取成员列表。将会直接返回 [previous] 的成员列表。
     */
    @Api4J
    override fun getMembers(groupingId: ID?, limiter: Limiter): Stream<out TencentMember> =
        previous.getMembers(groupingId, limiter)

    /**
     * 子频道目前无法直接获取成员列表。将会直接返回 [previous] 的成员列表。
     */
    @Api4J
    override fun getMembers(): Stream<out TencentMember> = previous.getMembers()

    /**
     * 子频道目前无法直接获取成员列表。将会直接返回 [previous] 的成员列表。
     */
    @Api4J
    override fun getMembers(groupingId: ID?): Stream<out TencentMember> = previous.getMembers(groupingId)

    /**
     * 子频道目前无法直接获取成员列表。将会直接返回 [previous] 的成员列表。
     */
    @Api4J
    override fun getMembers(limiter: Limiter): Stream<out TencentMember> = previous.getMembers(limiter)


    override fun getMember(id: ID): TencentMember? = runInBlocking { member(id) }
}