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

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import love.forte.simbot.component.tencentguild.TencentChannel
import love.forte.simbot.component.tencentguild.TencentGuild
import love.forte.simbot.component.tencentguild.event.TcgChannelAtMessageEvent
import love.forte.simbot.core.event.coreListener
import love.forte.simbot.event.EventProcessingContext
import love.forte.simbot.message.At
import love.forte.simbot.message.Text
import love.forte.simbot.message.plus

/**
 *
 * @author ForteScarlet
 */
class ListenerTest {
}


val listener =
    coreListener(eventKey = TcgChannelAtMessageEvent) { context: EventProcessingContext, event: TcgChannelAtMessageEvent ->
        // 此消息事件的子频道
        val channel: TencentChannel = event.source()

        // 子频道的外部频道
        val guild: TencentGuild = channel.guild()

        // 这个频道的所有子频道
        val children: Flow<TencentChannel> = guild.children()

        children.collect {
            println("Channel: ${it.name}")
        }

        // 发送消息,
        // 目前支持:
        // Text
        // At
        // Ark(tencent guild 专属)
        // AttachmentMessage(tencent guild 专属)

        // 消息：@事件发送者 你好啊
        channel.send(At(event.id) + Text { "你好啊！" })

        null // 事件返回值，爱是啥是啥
    }