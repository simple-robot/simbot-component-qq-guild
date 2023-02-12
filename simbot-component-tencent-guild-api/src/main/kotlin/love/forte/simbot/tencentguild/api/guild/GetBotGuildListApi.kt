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

package love.forte.simbot.tencentguild.api.guild

import io.ktor.http.*
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.builtins.ListSerializer
import love.forte.simbot.ID
import love.forte.simbot.tencentguild.TencentGuildInfo
import love.forte.simbot.tencentguild.api.RouteInfoBuilder
import love.forte.simbot.tencentguild.api.TencentApi


/**
 *
 * [guilds](https://bot.q.qq.com/wiki/develop/api/openapi/user/guilds.html)
 */
public class GetBotGuildListApi internal constructor(
    /**
     * 读此id之前的数据。
     *
     * [before] 设置时， 先反序，再分页
     */
    public val before: ID?,
    
    /**
     * 读此id之后的数据。
     *
     * [after] 和 [before] 同时设置时， after 参数无效
     */
    public val after: ID?,
    /**
     * 每次拉取多少条数据	最大不超过100，默认100
     */
    public val limit: Int = DEFAULT_LIMIT,
) : TencentApi<List<TencentGuildInfo>>() {
    
    public companion object Factory {
        private val route = arrayOf("users", "@me", "guilds")
        private val serializer = ListSerializer(TencentGuildInfo.serializer)
        private const val DEFAULT_LIMIT: Int = 100
        
        /**
         * 构造 [GetBotGuildListApi]
         */
        @JvmStatic
        @JvmOverloads
        public fun create(before: ID? = null, after: ID? = null, limit: Int = DEFAULT_LIMIT): GetBotGuildListApi =
            GetBotGuildListApi(before, after, limit)
        
        /**
         * 构造 [GetBotGuildListApi]
         */
        @JvmStatic
        public fun create(limit: Int): GetBotGuildListApi =
            GetBotGuildListApi(before = null, after = null, limit)
        
        
        /**
         * 构造 [GetBotGuildListApi]。提供 [GetBotGuildListApi.before] 属性。
         *
         * @param limit [GetBotGuildListApi.limit]
         */
        @JvmStatic
        @JvmOverloads
        public fun createByBefore(before: ID, limit: Int = DEFAULT_LIMIT): GetBotGuildListApi =
            create(before = before, after = null, limit)
        
        
        /**
         * 构造 [GetBotGuildListApi]。提供 [GetBotGuildListApi.after] 属性。
         *
         * @param limit [GetBotGuildListApi.limit]
         */
        @JvmStatic
        @JvmOverloads
        public fun createByAfter(after: ID, limit: Int = DEFAULT_LIMIT): GetBotGuildListApi =
            create(before = null, after = after, limit)
        
        
    }
    
    
    override val resultDeserializer: DeserializationStrategy<List<TencentGuildInfo>>
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
    
}




