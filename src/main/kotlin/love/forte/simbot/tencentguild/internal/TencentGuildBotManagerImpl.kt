package love.forte.simbot.tencentguild.internal

import love.forte.simbot.Bot
import love.forte.simbot.Component
import love.forte.simbot.ID
import love.forte.simbot.tencentguild.TencentGuildApi
import love.forte.simbot.tencentguild.TencentGuildBot
import love.forte.simbot.tencentguild.TencentGuildBotManager
import love.forte.simbot.tencentguild.TencentGuildBotManagerConfiguration

/**
 *
 * @author ForteScarlet
 */
internal class TencentGuildBotManagerImpl(
    internal val configuration: TencentGuildBotManagerConfiguration
) : TencentGuildBotManager() {

    override val component: Component
        get() = TencentGuildApi.component



    override suspend fun doCancel() {
        TODO("Not yet implemented")
    }

    override fun get(id: ID): TencentGuildBot? {
        TODO("Not yet implemented")
    }

    override suspend fun register(properties: Map<String, String>): Bot {
        TODO("Not yet implemented")
    }
}