/*
 * Copyright (c) 2023. ForteScarlet.
 *
 * This file is part of simbot-component-qq-guild.
 *
 * simbot-component-qq-guild is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * simbot-component-qq-guild is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with simbot-component-qq-guild. If not, see <https://www.gnu.org/licenses/>.
 */

package love.forte.simbot.component.qguild.config

import love.forte.simbot.qguild.Bot
import love.forte.simbot.qguild.BotConfiguration
import love.forte.simbot.qguild.ConfigurableBotConfiguration


/**
 *
 * 在 [BotConfiguration] 的基础上提供更多额外的、针对于组件实现的配置信息。
 *
 * @author ForteScarlet
 */
public class QGBotComponentConfiguration {
    /**
     * 得到 [QQGuild Bot][Bot] 使用的配置。
     *
     * 可直接覆盖，或通过 [`botConfig { ... }`][botConfig] 组合配置。
     *
     */
    public var botConfigure: ConfigurableBotConfiguration.() -> Unit = {}

    /**
     * 使用 [block] 与当前 [botConfigure] 组合。
     *
     */
    public fun botConfig(block: ConfigurableBotConfiguration.() -> Unit) {
        val bc = botConfigure
        botConfigure = {
            bc()
            block()
        }
    }
}
