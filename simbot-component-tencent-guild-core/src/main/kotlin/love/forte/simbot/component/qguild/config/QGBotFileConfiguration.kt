/*
 * Copyright (c) 2023. ForteScarlet.
 *
 * This file is part of simbot-component-tencent-guild.
 *
 * simbot-component-tencent-guild is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * simbot-component-tencent-guild is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with simbot-component-tencent-guild. If not, see <https://www.gnu.org/licenses/>.
 */

package love.forte.simbot.component.qguild.config

import io.ktor.http.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.qguild.Bot
import love.forte.simbot.qguild.BotConfiguration
import love.forte.simbot.qguild.QQGuild
import love.forte.simbot.qguild.event.EventIntents
import love.forte.simbot.qguild.event.Signal

// 仅用于文件序列化
/**
 * 标记一个类型为仅用于配置序列化的类型。
 *
 * 被标记的类型在除了配置序列化场景以外的情况下均不保证兼容和稳定，
 * 并且序列化的稳定以 `JSON` 格式为主要目标。
 */
@Retention(AnnotationRetention.SOURCE)
@MustBeDocumented
public annotation class UsedOnlyForConfigSerialization

/**
 * bot配置文件所对应的配置类，
 *
 * 通过由配置文件读取而来的信息来对指定Bot进行信息配置。
 *
 * ```json
 * {
 *    "component": "simbot.qqguild",
 *    "ticket": {
 *      "appId": "appId-value",
 *      "secret": "secret-value",
 *      "token": "token-value",
 *    },
 *    "config": { ... } // or null, or ignore
 * }
 * ```
 *
 */
@Suppress("MemberVisibilityCanBePrivate")
@Serializable
@UsedOnlyForConfigSerialization
public data class QGBotFileConfiguration(
    /**
     * bot相关的票据信息，必填。
     */
    val ticket: Ticket,

    /**
     * 其他配置信息。
     */
    val config: Config? = null
) {

    /**
     * bot票据信息。是与 [Bot.Ticket] 相对应的映射类
     * @see Bot.Ticket
     */
    @Serializable
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

    /**
     * 其他配置信息。
     *
     * ```json
     * {
     *   "config": { ... }
     * }
     * ```
     *
     */
    @Serializable
    @UsedOnlyForConfigSerialization
    public data class Config(
        /**
         * 目标服务器地址。默认为null，使用 [BotConfiguration] 的默认情况。
         *
         * 当 [serverUrl] 的值为特殊值：`"SANDBOX"` 时会选择使用 [QQGuild.SANDBOX_URL_STRING]
         *
         * 自定义服务器地址：
         *
         * ```json
         * {
         *   "serverUrl": "https://xxx.com"
         * }
         * ```
         *
         * `"SANDBOX"` 特殊值：
         *
         * ```json
         * {
         *   "serverUrl": "SANDBOX"
         * }
         * ```
         */
        val serverUrl: String? = null,

        /**
         * 分片信息配置，默认为 [ShardConfig.Full]
         *
         * ```json
         * {
         *   "shard": {...}
         * }
         * ```
         *
         * @see ShardConfig
         *
         */
        @SerialName("shard")
        val shardConfig: ShardConfig = ShardConfig.Full,

        /**
         * 事件订阅配置。
         *
         * ```json
         * {
         *    "intents": { ... }
         * }
         * ```
         *
         * 默认情况下订阅如下事件：
         * - [频道相关事件][EventIntents.Guilds]
         * - [频道成员相关事件][EventIntents.GuildMembers]
         * - [公域消息相关事件][EventIntents.PublicGuildMessages]
         *
         * @see IntentsConfig
         */
        @SerialName("intents")
        val intentsConfig: IntentsConfig = IntentsConfig.Raw(
            EventIntents.Guilds.intents
             + EventIntents.GuildMembers.intents
             + EventIntents.PublicGuildMessages.intents
        ),

        /**
         * 用作 [Signal.Identify.Data.properties] 中的参数。
         *
         * ```json
         * {
         *   "clientProperties": {
         *      "k1": "v1",
         *      "foo": "bar"
         *   }
         * }
         * ```
         *
         *
         */
        public val clientProperties: Map<String, String> = emptyMap(),

        ) {
        public companion object {
            internal const val SERVER_URL_SANDBOX_VALUE: String = "SANDBOX"
        }
    }


    internal fun includeConfig(cpConfiguration: QGBotComponentConfiguration) {
        cpConfiguration.botConfig {
            val configuration = this
            config?.apply {
                serverUrl?.also { su ->
                    if (su == Config.SERVER_URL_SANDBOX_VALUE) {
                        configuration.serverUrl = QQGuild.SANDBOX_URL
                    } else {
                        configuration.serverUrl = Url(su)
                    }
                }

                configuration.shard = shardConfig.shard
                configuration.intents = intentsConfig.intents

                // TODO?
            }
        }
    }
}
