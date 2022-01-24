/*
 *  Copyright (c) 2022-2022 ForteScarlet <ForteScarlet@163.com>
 *
 *  根据 GNU LESSER GENERAL PUBLIC LICENSE 3 获得许可；
 *  除非遵守许可，否则您不得使用此文件。
 *  您可以在以下网址获取许可证副本：
 *
 *       https://www.gnu.org/licenses/lgpl-3.0-standalone.html
 *
 *   有关许可证下的权限和限制的具体语言，请参见许可证。
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



val listener = coreListener(eventKey = TcgChannelAtMessageEvent) { context: EventProcessingContext, event: TcgChannelAtMessageEvent ->
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