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
import kotlinx.serialization.builtins.serializer
import love.forte.simbot.ID
import love.forte.simbot.literal
import love.forte.simbot.tencentguild.api.RouteInfoBuilder
import love.forte.simbot.tencentguild.api.TencentApi

/**
 * [撤回私信](https://bot.q.qq.com/wiki/develop/api/openapi/dms/delete_dms.html)
 *
 * ## 接口
 * `DELETE /dms/{guild_id}/messages/{message_id}?hidetip=false`
 *
 * ## 参数
 * | 字段名 | 类型 | 描述 |
 * |------|------|-----|
 * | `hidetip` | `bool` | 选填，是否隐藏提示小灰条，true 为隐藏，false 为显示。默认为false |
 *
 * ## 功能描述
 * 用于撤回私信频道 `guild_id` 中 `message_id` 指定的私信消息。只能用于撤回机器人自己发送的私信。
 *
 * ### 注意
 * * 公域机器人暂不支持申请，仅私域机器人可用，选择私域机器人后默认开通。
 * * 注意: 开通后需要先将机器人从频道移除，然后重新添加，方可生效。
 *
 *
 * @param hidetip 是否隐藏提示小灰条，`true` 为隐藏，`false` 为显示。默认为 `false`
 *
 *
 *
 *
 * @author ForteScarlet
 */
public class DeleteDmsApi internal constructor(
    guildId: ID,
    messageId: ID,
    private val hidetip: Boolean = false,
) : TencentApi<Unit>() {
    public companion object Factory {
        
        /**
         * 构造 [DeleteDmsApi]
         *
         */
        @JvmStatic
        @JvmOverloads
        public fun create(guildId: ID, messageId: ID, hidetip: Boolean = false): DeleteDmsApi =
            DeleteDmsApi(guildId, messageId, hidetip)
        
    }
    
    override val resultDeserializer: DeserializationStrategy<Unit>
        get() = Unit.serializer()
    
    override val method: HttpMethod
        get() = HttpMethod.Delete
    
    // /dms/{guild_id}/messages/{message_id}
    private val path = arrayOf("dms", guildId.literal, "messages", messageId.literal)
    
    override fun route(builder: RouteInfoBuilder) {
        builder.apiPath = path
        builder.parametersAppender.append("hidetip", hidetip)
    }
    
    override val body: Any?
        get() = null
}
