package love.forte.simbot.tencentguild.api.guild

import io.ktor.http.*
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.builtins.ListSerializer
import love.forte.simbot.ID
import love.forte.simbot.Limiter
import love.forte.simbot.tencentguild.TencentGuildInfo
import love.forte.simbot.tencentguild.api.RouteInfoBuilder
import love.forte.simbot.tencentguild.api.TencentApi


/**
 * [Limiter.offset] 是无效的。
 *
 * https://bot.q.qq.com/wiki/develop/api/openapi/guild/get_guild.html
 */
public class BotGuildListApi(
    private val before: ID? = null,
    private val after: ID? = null,
    private val limit: Int
) : TencentApi<List<TencentGuildInfo>> {

    override val resultDeserializer: DeserializationStrategy<out List<TencentGuildInfo>>
        get() = serializer

    override val method: HttpMethod
        get() = HttpMethod.Get

    override fun route(builder: RouteInfoBuilder) {
        builder.apiPath = route
        if (before != null) {
            builder.parametersAppender.append("before", before.toString())
        }
        if (after != null) {
            builder.parametersAppender.append("after", after.toString())
        }
        val limit = limit
        if (limit > 0) {
            builder.parametersAppender.append("limit", limit)
        }
    }

    override val body: Any?
        get() = null

    public companion object {
        private val route = listOf("users", "@me", "guilds")
        private val serializer = ListSerializer(TencentGuildInfo.serializer)
    }
}




