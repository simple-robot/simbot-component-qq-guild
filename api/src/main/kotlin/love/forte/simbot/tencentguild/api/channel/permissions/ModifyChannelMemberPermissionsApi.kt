package love.forte.simbot.tencentguild.api.channel.permissions

import io.ktor.http.*
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.Serializable
import love.forte.simbot.Api4J
import love.forte.simbot.ID
import love.forte.simbot.tencentguild.Permissions
import love.forte.simbot.tencentguild.TencentChannelPermissionsInfo
import love.forte.simbot.tencentguild.api.RouteInfoBuilder
import love.forte.simbot.tencentguild.api.TencentApi

/**
 *
 * [修改指定子频道的权限](https://bot.q.qq.com/wiki/develop/api/openapi/channel_permissions/put_channel_permissions.html)
 * @author ForteScarlet
 */
public class ModifyChannelMemberPermissionsApi(
    channelId: ID, memberId: ID,
    add: Permissions? = null,
    remove: Permissions? = null
) : TencentApi<TencentChannelPermissionsInfo>() {
    @Api4J
    public constructor(channelId: ID, memberId: ID, add: Long, remove: Long): this(channelId, memberId, Permissions(add), Permissions(remove))

    // GET /channels/{channel_id}/members/{user_id}/permissions
    private val path = listOf("channels", channelId.toString(), "members", memberId.toString(), "permissions")

    override val resultDeserializer: DeserializationStrategy<out TencentChannelPermissionsInfo>
        get() = TencentChannelPermissionsInfo.serializer

    override fun route(builder: RouteInfoBuilder) {
        builder.apiPath = path
    }

    override val method: HttpMethod
        get() = HttpMethod.Put

    override val body: Any = Body(add?.value?.toString(), remove?.value?.toString())

    @Serializable
    private data class Body(val add: String?, val remove: String?)
}