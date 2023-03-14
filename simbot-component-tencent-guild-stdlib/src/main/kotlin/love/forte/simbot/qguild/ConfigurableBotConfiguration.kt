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
import love.forte.simbot.qguild.event.EventIntents
import love.forte.simbot.qguild.event.Intents
import love.forte.simbot.qguild.event.Shard
import love.forte.simbot.qguild.event.Signal
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext


/**
 * 对于一个Bot的配置信息。
 * 如果在配置bot之后对内容进行后续修改，可能会影响到当前bot的使用。
 */
@Suppress("MemberVisibilityCanBePrivate")
public class ConfigurableBotConfiguration : BotConfiguration {

    /**
     * Context.
     *
     * 如果存在Job，则会被作为parentJob。
     */
    override var coroutineContext: CoroutineContext = EmptyCoroutineContext

    /**
     * 此bot建立的链接所使用的 shard。默认情况下使用 [Shard.FULL].
     */
    override var shard: Shard = Shard.FULL

    /**
     * bot需要订阅的事件 [Intents].
     *
     * 默认订阅如下事件：
     * - [频道相关事件][EventIntents.Guilds]
     * - [频道成员相关事件][EventIntents.GuildMembers]
     * - [公域消息相关事件][EventIntents.PublicGuildMessages]
     */
    @get:JvmSynthetic
    override var intents: Intents =
        EventIntents.Guilds.intents + EventIntents.GuildMembers.intents + EventIntents.PublicGuildMessages.intents

    /**
     * 异常处理器。
     * TODO
     */
    override var exceptionHandler: ExceptionProcessor<Unit>? = null

    /**
     * 用作 [Signal.Identify.Data.properties] 中的参数。
     *
     */
    override var clientProperties: Map<String, String> = emptyMap()

    /**
     * 请求的服务器地址。默认为 [QQGuild.URL]. 即正式地址。
     */
    override var serverUrl: Url = QQGuild.URL

    /**
     * 使 [BotConfiguration.serverUrl] 为 [QQGuild.SANDBOX_URL]
     */
    public fun useSandboxServerUrl() {
        serverUrl = QQGuild.SANDBOX_URL
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
    override var apiClient: HttpClient? = null

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
    override var apiDecoder: Json = defaultJson


    public companion object {
        private val defaultJson = Json {
            isLenient = true
            ignoreUnknownKeys = true
        }
    }


    internal fun release(): BotConfiguration = BotConfigurationImpl(
        coroutineContext = coroutineContext,
        shard = shard,
        intents = intents,
        exceptionHandler = exceptionHandler,
        clientProperties = clientProperties,
        serverUrl = serverUrl,
        apiClient = apiClient,
        apiDecoder = apiDecoder,
    )
}

private class BotConfigurationImpl(
    override val coroutineContext: CoroutineContext,
    override val shard: Shard,
    override val intents: Intents,
    override val exceptionHandler: ExceptionProcessor<Unit>?,
    override val clientProperties: Map<String, String>,
    override val serverUrl: Url,
    override val apiClient: HttpClient?,
    override val apiDecoder: Json
) : BotConfiguration
