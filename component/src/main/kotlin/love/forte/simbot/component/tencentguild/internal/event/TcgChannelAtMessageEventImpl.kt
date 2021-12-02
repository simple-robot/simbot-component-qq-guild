package love.forte.simbot.component.tencentguild.internal.event

import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.ID
import love.forte.simbot.Timestamp
import love.forte.simbot.component.tencentguild.event.TcgChannelAtMessageEvent
import love.forte.simbot.component.tencentguild.internal.TencentChannelImpl
import love.forte.simbot.component.tencentguild.internal.TencentGuildBotImpl
import love.forte.simbot.component.tencentguild.internal.TencentGuildImpl
import love.forte.simbot.component.tencentguild.internal.TencentMemberImpl
import love.forte.simbot.component.tencentguild.message.TencentReceiveMessageContent
import love.forte.simbot.event.Event
import love.forte.simbot.tencentguild.TencentMessage
import love.forte.simbot.tencentguild.api.channel.GetChannelApi
import love.forte.simbot.tencentguild.api.guild.GetGuildApi
import love.forte.simbot.tencentguild.request

/**
 *
 * @author ForteScarlet
 */
internal class TcgChannelAtMessageEventImpl(
    override val sourceMessage: TencentMessage,
    override val bot: TencentGuildBotImpl
) : TcgChannelAtMessageEvent() {

    private val _fromGuild: Deferred<TencentGuildImpl> = bot.async(start = CoroutineStart.LAZY) {
        val guildInfo = GetGuildApi(sourceMessage.guildId).request(bot)
        TencentGuildImpl(bot = bot, guildInfo)
    }

    private val _author: Deferred<TencentMemberImpl> = bot.async(start = CoroutineStart.LAZY) {
        TencentMemberImpl(
            bot = bot,
            info = sourceMessage.member,
            guild = _fromGuild.await()
        )
    }

    override val author: TencentMemberImpl by lazy { runBlocking { _author.await() } }

    private var _sourceChannel: Deferred<TencentChannelImpl> = bot.async(start = CoroutineStart.LAZY) {
        TencentChannelImpl(
            bot = bot,
            info = GetChannelApi(sourceMessage.channelId).request(bot),
            from = _fromGuild.await()
        )
    }

    override val source: TencentChannelImpl by lazy { runBlocking { _sourceChannel.await() } }

    override val timestamp: Timestamp
        get() = sourceMessage.timestamp

    /**
     * 暂时固定为 PUBLIC
     */
    override val visibleScope: Event.VisibleScope get() = Event.VisibleScope.PUBLIC

    override val messageContent: TencentReceiveMessageContent by lazy(LazyThreadSafetyMode.NONE) {
        TencentReceiveMessageContent(sourceMessage)
    }

    /** AT_MESSAGES 的事件meta的id就是消息的ID。 */
    override val metadata: Metadata = Metadata(sourceMessage.id)

    @SerialName("tcg.e.meta")
    @Serializable
    data class Metadata(override val id: ID) : Event.Metadata
}