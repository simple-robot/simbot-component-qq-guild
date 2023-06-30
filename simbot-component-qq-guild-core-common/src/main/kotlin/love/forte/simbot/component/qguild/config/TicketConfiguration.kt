/*
 * Copyright (c) 2023. ForteScarlet.
 *
 * This file is part of simbot-component-qq-guild.
 *
 * simbot-component-qq-guild is free software: you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * simbot-component-qq-guild is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with simbot-component-qq-guild.
 * If not, see <https://www.gnu.org/licenses/>.
 */

package love.forte.simbot.component.qguild.config

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule


/**
 *
 * 用于配置 [QGBotFileConfiguration.Ticket] 信息的配置属性，
 * 提供多种可选的配置方案。
 *
 * ```json
 * {
 *   "ticket": {
 *     "type": "...",
 *     "prop1": "v1"
 *   }
 * }
 * ```
 *
 * @author ForteScarlet
 */
@UsedOnlyForConfigSerialization
@Serializable
public sealed class TicketConfiguration {

    /**
     * 得到 [QGBotFileConfiguration.Ticket] 结果
     */
    public abstract fun toTicket(): QGBotFileConfiguration.Ticket


    /**
     * 与 [QGBotFileConfiguration.Ticket] 一致的配置类型，
     * 也是 [TicketConfiguration] 的默认方案。
     *
     * ```json
     * {
     *   "ticket": {
     *     "type": "simple",
     *     "appId": "appId-value",
     *     "secret": "secret-value",
     *     "token": "token-value"
     *   }
     * }
     * ```
     *
     * 或
     *
     * ```json
     *      * {
     *      *   "ticket": {
     *      *     "appId": "appId-value",
     *      *     "secret": "secret-value",
     *      *     "token": "token-value"
     *      *   }
     *      * }
     *      * ```
     *
     */
    @Serializable
    @SerialName(Simple.TYPE)
    public data class Simple(
        /**
         * @see QGBotFileConfiguration.Ticket
         */
        val appId: String,

        /**
         * @see QGBotFileConfiguration.Ticket
         */
        val secret: String,

        /**
         * @see QGBotFileConfiguration.Ticket
         */
        val token: String
    ) : TicketConfiguration() {

        override fun toTicket(): QGBotFileConfiguration.Ticket =
            QGBotFileConfiguration.Ticket(appId, secret, token)

        public companion object {
            public const val TYPE: String = "simple"
        }
    }

    // TODO

    public companion object {
        internal val serializersModule = SerializersModule {
            polymorphicDefaultDeserializer(TicketConfiguration::class) { Simple.serializer() }
        }
    }
}
