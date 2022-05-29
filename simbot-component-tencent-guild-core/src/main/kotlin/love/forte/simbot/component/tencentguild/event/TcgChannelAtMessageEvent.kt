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

import kotlinx.coroutines.runBlocking
import love.forte.simbot.Api4J
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
public abstract class TcgChannelAtMessageEvent : TcgEvent<TencentMessage>(), ChannelMessageEvent, ReplySupport,
    SendSupport {
    abstract override val sourceEventEntity: TencentMessage
    override val eventSignal: EventSignals<TencentMessage> get() = EventSignals.AtMessages.AtMessageCreate

    /**
     * 此消息的发送者。
     */
    @JvmSynthetic
    abstract override suspend fun author(): TencentMember

    /**
     * 此消息的发送者。
     */
    @Api4J
    override val author: TencentMember
        get() = runBlocking { author() }

    /**
     * 此事件发生的频道。同 [channel].
     */
    @JvmSynthetic
    abstract override suspend fun source(): TencentChannel

    /**
     * 此事件发生的频道。同 [channel].
     */
    @Api4J
    override val source: TencentChannel
        get() = runBlocking { source() }


    /**
     * 此事件发生的频道。同 [source].
     */
    @JvmSynthetic
    abstract override suspend fun channel(): TencentChannel

    /**
     * 此事件发生的频道。同 [source].
     */
    @Api4J
    override val channel: TencentChannel
        get() = runBlocking { channel() }

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
    @JvmSynthetic
    override suspend fun organization(): TencentChannel = channel()

    /**
     * 此事件发生的频道。同 [channel].
     */
    @Api4J
    override val organization: TencentChannel
        get() = runBlocking { organization() }



    /**
     * 通过当前事件中的 `msgId` 回复此事件的发送者。
     */
    @JvmSynthetic
    abstract override suspend fun reply(message: Message): MessageReceipt // TODO update return type.


    /**
     * 暂不支持撤回他人消息，始终返回false。
     */
    @JvmSynthetic
    override suspend fun delete(): Boolean = false // not support, maybe.

    override val key: Key get() = Key

    public companion object Key : BaseEventKey<TcgChannelAtMessageEvent>(
        "tcg.at_msg",
        setOf(ChannelMessageEvent.Key)
    ) {
        override fun safeCast(value: Any): TcgChannelAtMessageEvent? = doSafeCast(value)
    }
}