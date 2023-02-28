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
