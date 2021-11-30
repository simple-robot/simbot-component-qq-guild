package love.forte.simbot.component.tencentguild.internal

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import love.forte.simbot.Api4J
import love.forte.simbot.component.tencentguild.TencentGuild
import love.forte.simbot.component.tencentguild.TencentMember
import love.forte.simbot.definition.Role
import love.forte.simbot.definition.UserStatus
import love.forte.simbot.tencentguild.TencentMemberInfo
import kotlin.streams.toList

/**
 *
 * @author ForteScarlet
 */
internal class TencentMemberImpl(
    override val bot: TencentGuildBotImpl,
    private val info: TencentMemberInfo,
    private val guild: TencentGuild
) : TencentMember, TencentMemberInfo by info {

    override val status: UserStatus =
        if (info.id == bot.id) bot.status
        else UserStatus.builder().also {
            if (info.user.isBot) {
                it.bot()
                it.fakeUser()
            } else {
                it.normal()
            }
        }.build()

    override suspend fun mute(): Boolean = false

    override suspend fun roles(): Flow<Role> {
        val roleIds = info.roleIds.mapTo(mutableSetOf()) { it.toString() }
        return guild.roles().filter { it.id.toString() in roleIds }
    }

    @Api4J
    override val roles: List<Role>
        get() {
            val roleIds = info.roleIds.mapTo(mutableSetOf()) { it.toString() }
            return guild.getRoles().filter { it.id.toString() in roleIds }.toList()
        }

}