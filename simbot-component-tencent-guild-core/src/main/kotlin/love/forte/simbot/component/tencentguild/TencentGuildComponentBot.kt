/*
 *  Copyright (c) 2021-2022 ForteScarlet <ForteScarlet@163.com>
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

import kotlinx.coroutines.isActive
import love.forte.simbot.Api4J
import love.forte.simbot.ID
import love.forte.simbot.action.UnsupportedActionException
import love.forte.simbot.bot.Bot
import love.forte.simbot.definition.Contact
import love.forte.simbot.definition.Group
import love.forte.simbot.definition.GuildBot
import love.forte.simbot.event.EventProcessor
import love.forte.simbot.message.Image
import love.forte.simbot.tencentguild.TencentGuildBot
import love.forte.simbot.utils.item.Items
import love.forte.simbot.utils.item.Items.Companion.emptyItems

/**
 * 一个tencent频道BOT的接口实例。
 * @author ForteScarlet
 */
public interface TencentGuildComponentBot : Bot {
    /**
     * 腾讯频道的 [组件][TencentGuildComponent] 对象实例。
     */
    override val component: TencentGuildComponent
    
    /**
     * 得到自己。
     */
    override val bot: TencentGuildComponentBot
        get() = this
    
    /**
     * 在 stdlib 模块下的原始Bot类型。
     */
    public val source: TencentGuildBot
    
    @Deprecated("Use 'source'", ReplaceWith("source"))
    public val sourceBot: TencentGuildBot get() = source
    
    override val id: ID
        get() = source.ticket.appId.ID
    
    /**
     * 腾讯机器人有两个可能的唯一标识：作为bot的clientId以及在系统中作为用户的 user id.
     *
     * 需要注意的是，作为普通用户时的id必须在 [start] 之后才能被检测。在 [start] 之前，将会只通过 [id] 进行检测。
     *
     */
    override fun isMe(id: ID): Boolean
    
    /**
     * 启动当前bot, 并且初始化此bot的信息。 启动时会通过 [TencentGuildBot.me] 查询当前bot的信息，并为 [isMe] 提供额外id的匹配支持。
     *
     */
    override suspend fun start(): Boolean
    
    /**
     * bot的用户名
     */
    override val username: String
        get() = source.botInfo.username
    
    /**
     * bot的头像
     */
    override val avatar: String
        get() = source.botInfo.avatar
    
    /**
     * bot所属的bot管理器
     */
    override val manager: TencentGuildBotManager
    
    /**
     * bot所属的事件处理器。
     */
    override val eventProcessor: EventProcessor
    
    
    //// Impl
    
    override suspend fun resolveImage(id: ID): Image<*> {
        
        // TODO fake remote image?
        throw UnsupportedActionException("resolveImage(ID)")
    }
    
    
    override val isActive: Boolean
        get() = source.isActive
    
    @Api4J
    override fun cancelBlocking(reason: Throwable?): Boolean {
        return source.cancelBlocking(reason)
    }
    
    @Api4J
    override fun startBlocking(): Boolean {
        return source.startBlocking()
    }
    
    @Deprecated(
        "Group related APIs are not supported",
        ReplaceWith("emptyItems()", "love.forte.simbot.utils.item.Items.Companion.emptyItems")
    )
    override val groups: Items<Group>
        get() = emptyItems()
    
    
    override val guilds: Items<TencentGuild>
    
    @Api4J
    override fun getGuild(id: ID): TencentGuild?
    
    @JvmSynthetic
    override suspend fun guild(id: ID): TencentGuild?
    
    
    @Deprecated(
        "Contact related APIs are not supported",
        ReplaceWith("emptyItems()", "love.forte.simbot.utils.item.Items.Companion.emptyItems")
    )
    override val contacts: Items<Contact>
        get() = emptyItems()
    
    @Deprecated("Contact related APIs are not supported", ReplaceWith("null"))
    
    @JvmSynthetic
    override suspend fun contact(id: ID): Contact? = null
    
    @Deprecated("Contact related APIs are not supported", ReplaceWith("null"))
    @OptIn(Api4J::class)
    override fun getContact(id: ID): Contact? = null
    
    @Deprecated("Group related APIs are not supported", ReplaceWith("null"))
    @JvmSynthetic
    override suspend fun group(id: ID): Group? = null
    
    @Deprecated("Group related APIs are not supported", ReplaceWith("null"))
    @OptIn(Api4J::class)
    override fun getGroup(id: ID): Group? = null
    
}


/**
 * 腾讯频道组件中 [TencentGuildComponentBot] 对 [GuildBot] 的实现。
 *
 */
public interface TencentGuildComponentGuildBot : TencentGuildComponentBot, GuildBot {
    
    override suspend fun asMember(): TencentMember
    
    @Api4J
    override fun toMember(): TencentMember
}