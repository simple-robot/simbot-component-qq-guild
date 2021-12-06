package love.forte.simbot.component.tencentguild

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.toList
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
    override suspend fun mute(): Boolean
    override suspend fun roles(): Flow<TencentRole>

    @Api4J
    override val organization: TencentGuild get() = runBlocking { organization() }

    @Api4J
    override val roles: List<TencentRole> get() = runBlocking { roles().toList() }

    override suspend fun organization(): TencentGuild
}