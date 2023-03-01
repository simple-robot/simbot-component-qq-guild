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

package love.forte.simbot.component.tencentguild.internal

import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import love.forte.simbot.ID
import love.forte.simbot.Timestamp
import love.forte.simbot.component.tencentguild.TencentGuild
import love.forte.simbot.component.tencentguild.TencentMember
import love.forte.simbot.component.tencentguild.TencentRole
import love.forte.simbot.component.tencentguild.event.TcgChannelAtMessageEvent
import love.forte.simbot.component.tencentguild.util.requestBy
import love.forte.simbot.event.EventProcessingContext
import love.forte.simbot.literal
import love.forte.simbot.message.Message
import love.forte.simbot.message.MessageContent
import love.forte.simbot.tencentguild.DirectMessageSession
import love.forte.simbot.tencentguild.TencentMemberInfo
import love.forte.simbot.tencentguild.api.message.direct.CreateDmsApi
import love.forte.simbot.tencentguild.api.message.direct.DmsSendApi
import love.forte.simbot.utils.item.Items
import love.forte.simbot.utils.item.effectedItemsByFlow

/**
 *
 * @author ForteScarlet
 */
internal class TencentMemberImpl(
    override val bot: TencentGuildComponentBotImpl,
    override val source: TencentMemberInfo,
    private val _guild: TencentGuildImpl,
) : TencentMember {
    override val joinTime: Timestamp
        get() = source.joinTime
    override val nickname: String
        get() = source.nickname
    override val avatar: String
        get() = source.avatar
    override val id: ID
        get() = source.id
    override val username: String
        get() = source.username
    
    override suspend fun guild(): TencentGuild = _guild
    
    private val roleIdSet = source.roleIds.mapTo(mutableSetOf()) { it.literal }
    private lateinit var dms: DirectMessageSession
    private val dmsInitLock = Mutex()
    
    private suspend fun getDms(): DirectMessageSession {
        if (::dms.isInitialized) {
            return dms
        } else {
            dmsInitLock.withLock {
                if (::dms.isInitialized) {
                    return dms
                }
                return CreateDmsApi.create(id, _guild.id).requestBy(bot).also {
                    dms = it
                }
            }
        }
    }
    
    override val roles: Items<TencentRole>
        get() {
            
            return bot.effectedItemsByFlow {
                _guild.roles.asFlow().filter { it.id.literal in roleIdSet }
            }
            
        }
    
    
    @JvmSynthetic
    override suspend fun send(message: Message): TencentMessageReceipt {
        val dms = getDms()
        val currentCoroutineContext = currentCoroutineContext()
        
        val (messageForSend, fileImage) = MessageParsers.parse(message) {
            forSending {
                if (this.msgId == null) {
                    val currentEvent =
                        currentCoroutineContext[EventProcessingContext]?.event?.takeIf { it is TcgChannelAtMessageEvent } as? TcgChannelAtMessageEvent
                    
                    val msgId = currentEvent?.sourceEventEntity?.id
                    if (msgId != null) {
                        this.msgId = msgId
                    }
                }
            }
        }
        
        
        return DmsSendApi.create(dms.guildId, messageForSend, fileImage).requestBy(bot).asReceipt()
    }
    
    override suspend fun send(text: String): TencentMessageReceipt {
        val dms = getDms()
        val currentEvent =
            currentCoroutineContext()[EventProcessingContext]?.event?.takeIf { it is TcgChannelAtMessageEvent } as? TcgChannelAtMessageEvent
        val msgId = currentEvent?.sourceEventEntity?.id
        
        return DmsSendApi.create(guildId = dms.guildId, content = text, msgId = msgId).requestBy(bot).asReceipt()
    }
    
    
    override suspend fun send(message: MessageContent): TencentMessageReceipt {
        return send(message.messages)
    }
    
    override fun toString(): String {
        return "TencentMemberImpl(bot=$bot, source=$source, guild=$_guild)"
    }
}

