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

package love.forte.simbot.component.tencentguild

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.runBlocking
import love.forte.simbot.*
import love.forte.simbot.definition.Channel
import love.forte.simbot.message.Message
import love.forte.simbot.message.MessageReceipt
import love.forte.simbot.tencentguild.TencentChannelInfo
import love.forte.simbot.utils.runInBlocking
import java.util.concurrent.TimeUnit
import java.util.stream.Stream
import kotlin.time.Duration

/**
 * 子频道。来自于一个 [TencentGuild]。
 *
 * @see TencentGuild
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
    override val channelTypeValue: Int
    override val channelSubTypeValue: Int
    override val position: Int
    override val parentId: String

    @Api4J
    override val previous: TencentGuild

    override suspend fun guild(): TencentGuild

    override suspend fun owner(): TencentMember



    @Api4J
    override fun getRoles(groupingId: ID?, limiter: Limiter): Stream<out TencentRole>

    override suspend fun previous(): TencentGuild

    override suspend fun roles(groupingId: ID?, limiter: Limiter): Flow<TencentRole>


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
    override suspend fun members(groupingId: ID?, limiter: Limiter): Flow<TencentMember> {
        return previous().members(groupingId, limiter)
    }

    override suspend fun member(id: ID): TencentMember? {
        return previous().member(id)
    }



    @Deprecated("子频道不支持禁言", ReplaceWith("false"))
    override suspend fun mute(duration: Duration): Boolean = false

    @OptIn(Api4J::class)
    @Deprecated("子频道不支持禁言", ReplaceWith("false"))
    override fun muteBlocking(time: Long, unit: TimeUnit): Boolean = false

    @Deprecated("子频道不支持禁言", ReplaceWith("false"))
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