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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.serialization.json.Json
import love.forte.plugin.suspendtrans.annotation.JvmAsync
import love.forte.plugin.suspendtrans.annotation.JvmBlocking
import love.forte.simbot.Api4J
import love.forte.simbot.qguild.api.GatewayInfo
import love.forte.simbot.qguild.api.user.GetBotInfoApi
import love.forte.simbot.qguild.event.Shard
import love.forte.simbot.qguild.event.Signal
import love.forte.simbot.qguild.model.User
import java.util.function.BiConsumer
import kotlin.coroutines.CoroutineContext


/**
 * 一个 QQ频道Bot。
 *
 * @author ForteScarlet
 */
public interface Bot : CoroutineScope {
    override val coroutineContext: CoroutineContext

    /**
     * 当前bot的 [Ticket].
     */
    public val ticket: Ticket

    /**
     * Bot当前的配置信息。
     */
    public val configuration: BotConfiguration

    /**
     * bot用于api请求的 [HttpClient].
     */
    public val apiClient: HttpClient

    /**
     * 用于api请求并反序列化的 [Json]
     */
    public val apiDecoder: Json

    /**
     * 当前bot所使用的
     */
    public val apiServer: Url

    /**
     * 添加一个事件**预处理器**。
     *
     * [registerPreProcessor] 与 [processor] 不同的是，
     * [registerPreProcessor] 所注册的所有事件处理器会在接收到事件的时候以**同步顺序**的方式依次执行，
     * 而后再交由下游的 [processor] 处理。
     *
     * 也同样因此，尽可能避免在 [registerPreProcessor] 中执行任何耗时逻辑，这可能会对事件处理流程造成严重阻塞。
     *
     * @param processor 用于处理事件的函数类型
     */
    @JvmSynthetic
    public fun registerPreProcessor(processor: EventProcessor)

    /**
     * 添加一个事件处理器。
     *
     */
    @JvmSynthetic
    public fun registerProcessor(processor: EventProcessor)


    /**
     * [registerProcessor] for java
     */
    @Api4J
    public fun registerBlockingProcessor(processor: BlockingEventProcessor) {
        registerProcessor(processor.parse())
    }

    /**
     * [registerProcessor] for java
     */
    @Api4J
    public fun <E : Signal.Dispatch> registerBlockingProcessor(
        eventType: Class<out E>,
        processor: BiConsumer<E, String>
    ) {
        registerProcessor { raw ->
            if (eventType.isInstance(this)) {
                val event = eventType.cast(this)
                processor.accept(event, raw)
            }
        }
    }

    /**
     * [票据](https://bot.q.qq.com/wiki/develop/api/#%E7%A5%A8%E6%8D%AE)
     *
     * 申请机器人通过后，平台将会下发三个票据。
     *
     */
    public data class Ticket(
        /**
         * 用于识别一个机器人的 id
         */
        val appId: String,

        /**
         * 用于在 oauth 场景进行请求签名的密钥
         */
        val secret: String,

        /**
         * 机器人token，用于以机器人身份调用 openapi，格式为 `${app_id}.${random_str}`
         */
        val token: String
    )


//    /**
//     * 拼接bot token
//     *
//     * 使用申请机器人时平台返回的机器人 appID + token 拼接而成。此时，所有的操作都是以机器人身份来完成的。
//     *
//     * ```
//     * Authorization: Bot 100000.Cl2FMQZnCjm1XVW7vRze4b7Cq4se7kKWs
//     * ```
//     */
//    public val botToken: String

    /**
     * 启动当前BOT。如果已经存在 [client], 则会关闭已存连接并重新连接。
     */
    @JvmBlocking
    @JvmAsync
    public suspend fun start(): Boolean


    /**
     * 启动当前BOT。如果已经存在 [client], 则会关闭已存连接并重新连接。
     *
     * @param gateway 提供准备好的路由信息。
     */
    @JvmBlocking
    @JvmAsync
    public suspend fun start(gateway: GatewayInfo): Boolean

    /**
     * 终止当前BOT。
     *
     * @return 当且仅当此BOT未关闭且关闭成功才会得到true。
     */
    @JvmBlocking
    @JvmAsync
    public suspend fun cancel(reason: Throwable? = null): Boolean


    /**
     * 挂起直到此bot被 [cancel]. 如果已经 [cancel], 则不会挂起。
     */
    @JvmBlocking
    @JvmAsync
    public suspend fun join()


    /**
     * 此bot使用的 [shard] 信息。
     *
     */
    public val shard: Shard


    /**
     * 此Bot中的连接信息。
     *
     * 如果当前处于重连、重启的状态，得到的 [client] 中 [client.isActive][Client.isActive] 可能为 `false`。
     *
     * @throws IllegalStateException 如果尚未启动过当前bot
     */
    public val client: Client


    /**
     * Bot的连接信息。一般来讲代表一个ws连接。
     */
    public interface Client {
        /**
         * 此连接所属的 [Bot]
         */
        public val bot: Bot

        /**
         * 事件推送所携带的 `s`.
         */
        public val seq: Long

        /**
         * 当前 client 是否处于运行状态。
         */
        public val isActive: Boolean
    }


    //// some self api

    /**
     * 通过 api [GetBotInfoApi] 查询bot自身信息。
     */
    @JvmBlocking(asProperty = true)
    @JvmAsync(asProperty = true, suffix = "")
    public suspend fun me(): User
}

/**
 * 用于处理事件的函数。
 */
public fun interface EventProcessor {
    /**
     * 处理事件
     *
     * @param raw 本次事件接收到的原始json字符串
     * @receiver 事件的反序列化结果。具体类型可参考其各个实现类，例如 [love.forte.simbot.qguild.event.GuildCreate]
     *
     */
    public suspend fun Signal.Dispatch.invoke(raw: String)
}

/**
 * 用于处理事件的函数。
 *
 * 用于面向Java兼容 [EventProcessor] 类型。
 */
public fun interface BlockingEventProcessor {
    /**
     * 处理事件
     *
     * @param raw 本次事件接收到的原始json字符串
     * @param event 事件的反序列化结果。具体类型可参考其各个实现类，例如 [love.forte.simbot.qguild.event.GuildCreate]
     *
     */
    public fun invoke(event: Signal.Dispatch, raw: String)
}

internal fun BlockingEventProcessor.parse(): EventProcessor = EventProcessor { raw -> invoke(this, raw) }
