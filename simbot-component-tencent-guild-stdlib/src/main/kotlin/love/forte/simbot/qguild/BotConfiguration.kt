/*
 * Copyright (c) 2022-2023. ForteScarlet.
 *
 * This file is part of simbot-component-tencent-guild.
 *
 * simbot-component-tencent-guild is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * simbot-component-tencent-guild is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with simbot-component-tencent-guild. If not, see <https://www.gnu.org/licenses/>.
 */

package love.forte.simbot.qguild

import io.ktor.client.*
import io.ktor.http.*
import kotlinx.serialization.json.Json
import love.forte.simbot.ExceptionProcessor
import love.forte.simbot.qguild.event.EventIntents
import love.forte.simbot.qguild.event.Intents
import love.forte.simbot.qguild.event.Shard
import love.forte.simbot.qguild.event.Signal
import love.forte.simbot.qguild.internal.BotImpl
import kotlin.coroutines.CoroutineContext

/**
 * 用于构建 [Bot] 的工厂类型，提供一些工厂函数。
 */
public object BotFactory {

    /**
     * 构造一个 [Bot].
     */
    @JvmStatic
    public fun create(appId: String, secret: String, token: String, config: ConfigurableBotConfiguration.() -> Unit = {}): Bot {
        val ticket = Bot.Ticket(appId, secret, token)
        val configuration = ConfigurableBotConfiguration().also(config)
        return BotImpl(ticket, configuration.release())
    }

}

/**
 * [Bot] 所需的配置信息。
 */
public interface BotConfiguration {

    /**
     * Context.
     *
     * 如果存在Job，则会被作为parentJob。
     */
    public val coroutineContext: CoroutineContext

    /**
     * 此bot建立的链接所使用的 shard。默认情况下使用 [Shard.FULL].
     */
    public val shard: Shard

    /**
     * bot需要订阅的事件 [Intents].
     *
     * 默认订阅如下事件：
     * - [频道相关事件][EventIntents.Guilds]
     * - [频道成员相关事件][EventIntents.GuildMembers]
     * - [公域消息相关事件][EventIntents.PublicGuildMessages]
     */
    @get:JvmSynthetic
    public val intents: Intents


    /**
     * @see intents
     */
    public val intentsValue: Int get() = intents.value

    /**
     * 异常处理器。
     * TODO
     */
    public val exceptionHandler: ExceptionProcessor<Unit>?

    /**
     * 用作 [Signal.Identify.Data.properties] 中的参数。
     *
     */
    public val clientProperties: Map<String, String>

    /**
     * 请求的服务器地址。默认为 [QQGuild.URL]. 即正式地址。
     */
    public val serverUrl: Url


    /**
     * 用于API请求的 [HttpClient].
     *
     * 如果为null则会构建一个默认的 client:
     * ```kotlin
     * HttpClient {
     *     install(ContentNegotiation) {
     *         json()
     *     }
     * }
     * ```
     *
     */
    public val apiClient: HttpClient?

    /**
     * 用于API请求结果反序列化的 [Json].
     *
     * 如果为null则会使用一个默认的 Json:
     * ```kotlin
     * Json {
     *     isLenient = true
     *     ignoreUnknownKeys = true
     * }
     * ```
     *
     */
    public val apiDecoder: Json

}



