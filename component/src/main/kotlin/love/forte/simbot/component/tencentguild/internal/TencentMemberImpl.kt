package love.forte.simbot.component.tencentguild.internal

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.runBlocking
import love.forte.simbot.Api4J
import love.forte.simbot.component.tencentguild.TencentMember
import love.forte.simbot.component.tencentguild.TencentRole
import love.forte.simbot.definition.UserStatus
import love.forte.simbot.tencentguild.TencentMemberInfo
import java.util.concurrent.TimeUnit
import kotlin.streams.toList
import kotlin.time.Duration

/**
 *
 * @author ForteScarlet
 */
internal class TencentMemberImpl internal constructor(
    override val bot: TencentGuildBotImpl,
    private val info: TencentMemberInfo,
    private val guild: suspend () -> TencentGuildImpl
) : TencentMember, TencentMemberInfo by info {

    internal constructor(
        bot: TencentGuildBotImpl,
        info: TencentMemberInfo,
        guild: TencentGuildImpl
    ) : this(bot, info, { guild })

    override suspend fun organization(): TencentGuildImpl = guild()

    @Api4J
    override val organization: TencentGuildImpl
        get() = runBlocking { organization() }

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

    override suspend fun mute(duration: Duration): Boolean = false
    override suspend fun unmute(): Boolean = false

    @Api4J
    override fun muteBlocking(time: Long, unit: TimeUnit): Boolean = false

    @Api4J
    override fun unmuteBlocking(): Boolean = false

    override suspend fun roles(): Flow<TencentRole> {
        val roleIds = info.roleIds.mapTo(mutableSetOf()) { it.toString() }
        return guild().roles().filter { it.id.toString() in roleIds }
    }

    @Api4J
    override val roles: List<TencentRole>
        get() {
            val roleIds = info.roleIds.mapTo(mutableSetOf()) { it.toString() }
            return runBlocking { guild() }.getRoles().filter { it.id.toString() in roleIds }.toList()
        }

}