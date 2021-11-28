package love.forte.simbot.tencentguild.core.internal

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.tencentguild.TencentBot
import love.forte.simbot.tencentguild.TencentBotConfiguration


/**
 *
 * @author ForteScarlet
 */
internal class TencentBotImpl(
    override val ticket: TicketImpl,
    config: TencentBotConfiguration
): TencentBot {

    override suspend fun start(): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun cancel(): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun join() {
        TODO("Not yet implemented")
    }

    override val totalShared: Int
        get() = TODO("Not yet implemented")

    override val clients: List<TencentBot.Client>
        get() = TODO("Not yet implemented")

}




@Serializable
internal data class TicketImpl(
    @SerialName("app_id")
    override val appId: String,
    @SerialName("app_key")
    override val appKey: String,
    override val token: String
) : TencentBot.Ticket