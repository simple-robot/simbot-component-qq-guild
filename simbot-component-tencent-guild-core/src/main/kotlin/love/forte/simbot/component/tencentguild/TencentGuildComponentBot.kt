/*
 * Copyright (c) 2021-2023. ForteScarlet.
 *
 * This file is part of simbot-component-tencent-guild.
 *
 * simbot-component-tencent-guild is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * simbot-component-tencent-guild is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with simbot-component-tencent-guild. If not, see <https://www.gnu.org/licenses/>.
 */

package love.forte.simbot.component.tencentguild

import kotlinx.coroutines.isActive
import love.forte.plugin.suspendtrans.annotation.JvmAsync
import love.forte.plugin.suspendtrans.annotation.JvmBlocking
import love.forte.simbot.ID
import love.forte.simbot.action.UnsupportedActionException
import love.forte.simbot.bot.Bot
import love.forte.simbot.definition.Contact
import love.forte.simbot.definition.Group
import love.forte.simbot.definition.GuildBot
import love.forte.simbot.event.EventProcessor
import love.forte.simbot.message.Image
import love.forte.simbot.utils.item.Items
import love.forte.simbot.utils.item.Items.Companion.emptyItems
import love.forte.simbot.qguild.Bot as QGuildBot

/**
 * 一个tencent频道BOT的接口实例。
 * @author ForteScarlet
 */
public interface TencentGuildComponentBot : Bot {
    /**
     * QQ频道的 [组件][TencentGuildComponent] 对象实例。
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
    public val source: QGuildBot

    @Deprecated("Use 'source'", ReplaceWith("source"))
    public val sourceBot: love.forte.simbot.qguild.Bot get() = source

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
     * 启动当前bot, 并且初始化此bot的信息。
     *
     * 启动时会通过 [Bot.me()][QGuildBot.me] 查询当前bot的信息，并为 [isMe] 提供额外id的匹配支持。
     *
     */
    @JvmSynthetic
    override suspend fun start(): Boolean

    /**
     * bot的用户名
     */
    override val username: String

    /**
     * bot的头像
     */
    override val avatar: String
    
    /**
     * bot所属的bot管理器
     */
    override val manager: TencentGuildBotManager

    /**
     * bot所属的事件处理器。
     */
    override val eventProcessor: EventProcessor


    //// Impl

    @JvmBlocking
    @JvmAsync
    override suspend fun resolveImage(id: ID): Image<*> {

        // TODO fake remote image?
        throw UnsupportedActionException("resolveImage(ID) not support yet.")
    }


    override val isActive: Boolean
        get() = source.isActive

    /**
     * QQ频道BOT没有'群'概念
     */
    @Deprecated(
        "Group related APIs are not supported",
        ReplaceWith("emptyItems()", "love.forte.simbot.utils.item.Items.Companion.emptyItems")
    )
    override val groups: Items<Group>
        get() = emptyItems()

    /**
     *
     */
    override val guilds: Items<TencentGuild>

    /**
     *
     */
    @JvmBlocking(baseName = "getGuild", suffix = "")
    @JvmAsync(baseName = "getGuild")
    override suspend fun guild(id: ID): TencentGuild?

    /**
     * QQ频道BOT不存在'联系人'列表
     */
    @Deprecated(
        "Contact related APIs are not supported",
        ReplaceWith("emptyItems()", "love.forte.simbot.utils.item.Items.Companion.emptyItems")
    )
    override val contacts: Items<Contact>
        get() = emptyItems()

    @Deprecated("Contact related APIs are not supported", ReplaceWith("null"))
    @JvmSynthetic
    override suspend fun contact(id: ID): Contact? = null

    @Deprecated("Group related APIs are not supported", ReplaceWith("null"))
    @JvmSynthetic
    override suspend fun group(id: ID): Group? = null
}


/**
 * QQ频道组件中 [TencentGuildComponentBot] 对 [GuildBot] 的实现。
 *
 */
public interface TencentGuildComponentGuildBot : TencentGuildComponentBot, GuildBot {
    override suspend fun asMember(): TencentMember
    override val guilds: Items<TencentGuild>

    @JvmBlocking(baseName = "getGuild", suffix = "")
    @JvmAsync(baseName = "getGuild")
    override suspend fun guild(id: ID): TencentGuild?
}
