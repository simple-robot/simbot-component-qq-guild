/*
 *  Copyright (c) 2022-2022 ForteScarlet <ForteScarlet@163.com>
 *
 *  本文件是 simbot-component-tencent-guild 的一部分。
 *
 *  simbot-component-tencent-guild 是自由软件：你可以再分发之和/或依照由自由软件基金会发布的 GNU 通用公共许可证修改之，无论是版本 3 许可证，还是（按你的决定）任何以后版都可以。
 *
 *  发布 simbot-component-tencent-guild 是希望它能有用，但是并无保障;甚至连可销售和符合某个特定的目的都不保证。请参看 GNU 通用公共许可证，了解详情。
 *
 *  你应该随程序获得一份 GNU 通用公共许可证的复本。如果没有，请看:
 *  https://www.gnu.org/licenses
 *  https://www.gnu.org/licenses/gpl-3.0-standalone.html
 *  https://www.gnu.org/licenses/lgpl-3.0-standalone.html
 *
 *
 */

package love.forte.simbot.tencentguild.api.message.direct

import io.ktor.http.*
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.ID
import love.forte.simbot.tencentguild.DirectMessageSession
import love.forte.simbot.tencentguild.api.RouteInfoBuilder
import love.forte.simbot.tencentguild.api.TencentApi

/**
 * [创建私信会话](https://bot.q.qq.com/wiki/develop/api/openapi/dms/post_dms.html)
 * ## 接口
 * `POST /users/@me/dms`
 *
 * ## 功能描述
 * 用于机器人和在同一个频道内的成员创建私信会话。
 *
 * 机器人和用户存在共同频道才能创建私信会话。
 * 创建成功后，返回创建成功的频道 `id` ，子频道 `id` 和创建时间。
 *
 * ## 参数
 * | 字段名 | 类型 | 描述 |
 * |-----|-----|-----|
 * | `recipient_id` | `string` | 接收者 id |
 * | `source_guild_id` | `string` | 源频道 id |
 *
 *
 * @author ForteScarlet
 */
public class CreateDmsApi(
    recipientId: ID,
    sourceGuildId: ID,
) : TencentApi<DirectMessageSession>() {
    override val resultDeserializer: DeserializationStrategy<DirectMessageSession>
        get() = DirectMessageSession.serializer
    
    override val method: HttpMethod
        get() = HttpMethod.Post
    
    override fun route(builder: RouteInfoBuilder) {
        builder.apiPath = route
        builder.contentType
    }
    
    private val _body = Body(recipientId, sourceGuildId)
    
    override val body: Any get() = _body
    
    @Serializable
    private data class Body(
        @Serializable(ID.AsCharSequenceIDSerializer::class) @SerialName("recipient_id") private val recipientId: ID,
        @Serializable(ID.AsCharSequenceIDSerializer::class) @SerialName("source_guild_id") private val sourceGuildId: ID,
    )
    
    public companion object {
        // POST /users/@me/dms
        private val route = listOf("users", "@me", "dms")
    }
}
