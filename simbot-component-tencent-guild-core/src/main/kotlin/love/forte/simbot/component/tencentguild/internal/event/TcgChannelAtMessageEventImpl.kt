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



