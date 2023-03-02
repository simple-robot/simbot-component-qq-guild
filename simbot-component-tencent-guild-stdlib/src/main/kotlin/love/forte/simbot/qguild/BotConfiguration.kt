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
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.websocket.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import love.forte.simbot.ExceptionProcessor
import love.forte.simbot.qguild.event.Intents
import love.forte.simbot.qguild.event.Shard
import love.forte.simbot.qguild.event.Signal
import love.forte.simbot.qguild.internal.BotImpl
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

//@JvmName("newBot")
//@JvmOverloads
//public fun newBot(
//    appId: String,
//    appKey: String,
//    token: String,
//    configBlock: BotConfiguration.() -> Unit = {},
//): Bot {
//    val ticket = TicketImpl(appId, appKey, token)
//    val config = BotConfiguration().also(configBlock)
//
//
//    return BotImpl(ticket, config)
//}

public object BotFactory {

    /**
     * 构造一个 [Bot].
     */
    @JvmStatic
    public fun create(appId: String, secret: String, token: String, config: BotConfiguration.() -> Unit = {}): Bot {
        val ticket = Bot.Ticket(appId, secret, token)
        val configuration = BotConfiguration().also(config)
        return BotImpl(ticket, configuration)
    }

}


/**
 * 对于一个Bot的配置信息。
 * 如果在配置bot之后对内容进行后续修改，可能会影响到当前bot的使用。
 */
@Suppress("MemberVisibilityCanBePrivate")
public class BotConfiguration {

    /**
     * Context.
     *
     * 如果存在Job，则会被作为parentJob。
     */
    public var coroutineContext: CoroutineContext = EmptyCoroutineContext

    /**
     * 此bot建立的链接所使用的 shard。默认情况下使用 [Shard.FULL].
     */
    public var shard: Shard = Shard.FULL

    /**
     * bot需要订阅的事件 [Intents]. 默认为0，即不订阅任何事件。
     */
    @get:JvmName("getIntents")
    @set:JvmName("setIntents")
    public var intents: Intents = Intents(0)

    /**
     * 异常处理器。
     * TODO
     */
    public var exceptionHandler: ExceptionProcessor<Unit>? = null

    /**
     * 用作 [Signal.Identify.Data.properties] 中的参数。
     *
     */
    public var clientProperties: Map<String, String> = emptyMap()

    /**
     * 请求的服务器地址。默认为 [QGuildApi.URL]. 即正式地址。
     */
    public var serverUrl: Url = QGuildApi.URL

    /**
     * 使 [BotConfiguration.serverUrl] 为 [QGuildApi.SANDBOX_URL]
     */
    public fun useSandboxServerUrl() {
        serverUrl = QGuildApi.SANDBOX_URL
    }

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
    public var apiClient: HttpClient? = null

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
    public var apiDecoder: Json = defaultJson

    public companion object {
        private val defaultJson = Json {
            isLenient = true
            ignoreUnknownKeys = true
        }
    }

}

