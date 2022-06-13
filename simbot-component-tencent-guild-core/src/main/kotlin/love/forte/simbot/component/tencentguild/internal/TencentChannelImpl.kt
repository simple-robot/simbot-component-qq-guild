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

package love.forte.simbot.component.tencentguild.internal

import kotlinx.coroutines.currentCoroutineContext
import love.forte.simbot.ID
import love.forte.simbot.Timestamp
import love.forte.simbot.component.tencentguild.TencentChannel
import love.forte.simbot.component.tencentguild.TencentGuildComponentGuildBot
import love.forte.simbot.component.tencentguild.TencentMember
import love.forte.simbot.component.tencentguild.TencentRole
import love.forte.simbot.component.tencentguild.event.TcgChannelAtMessageEvent
import love.forte.simbot.component.tencentguild.util.requestBy
import love.forte.simbot.event.EventProcessingContext
import love.forte.simbot.message.Message
import love.forte.simbot.tencentguild.TencentChannelInfo
import love.forte.simbot.tencentguild.api.message.MessageSendApi
import love.forte.simbot.utils.item.Items

/**
 *
 * @author ForteScarlet
 */
internal class TencentChannelImpl internal constructor(
    private val baseBot: TencentGuildComponentBotImpl,
    internal var channel: TencentChannelInfo,
    override val guild: TencentGuildImpl,
) : TencentChannel {
    
    override val bot: TencentGuildComponentGuildBot get() = guild.bot
    
    override suspend fun send(message: Message): TencentMessageReceipt {
        val currentEvent =
            currentCoroutineContext()[EventProcessingContext]?.event?.takeIf { it is TcgChannelAtMessageEvent } as? TcgChannelAtMessageEvent
        
        val msgId = currentEvent?.sourceEventEntity?.id
        
        val messageForSend = MessageParsers.parse(message) {
            if (msgId != null) {
                this.msgId = msgId
            }
        }
        return MessageSendApi(channel.id, messageForSend).requestBy(baseBot).asReceipt()
    }
    
    override val owner: TencentMember
        get() = guild.owner
    
    
    // TODO
    override val roles: Items<TencentRole>
        get() = guild.roles
    
    
    // region info impl
    @Suppress("DEPRECATION")
    override val createTime: Timestamp
        get() = channel.createTime
    
    @Suppress("DEPRECATION")
    override val currentMember: Int
        get() = channel.currentMember
    
    @Suppress("DEPRECATION")
    override val description: String
        get() = channel.description
    override val guildId: ID
        get() = channel.guildId
    
    override val icon: String
        get() = guild.icon
    
    override val id: ID
        get() = channel.id
    
    @Suppress("DEPRECATION")
    override val maximumMember: Int
        get() = channel.maximumMember
    
    @Suppress("DEPRECATION")
    override val name: String
        get() = channel.name
    override val ownerId: ID
        get() = channel.ownerId
    override val channelTypeValue: Int
        get() = channel.channelTypeValue
    override val channelSubTypeValue: Int
        get() = channel.channelSubTypeValue
    override val position: Int
        get() = channel.position
    override val parentId: String
        get() = channel.parentId
    // endregion
    
}