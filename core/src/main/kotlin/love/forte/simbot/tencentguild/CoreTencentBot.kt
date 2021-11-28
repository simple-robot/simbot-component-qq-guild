package love.forte.simbot.tencentguild

import io.ktor.client.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.features.websocket.*
import io.ktor.http.cio.websocket.*
import love.forte.simbot.tencentguild.core.internal.TencentBotImpl
import love.forte.simbot.tencentguild.core.internal.TicketImpl


public fun tencentBot(
    appId: String,
    appKey: String,
    token: String,
    configBlock: TencentBotConfiguration.() -> Unit
): TencentBot {
    val ticket = TicketImpl(appId, appKey, token)
    val config = TencentBotConfiguration().also(configBlock)


    val bot = TencentBotImpl(ticket, config)

    TODO()
}


public class TencentBotConfiguration() {

    /**
     * 分片总数。如果为0，则会通过 [love.forte.simbot.tencentguild.api.GatewayWithShard] 来决定分片结果。
     */
    public var totalShared: Int = 0


    /**
     * 根据一个分片信息，得到这个分片下需要监听的事件类型。
     *
     * @see Intents
     */
    public var intentsForShared: (Int) -> Intents = {
        EventSignals.allIntents
    }

    /**
     * 提供一个 [HttpClient] 实例用于内部的api请求和ws请求。
     *
     * 因为需要ws请求，因此你需要 `install(WebSockets)`
     *
     */
    @OptIn(ExperimentalWebSocketExtensionApi::class)
    public var httpClient: HttpClient = HttpClient() {
        install(JsonFeature) {
            serializer = KotlinxSerializer(kotlinx.serialization.json.Json {
                isLenient = true
                ignoreUnknownKeys = true
                classDiscriminator = "\$t_"
            })
        }
        // for install web socket
        install(WebSockets)


    }


}

