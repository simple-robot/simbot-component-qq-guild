package love.forte.simbot.component.tencentguild.internal.event

import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.ID
import love.forte.simbot.Timestamp
import love.forte.simbot.action.MessageReplyReceipt
import love.forte.simbot.component.tencentguild.TencentMember
import love.forte.simbot.component.tencentguild.event.TcgChannelAtMessageEvent
import love.forte.simbot.component.tencentguild.internal.*
import love.forte.simbot.event.Event
import love.forte.simbot.message.Message
import love.forte.simbot.tencentguild.EventSignals
import love.forte.simbot.tencentguild.TencentMessage
import love.forte.simbot.tencentguild.api.channel.GetChannelApi
import love.forte.simbot.tencentguild.api.guild.GetGuildApi
import love.forte.simbot.tencentguild.api.message.MessageSendApi
import love.forte.simbot.tencentguild.request

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

    private val _fromGuild: Deferred<TencentGuildImpl> = bot.async(start = CoroutineStart.LAZY) {
        val guildInfo = GetGuildApi(sourceEventEntity.guildId).request(bot)
        TencentGuildImpl(bot = bot, guildInfo)
    }

    override suspend fun author(): TencentMember = _author.await()

    private val _author: Deferred<TencentMemberImpl> = bot.async(start = CoroutineStart.LAZY) {
        TencentMemberImpl(
            bot = bot,
            info = sourceEventEntity.member,
            guild = _fromGuild.await()
        )
    }

    private var _sourceChannel: Deferred<TencentChannelImpl> = bot.async(start = CoroutineStart.LAZY) {
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



