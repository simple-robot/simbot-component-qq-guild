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
import love.forte.simbot.ID
import love.forte.simbot.Timestamp
import love.forte.simbot.component.tencentguild.*
import love.forte.simbot.component.tencentguild.event.TcgChannelAtMessageEvent
import love.forte.simbot.component.tencentguild.util.requestBy
import love.forte.simbot.event.EventProcessingContext
import love.forte.simbot.literal
import love.forte.simbot.message.Message
import love.forte.simbot.qguild.TencentChannelInfo
import love.forte.simbot.qguild.api.message.MessageSendApi
import love.forte.simbot.utils.item.Items

/**
 *
 * @author ForteScarlet
 */
internal class TencentChannelImpl internal constructor(
    private val baseBot: TencentGuildComponentBotImpl,
    @Volatile
    override var source: TencentChannelInfo,
    internal val guildInternal: TencentGuildImpl,
    override val category: TencentChannelCategoryImpl,
) : TencentChannel {
    override suspend fun guild(): TencentGuild = guildInternal
    
    override suspend fun owner(): TencentMember = guildInternal.owner()
    
    override val bot: TencentGuildComponentGuildBot get() = guildInternal.bot
    
    override suspend fun send(message: Message): TencentMessageReceipt {
        val currentCoroutineContext = currentCoroutineContext()
        
        
        val builder = MessageParsers.parse(message) {
                if (this.msgId == null) {
                    val currentEvent =
                        currentCoroutineContext[EventProcessingContext]?.event?.takeIf { it is TcgChannelAtMessageEvent } as? TcgChannelAtMessageEvent

                    val msgId = currentEvent?.sourceEventEntity?.id
                    if (msgId != null) {
                        this.msgId = msgId
                    }
                }
//            forSending {
//            }
        }

        return MessageSendApi.create(source.id.literal,builder.build()).requestBy(baseBot).asReceipt()

//        return MessageSendApi.create(source.id, builder, fileImage).requestBy(baseBot).asReceipt()
    }
    
    
    // TODO
    override val roles: Items<TencentRole>
        get() = guildInternal.roles
    
    override fun toString(): String {
        return "TencentChannelImpl(bot=$baseBot, source=$source, guild=$guildInternal)"
    }
    
    // region info impl
    @Suppress("DEPRECATION")
    override val createTime: Timestamp
        get() = source.createTime
    
    @Suppress("DEPRECATION")
    override val currentMember: Int
        get() = source.currentMember
    
    @Suppress("DEPRECATION")
    override val description: String
        get() = source.description
    override val guildId: ID
        get() = source.guildId
    
    override val icon: String
        get() = guildInternal.icon
    
    override val id: ID
        get() = source.id
    
    @Suppress("DEPRECATION")
    override val maximumMember: Int
        get() = source.maximumMember
    
    override val name: String
        get() = source.name
    
    override val ownerId: ID
        get() = source.ownerId
    // endregion
    
}
