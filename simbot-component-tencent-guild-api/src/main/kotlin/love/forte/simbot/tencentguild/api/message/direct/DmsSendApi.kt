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
import love.forte.simbot.ID
import love.forte.simbot.literal
import love.forte.simbot.tencentguild.TencentMessage
import love.forte.simbot.tencentguild.api.RouteInfoBuilder
import love.forte.simbot.tencentguild.api.TencentApi
import love.forte.simbot.tencentguild.api.message.MessageSendApi
import love.forte.simbot.tencentguild.api.message.SendMessageResult
import love.forte.simbot.tencentguild.api.message.TencentMessageForSending

/**
 * [发送私信](https://bot.q.qq.com/wiki/develop/api/openapi/dms/post_dms_messages.html)
 * ## 接口
 * `POST /dms/{guild_id}/messages`
 *
 * ## 功能描述
 * 用于发送私信消息，前提是已经创建了私信会话。
 *
 * * 私信的 `guild_id` 在创建私信会话时以及私信消息事件中获取。
 * * 私信场景下，每个机器人每天可以对一个用户发 `2` 条主动消息。
 * * 私信场景下，每个机器人每天累计可以发 `200` 条主动消息。
 * * 私信场景下，被动消息没有条数限制。
 *
 * ## 参数
 * 和 [发送消息][MessageSendApi] 参数一致。
 *
 * ## 返回
 * 和 [发送消息][MessageSendApi] 返回一致。
 *
 *
 * @see MessageSendApi
 *
 * @author ForteScarlet
 */
public class DmsSendApi(dmsId: ID, override val body: TencentMessageForSending) : TencentApi<TencentMessage>() {
    @JvmOverloads
    public constructor(dmsId: ID, content: String, msgId: ID? = null) : this(
        dmsId,
        TencentMessageForSending(content = content, msgId = msgId)
    )
    
    @JvmOverloads
    public constructor(dmsId: ID, embed: TencentMessage.Embed, msgId: ID? = null) : this(
        dmsId,
        TencentMessageForSending(embed = embed, msgId = msgId)
    )
    
    @JvmOverloads
    public constructor(dmsId: ID, ark: TencentMessage.Ark, msgId: ID? = null) : this(
        dmsId,
        TencentMessageForSending(ark = ark, msgId = msgId)
    )
    
    // POST /channels/{channel_id}/messages
    private val path: List<String> = listOf("dms", dmsId.literal, "messages")
    
    override val resultDeserializer: DeserializationStrategy<out TencentMessage>
        get() = SendMessageResult.serializer()
    
    override val method: HttpMethod
        get() = HttpMethod.Post
    
    override fun route(builder: RouteInfoBuilder) {
        builder.apiPath = path
    }
}
