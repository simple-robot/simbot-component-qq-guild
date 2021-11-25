package love.forte.simbot.tencentguild.internal

import io.ktor.client.*
import kotlinx.coroutines.flow.Flow
import love.forte.simbot.*
import love.forte.simbot.tencentguild.TencentGuildBot
import love.forte.simbot.tencentguild.TencentGuildBotID
import love.forte.simbot.definition.Friend
import love.forte.simbot.definition.Group
import love.forte.simbot.definition.UserStatus
import love.forte.simbot.message.Image
import love.forte.simbot.resources.Resource
import love.forte.simbot.tencentguild.TencentGuildApi
import love.forte.simbot.tencentguild.TencentGuildBotConfiguration
import kotlin.coroutines.CoroutineContext

/**
 *
 * @author ForteScarlet
 */
internal class TencentGuildBotImpl(
    configuration: TencentGuildBotConfiguration,
    override val coroutineContext: CoroutineContext,
    override val status: UserStatus
) : TencentGuildBot() {

    override val client: HttpClient = configuration.client

    override val component: Component
        get() = TencentGuildApi.component

    override val id: TencentGuildBotID = TencentGuildBotID(configuration.ticket!!)
    override val info: BotInfo
        get() = TODO("Not yet implemented")

    override val manager: BotManager<Bot>
        get() = TODO("Not yet implemented")



    override suspend fun friends(grouping: Grouping, limiter: Limiter): Flow<Friend> {
        TODO("Not yet implemented")
    }

    override suspend fun groups(grouping: Grouping, limiter: Limiter): Flow<Group> {
        TODO("Not yet implemented")
    }

    override suspend fun guilds(grouping: Grouping, limiter: Limiter): Flow<Group> {
        TODO("Not yet implemented")
    }

    override suspend fun uploadImage(resource: Resource): Image {
        TODO("Not yet implemented")
    }

    override suspend fun cancel() {
        TODO("Not yet implemented")
    }

    override suspend fun join() {
        TODO("Not yet implemented")
    }

}