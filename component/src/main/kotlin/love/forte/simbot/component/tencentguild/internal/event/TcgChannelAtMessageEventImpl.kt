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

package love.forte.simbot.component.tencentguild.internal.event

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.ID
import love.forte.simbot.Timestamp
import love.forte.simbot.action.MessageReplyReceipt
import love.forte.simbot.component.tencentguild.TencentMember
import love.forte.simbot.component.tencentguild.event.TcgChannelAtMessageEvent
import love.forte.simbot.component.tencentguild.internal.*
import love.forte.simbot.component.tencentguild.util.request
import love.forte.simbot.event.Event
import love.forte.simbot.message.Message
import love.forte.simbot.message.MessageReceipt
import love.forte.simbot.tencentguild.EventSignals
import love.forte.simbot.tencentguild.TencentMessage
import love.forte.simbot.tencentguild.api.channel.GetChannelApi
import love.forte.simbot.tencentguild.api.guild.GetGuildApi
import love.forte.simbot.tencentguild.api.message.MessageSendApi
import love.forte.simbot.utils.LazyValue
import love.forte.simbot.utils.lazyValue

/**
 *
 * @author ForteScarlet
 */
internal class TcgChannelAtMessageEventImpl(
    override val sourceEventEntity: TencentMessage,
    override val bot: TencentGuildBotImpl
) : TcgChannelAtMessageEvent() {


    override suspend fun reply(message: Message): MessageReplyReceipt {
        val messageForSend = MessageParsers.parse(message)
        messageForSend.msgId = sourceEventEntity.id
        val cid = sourceEventEntity.channelId
        return MessageSendApi(cid, messageForSend).request(bot).asReplyReceipt()
    }

    override suspend fun send(message: Message): MessageReceipt = reply(message)

    private val _fromGuild: LazyValue<TencentGuildImpl> = bot.lazyValue {
        val guildInfo = GetGuildApi(sourceEventEntity.guildId).request(bot)
        TencentGuildImpl(bot = bot, guildInfo)
    }

    override suspend fun author(): TencentMember = _author.await()

    private val _author: LazyValue<TencentMemberImpl> = bot.lazyValue {
        TencentMemberImpl(
            bot = bot,
            info = sourceEventEntity.member,
            guild = _fromGuild.await()
        )
    }

    private var _sourceChannel: LazyValue<TencentChannelImpl> = bot.lazyValue {
        TencentChannelImpl(
            bot = bot,
            info = GetChannelApi(sourceEventEntity.channelId).request(bot),
            from = _fromGuild.await()
        )
    }

    override suspend fun source(): TencentChannelImpl = _sourceChannel.await()
    override suspend fun channel(): TencentChannelImpl = _sourceChannel.await()

    override val timestamp: Timestamp
        get() = sourceEventEntity.timestamp

    /**
     * 暂时固定为 PUBLIC
     */
    override val visibleScope: Event.VisibleScope get() = Event.VisibleScope.PUBLIC

    override val messageContent: TencentReceiveMessageContentImpl by lazy(LazyThreadSafetyMode.NONE) {
        TencentReceiveMessageContentImpl(sourceEventEntity)
    }

    /** AT_MESSAGES 的事件meta的id就是消息的ID。 */
    override val metadata: Metadata = Metadata(sourceEventEntity.id)

    @SerialName("tcg.e.meta")
    @Serializable
    data class Metadata(override val id: ID) : Event.Metadata


    internal object Parser : BaseSignalToEvent<TencentMessage>() {
        override val key = Key
        override val type: EventSignals<TencentMessage>
            get() = EventSignals.AtMessages.AtMessageCreate

        override suspend fun doParser(data: TencentMessage, bot: TencentGuildBotImpl): Event {
            return TcgChannelAtMessageEventImpl(data, bot)
        }

    }
}



