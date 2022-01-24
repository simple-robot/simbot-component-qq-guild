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

package love.forte.simbot.component.tencentguild.event

import kotlinx.coroutines.runBlocking
import love.forte.simbot.Api4J
import love.forte.simbot.Timestamp
import love.forte.simbot.action.MessageReplyReceipt
import love.forte.simbot.action.ReplySupport
import love.forte.simbot.action.SendSupport
import love.forte.simbot.component.tencentguild.TencentChannel
import love.forte.simbot.component.tencentguild.TencentGuildBot
import love.forte.simbot.component.tencentguild.TencentMember
import love.forte.simbot.event.BaseEventKey
import love.forte.simbot.event.ChannelMessageEvent
import love.forte.simbot.event.Event
import love.forte.simbot.message.Message
import love.forte.simbot.message.ReceivedMessageContent
import love.forte.simbot.message.doSafeCast
import love.forte.simbot.tencentguild.EventSignals
import love.forte.simbot.tencentguild.TencentMessage

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
public abstract class TcgChannelAtMessageEvent : TcgEvent<TencentMessage>(), ChannelMessageEvent, ReplySupport, SendSupport {
    abstract override val sourceEventEntity: TencentMessage
    override val eventSignal: EventSignals<TencentMessage> get() = EventSignals.AtMessages.AtMessageCreate
    abstract override suspend fun author(): TencentMember
    abstract override suspend fun source(): TencentChannel
    abstract override suspend fun channel(): TencentChannel
    abstract override val timestamp: Timestamp
    abstract override val visibleScope: Event.VisibleScope
    abstract override val bot: TencentGuildBot
    abstract override val messageContent: ReceivedMessageContent
    abstract override val metadata: Event.Metadata

    //// impl

    override suspend fun organization(): TencentChannel = channel()

    @Api4J
    override val author: TencentMember
        get() = runBlocking { author() }

    @Api4J
    override val channel: TencentChannel
        get() = runBlocking { channel() }

    @Api4J
    override val source: TencentChannel
        get() = runBlocking { source() }

    @Api4J
    override val organization: TencentChannel
        get() = runBlocking { organization() }


    /**
     * Tcg支持消息回复。
     */
    abstract override suspend fun reply(message: Message): MessageReplyReceipt

    /**
     * Tcg暂不支持撤回他人消息。
     */
    override suspend fun delete(): Boolean = false // not support, maybe.

    override val key: Key get() = Key

    public companion object Key : BaseEventKey<TcgChannelAtMessageEvent>(
        "tcg.at_msg",
        setOf(ChannelMessageEvent.Key)
    ) {
        override fun safeCast(value: Any): TcgChannelAtMessageEvent? = doSafeCast(value)
    }
}