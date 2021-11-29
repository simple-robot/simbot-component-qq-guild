package love.forte.simbot.tencentguild.api

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


public sealed class GatewayApis<R : GatewayInfo>(protected val path: List<String>, override val resultDeserializer: DeserializationStrategy<out R>) : GetTencentApi<R> {

    override fun route(builder: RouteInfoBuilder) {
        builder.apiPath = path
    }

    public object Normal : GatewayApis<Gateway>(listOf("gateway"), Gateway.serializer())
    public object Shared : GatewayApis<GatewayWithShard>(listOf("gateway", "bot"), GatewayWithShard.serializer())
}



/**
 * Base sealed class for [Gateway] and [GatewayWithShard].
 */
@Serializable
public sealed class GatewayInfo {
    public abstract val url: String
}

/**
 * [https://bot.q.qq.com/wiki/develop/api/openapi/wss/url_get.html#%E8%BF%94%E5%9B%9E]
 * 一个用于连接 websocket 的地址。
 */
@SerialName("n")
@Serializable
public data class Gateway(override val url: String) : GatewayInfo()


/**
 * [https://bot.q.qq.com/wiki/develop/api/openapi/wss/shard_url_get.html#%E8%BF%94%E5%9B%9E]
 * 一个用于连接 websocket 的地址。同时返回建议的分片数，以及目前连接数使用情况。
 */
@SerialName("s")
@Serializable
public data class GatewayWithShard(
    override val url: String,
    public val shards: Int,
    @SerialName("session_start_limit")
    public val sessionStartLimit: SessionStartLimit
) : GatewayInfo()


/**
 * [https://bot.q.qq.com/wiki/develop/api/openapi/wss/shard_url_get.html#sessionstartlimit]
 *
 */
@Serializable
public data class SessionStartLimit(
    /**
     * 每 24 小时可创建 Session 数
     */
    public val total: Int,
    /**
     * 目前还可以创建的 Session 数
     */
    public val remaining: Int,
    /**
     * 重置计数的剩余时间(ms)
     */
    @SerialName("reset_after")
    public val resetAfter: Int,
    /**
     * 每 5s 可以创建的 Session 数
     */
    @SerialName("max_concurrency")
    public val maxConcurrency: Int,
)