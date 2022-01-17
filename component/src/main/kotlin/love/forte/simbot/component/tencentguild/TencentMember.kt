package love.forte.simbot.component.tencentguild

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.runBlocking
import love.forte.simbot.Api4J
import love.forte.simbot.Bot
import love.forte.simbot.ID
import love.forte.simbot.Timestamp
import love.forte.simbot.definition.Member
import love.forte.simbot.definition.MemberInfo
import love.forte.simbot.definition.UserStatus
import love.forte.simbot.tencentguild.TencentMemberInfo
import love.forte.simbot.tencentguild.TencentUserInfo
import java.util.concurrent.TimeUnit
import java.util.stream.Stream
import kotlin.time.Duration

/**
 *
 * @author ForteScarlet
 */
public interface TencentMember : Member, MemberInfo, TencentMemberInfo {
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

    //// Impl



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