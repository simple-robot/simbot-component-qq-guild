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

package love.forte.simbot.component.tencentguild.internal.event

import love.forte.simbot.ID
import love.forte.simbot.component.tencentguild.event.TcgChannelAtMessageEvent
import love.forte.simbot.component.tencentguild.internal.*
import love.forte.simbot.component.tencentguild.util.requestBy
import love.forte.simbot.event.Event
import love.forte.simbot.message.Message
import love.forte.simbot.message.MessageReceipt
import love.forte.simbot.tencentguild.EventSignals
import love.forte.simbot.tencentguild.api.message.MessageSendApi
import love.forte.simbot.tencentguild.model.Message

/**
 *
 * @author ForteScarlet
 */
internal class TcgChannelAtMessageEventImpl(
    override val sourceEventEntity: love.forte.simbot.tencentguild.model.Message,
    override val bot: TencentGuildComponentBotImpl,
    override val channelInternal: TencentChannelImpl,
) : TcgChannelAtMessageEvent() {
    override val id: ID = sourceEventEntity.id
    override suspend fun reply(message: Message): MessageReceipt {
        val (messageForSend, fileImage) = MessageParsers.parse(message)
        messageForSend.msgId = sourceEventEntity.id
        val cid = sourceEventEntity.channelId
        return MessageSendApi.create(cid, messageForSend, fileImage).requestBy(bot).asReceipt()
    }
    
    override suspend fun send(message: Message): MessageReceipt = reply(message)
    
    override val authorInternal: TencentMemberImpl =
        TencentMemberImpl(bot, sourceEventEntity.member, channelInternal.guildInternal)
    
    
    override val messageContent: TencentReceiveMessageContentImpl by lazy(LazyThreadSafetyMode.NONE) {
        TencentReceiveMessageContentImpl(sourceEventEntity)
    }
    
    
    internal object Parser : BaseSignalToEvent<love.forte.simbot.tencentguild.model.Message>() {
        override val key = Key
        override val type: EventSignals<love.forte.simbot.tencentguild.model.Message>
            get() = EventSignals.AtMessages.AtMessageCreate
        
        override suspend fun doParser(data: love.forte.simbot.tencentguild.model.Message, bot: TencentGuildComponentBotImpl): Event {
            val channel = bot.findOrCreateChannelImpl(data.guildId, data.channelId)
            return TcgChannelAtMessageEventImpl(data, bot, channel)
        }
        
    }
}



