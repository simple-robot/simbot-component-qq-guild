package love.forte.simbot.api.tencentguild.internal

import kotlinx.coroutines.flow.Flow
import love.forte.simbot.*
import love.forte.simbot.api.tencentguild.TencentGuildBot
import love.forte.simbot.api.tencentguild.TencentGuildBotID
import love.forte.simbot.api.tencentguild.tgbComponent
import love.forte.simbot.definition.Friend
import love.forte.simbot.definition.Group
import love.forte.simbot.definition.UserStatus
import kotlin.coroutines.CoroutineContext

/**
 *
 * @author ForteScarlet
 */
internal class TencentGuildBotImpl(
    private val ticket: TencentGuildBot.Ticket,
    override val coroutineContext: CoroutineContext,
    override val status: UserStatus
) : TencentGuildBot {

    override val component: Component
        get() = tgbComponent

    override val id: TencentGuildBotID = TencentGuildBotID(ticket)
    override val info: BotInfo
        get() = TODO("Not yet implemented")

    override val manager: BotManager<Bot>
        get() = TODO("Not yet implemented")

    override suspend fun cancel() {
        TODO("Not yet implemented")
    }

    override suspend fun friends(grouping: Grouping, limiter: Limiter): Flow<Friend> {
        TODO("Not yet implemented")
    }

    override suspend fun groups(grouping: Grouping, limiter: Limiter): Flow<Group> {
        TODO("Not yet implemented")
    }

    override suspend fun guilds(grouping: Grouping, limiter: Limiter): Flow<Group> {
        TODO("Not yet implemented")
    }

    override suspend fun join() {
        TODO("Not yet implemented")
    }

}