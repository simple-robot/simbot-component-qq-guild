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

package love.forte.simbot.component.tencentguild

import love.forte.simbot.Api4J
import love.forte.simbot.ID
import love.forte.simbot.Timestamp
import love.forte.simbot.component.tencentguild.internal.TencentMessageReceipt
import love.forte.simbot.definition.Channel
import love.forte.simbot.message.Message
import love.forte.simbot.message.MessageContent
import love.forte.simbot.message.Text
import love.forte.simbot.tencentguild.ChannelSubType
import love.forte.simbot.tencentguild.ChannelType
import love.forte.simbot.tencentguild.TencentChannelInfo
import love.forte.simbot.utils.item.Items
import love.forte.simbot.utils.item.Items.Companion.emptyItems
import love.forte.simbot.utils.runInBlocking
import java.util.concurrent.TimeUnit
import kotlin.time.Duration

/**
 * 子频道。来自于一个 [TencentGuild]。
 *
 * @see TencentGuild
 * @author ForteScarlet
 */
public interface TencentChannel : Channel, TencentChannelInfo {
    
    
    override val bot: TencentGuildComponentGuildBot
    override val createTime: Timestamp
    override val currentMember: Int
    override val description: String
    override val guildId: ID
    override val icon: String
    override val id: ID
    override val maximumMember: Int
    override val name: String
    override val ownerId: ID
    
    /**
     * 子频道始终有分组。
     */
    override val category: TencentChannelCategory
    
    @get:JvmSynthetic
    override val channelType: ChannelType
    
    @get:JvmSynthetic
    override val channelSubType: ChannelSubType
    override val position: Int
    override val parentId: String
    
    
    @OptIn(Api4J::class)
    override val guild: TencentGuild
    
    
    @OptIn(Api4J::class)
    override val owner: TencentMember
    
    
    @OptIn(Api4J::class)
    override val previous: TencentGuild get() = guild
    
    @JvmSynthetic
    override suspend fun guild(): TencentGuild = guild
    
    @JvmSynthetic
    override suspend fun owner(): TencentMember = owner
    
    
    override val roles: Items<TencentRole>
    
    @JvmSynthetic
    override suspend fun previous(): TencentGuild = guild
    
    @JvmSynthetic
    override suspend fun send(message: Message): TencentMessageReceipt
    
    override suspend fun send(text: String): TencentMessageReceipt {
        return send(Text.of(text))
    }
    
    override suspend fun send(message: MessageContent): TencentMessageReceipt {
        return send(message)
    }
    
    @Api4J
    override fun sendBlocking(text: String): TencentMessageReceipt {
        return sendBlocking(text)
    }
    
    @Api4J
    override fun sendBlocking(message: Message): TencentMessageReceipt {
        return sendBlocking(message)
    }
    
    @Api4J
    override fun sendBlocking(message: MessageContent): TencentMessageReceipt {
        return sendBlocking(message)
    }
    
    
    /**
     * 目前无法直接获取成员列表。
     */
    @Deprecated(
        "Get member list is not supported",
        ReplaceWith("emptyItems()", "love.forte.simbot.utils.item.Items.Companion.emptyItems")
    )
    override val members: Items<TencentMember>
        get() = emptyItems() // previous.members // TODO
    
    @JvmSynthetic
    override suspend fun member(id: ID): TencentMember? {
        return previous().member(id)
    }
    
    
    @Deprecated("Mute API is not supported", ReplaceWith("false"))
    @JvmSynthetic
    override suspend fun mute(duration: Duration): Boolean = false
    
    @OptIn(Api4J::class)
    @Deprecated("Mute API is not supported", ReplaceWith("false"))
    override fun muteBlocking(time: Long, timeUnit: TimeUnit): Boolean = false
    
    @Deprecated("Mute API is not supported", ReplaceWith("false"))
    @JvmSynthetic
    override suspend fun unmute(): Boolean = false
    
    @OptIn(Api4J::class)
    @Deprecated("Mute API is not supported", ReplaceWith("false"))
    override fun unmuteBlocking(): Boolean = false
    
    
    @Api4J
    override fun getMember(id: ID): TencentMember? = runInBlocking { member(id) }
}