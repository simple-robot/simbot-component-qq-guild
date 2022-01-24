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
import love.forte.simbot.Api4J
import love.forte.simbot.Bot
import love.forte.simbot.ID
import love.forte.simbot.Timestamp
import love.forte.simbot.definition.GuildMember
import love.forte.simbot.definition.MemberInfo
import love.forte.simbot.definition.UserStatus
import love.forte.simbot.tencentguild.TencentMemberInfo
import love.forte.simbot.tencentguild.TencentUserInfo
import love.forte.simbot.utils.runInBlocking
import java.util.concurrent.TimeUnit
import java.util.stream.Stream
import kotlin.time.Duration

/**
 *
 * @author ForteScarlet
 */
public interface TencentMember : GuildMember, MemberInfo, TencentMemberInfo {
    override val guildId: ID?
    override val user: TencentUserInfo
    override val nick: String
    override val roleIds: List<ID>
    override val joinedAt: Timestamp
    override val avatar: String
    override val bot: Bot
    override val id: ID
    override val status: UserStatus
    override val username: String
    override suspend fun roles(): Flow<TencentRole>
    @Api4J
    override val roles: Stream<out TencentRole>
    override suspend fun organization(): TencentGuild
    override suspend fun guild(): TencentGuild
    //// Impl

    @Api4J
    override val guild: TencentGuild
        get() = runInBlocking { guild() }

    @Deprecated("子频道不支持禁言", ReplaceWith("false"))
    override suspend fun mute(duration: Duration): Boolean = false

    @Deprecated("子频道不支持禁言", ReplaceWith("false"))
    override suspend fun unmute(): Boolean = false

    @Deprecated("子频道不支持禁言", ReplaceWith("false"))
    @OptIn(Api4J::class)
    override fun muteBlocking(time: Long, unit: TimeUnit): Boolean = false

    @OptIn(Api4J::class)
    @Deprecated("子频道不支持禁言", ReplaceWith("false"))
    override fun unmuteBlocking(): Boolean = false

    @Api4J
    override val organization: TencentGuild
        get() = runBlocking { organization() }


}