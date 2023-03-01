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

package love.forte.simbot.component.tencentguild.event

import love.forte.plugin.suspendtrans.annotation.JvmAsync
import love.forte.plugin.suspendtrans.annotation.JvmBlocking
import love.forte.simbot.Timestamp
import love.forte.simbot.action.ReplySupport
import love.forte.simbot.action.SendSupport
import love.forte.simbot.component.tencentguild.TencentChannel
import love.forte.simbot.component.tencentguild.TencentMember
import love.forte.simbot.event.BaseEventKey
import love.forte.simbot.event.ChannelMessageEvent
import love.forte.simbot.message.Message
import love.forte.simbot.message.MessageReceipt
import love.forte.simbot.message.ReceivedMessageContent
import love.forte.simbot.message.doSafeCast
import love.forte.simbot.tencentguild.EventSignals
import love.forte.simbot.tencentguild.model.Message

/**
 *
 * 频道（AT_MESSAGE_CREATE）消息事件.
 *
 *
 * @see EventSignals
 * @see ChannelMessageEvent
 * @see ReplySupport
 *
 * @author ForteScarlet
 */
public abstract class TcgChannelAtMessageEvent : TcgEvent<love.forte.simbot.tencentguild.model.Message>(), ChannelMessageEvent, ReplySupport,
    SendSupport {
    abstract override val sourceEventEntity: love.forte.simbot.tencentguild.model.Message
    override val eventSignal: EventSignals<love.forte.simbot.tencentguild.model.Message> get() = EventSignals.AtMessages.AtMessageCreate
    
    
    protected abstract val authorInternal: TencentMember
    protected abstract val channelInternal: TencentChannel
    
    /**
     * 此消息的发送者。
     */
    @JvmBlocking(asProperty = true, suffix = "")
    @JvmAsync(asProperty = true)
    override suspend fun author(): TencentMember = authorInternal
    
    
    /**
     * 此事件发生的频道。同 [channel].
     */
    @JvmBlocking(asProperty = true, suffix = "")
    @JvmAsync(asProperty = true)
    override suspend fun source(): TencentChannel = channelInternal
    
    
    /**
     * 此事件发生的频道。同 [source].
     */
    @JvmBlocking(asProperty = true, suffix = "")
    @JvmAsync(asProperty = true)
    override suspend fun channel(): TencentChannel = channelInternal
    
    
    /**
     * 事件发生时间。
     */
    override val timestamp: Timestamp get() = sourceEventEntity.timestamp
    
    /**
     * 接收到的消息。
     */
    abstract override val messageContent: ReceivedMessageContent
    
    /**
     * 此事件发生的频道。同 [channel].
     */
    @JvmBlocking(asProperty = true, suffix = "")
    @JvmAsync(asProperty = true)
    override suspend fun organization(): TencentChannel = channel()
    
    
    /**
     * 通过当前事件中的 `msgId` 回复此事件的发送者。
     */
    @JvmBlocking
    @JvmAsync
    abstract override suspend fun reply(message: Message): MessageReceipt // TODO update return type.
    
    
    override val key: Key get() = Key
    
    public companion object Key : BaseEventKey<TcgChannelAtMessageEvent>(
        "tcg.at_msg", setOf(ChannelMessageEvent.Key)
    ) {
        override fun safeCast(value: Any): TcgChannelAtMessageEvent? = doSafeCast(value)
    }
}
