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

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.runBlocking
import love.forte.simbot.Api4J
import love.forte.simbot.ID
import love.forte.simbot.Limiter
import love.forte.simbot.Timestamp
import love.forte.simbot.definition.Guild
import love.forte.simbot.definition.Organization
import love.forte.simbot.utils.runInBlocking
import java.util.stream.Stream

/**
 *
 * @author ForteScarlet
 */
public interface TencentGuild : Guild {
    override val bot: TencentGuildComponentGuildMemberBot
    override val createTime: Timestamp
    override val currentMember: Int
    override val description: String
    override val icon: String
    override val id: ID
    override val maximumMember: Int
    override val name: String
    override val ownerId: ID

    @JvmSynthetic
    override suspend fun children(groupingId: ID?, limiter: Limiter): Flow<TencentChannel>

    @JvmSynthetic
    override suspend fun children(groupingId: ID?): Flow<TencentChannel> = children(groupingId, Limiter)

    @Api4J
    override fun getChildren(groupingId: ID?, limiter: Limiter): Stream<out TencentChannel>


    @JvmSynthetic
    override suspend fun owner(): TencentMember

    @Api4J
    override val owner: TencentMember
        get() = runBlocking { owner() }
    
    /**
     * 获取指定成员信息。
     */
    @JvmSynthetic
    override suspend fun member(id: ID): TencentMember?
    
    /**
     * 获取指定成员信息。
     */
    override fun getMember(id: ID): TencentMember? = runInBlocking { member(id) }

    /**
     * 频道无法获取成员列表。
     */
    @JvmSynthetic
    @Deprecated("Get member list is not supported", ReplaceWith("emptyFlow()", "kotlinx.coroutines.flow.emptyFlow"))
    override suspend fun members(groupingId: ID?, limiter: Limiter): Flow<TencentMember> = emptyFlow()



    /**
     * 频道无法获取成员列表。
     */
    @OptIn(Api4J::class)
    @Deprecated("Get member list is not supported", ReplaceWith("Stream.empty()", "java.util.stream.Stream"))
    override fun getMembers(groupingId: ID?, limiter: Limiter): Stream<out TencentMember> = Stream.empty()

    @OptIn(Api4J::class)
    @Deprecated("Get member list is not supported", ReplaceWith("Stream.empty()", "java.util.stream.Stream"))
    override fun getMembers(): Stream<out TencentMember> = Stream.empty()

    @OptIn(Api4J::class)
    @Deprecated("Get member list is not supported", ReplaceWith("Stream.empty()", "java.util.stream.Stream"))
    override fun getMembers(groupingId: ID?): Stream<out TencentMember> = Stream.empty()

    @OptIn(Api4J::class)
    @Deprecated("Get member list is not supported", ReplaceWith("Stream.empty()", "java.util.stream.Stream"))
    override fun getMembers(limiter: Limiter): Stream<out TencentMember> = Stream.empty()


    @JvmSynthetic
    override suspend fun roles(groupingId: ID?, limiter: Limiter): Flow<TencentRole>

    @Api4J
    override fun getRoles(groupingId: ID?, limiter: Limiter): Stream<out TencentRole>

    //// Impls

    @JvmSynthetic
    override suspend fun unmute(): Boolean = false

    @JvmSynthetic
    override suspend fun previous(): Organization? = null


    @OptIn(Api4J::class)
    override val previous: Organization?
        get() = null
}