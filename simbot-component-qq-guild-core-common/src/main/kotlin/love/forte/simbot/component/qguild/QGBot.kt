/*
 * Copyright (c) 2021-2023. ForteScarlet.
 *
 * This file is part of simbot-component-qq-guild.
 *
 * simbot-component-qq-guild is free software: you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * simbot-component-qq-guild is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with simbot-component-qq-guild.
 * If not, see <https://www.gnu.org/licenses/>.
 */

package love.forte.simbot.component.qguild

import kotlinx.coroutines.isActive
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
import love.forte.simbot.qguild.Bot as QGSourceBot
import love.forte.simbot.qguild.model.User as QGSourceUser

/**
 * 一个 [QQ频道Bot][QGSourceBot] 的 simbot组件实现接口，
 * 是一个具有功能的 QQ频道组件Bot。
 *
 * ### AppID 与 用户ID
 * [QGBot] 的 [id] 是用于连接服务器的 `appId` 而并非系统用户ID。
 * 如果希望获取bot的 **用户ID**, 至少执行过一次 [QGBot.start] 后通过 [userId] 获取，
 * 或者通过 [me] 实时查询。
 *
 * _Note: QGBot仅由内部实现，对外不稳定_
 *
 * @author ForteScarlet
 */
public interface QGBot : Bot {
    /**
     * QQ频道的 [组件][QQGuildComponent] 对象实例。
     */
    override val component: QQGuildComponent

    /**
     * 得到自己。
     */
    override val bot: QGBot
        get() = this

    /**
     * 在QQ频道标准库中的原始Bot类型。
     */
    public val source: love.forte.simbot.qguild.Bot

    /**
     * 当前bot的 **appId** 。
     *
     * 如果希望获取当前bot的**用户**ID，
     * 至少执行一次 [start] 后使用 [userId]，
     * 或者通过 []
     */
    override val id: ID
        get() = source.ticket.appId.ID

    /**
     * 得到当前bot的用户ID。
     *
     * [userId] 是当前bot的用户id，需要至少执行一次 [start] 来初始化，
     * 否则会导致 [UninitializedPropertyAccessException] 异常。
     *
     * @throws UninitializedPropertyAccessException bot没有通过 [start] 初始化用户信息.
     */
    public val userId: ID

    /**
     * bot的用户名
     *
     * 需要至少执行一次 [start] 来初始化，
     * 否则会导致 [UninitializedPropertyAccessException] 异常。
     *
     * @throws UninitializedPropertyAccessException bot没有通过 [start] 初始化用户信息.
     */
    override val username: String

    /**
     * QQ机器人有两个可能的唯一标识：作为bot的 [app id][id] 以及在系统中作为用户的 [user id][userId].
     *
     * 需要注意的是，作为普通用户时的id必须在 [start] 之后才能被检测。在 [start] 之前，将会只通过 [id] 进行检测。
     *
     */
    override fun isMe(id: ID): Boolean

    /**
     * 启动当前bot, 并且初始化此bot的信息。
     *
     * 启动时会通过 [Bot.me()][QGSourceBot.me] 查询当前bot的信息，并为 [isMe] 提供额外id的匹配支持。
     *
     */
    @JvmSynthetic
    override suspend fun start(): Boolean

    /**
     * bot的头像
     */
    override val avatar: String
    
    /**
     * bot所属的bot管理器
     */
    override val manager: BaseQQGuildBotManager

    /**
     * bot所属的事件处理器。
     */
    override val eventProcessor: EventProcessor


    //// Impl

    /**
     * 暂时无法直接解析“id”图片
     *
     * @throws UnsupportedActionException
     */
    @JST
    override suspend fun resolveImage(id: ID): Image<*> {

        // TODO fake remote image?
        throw UnsupportedActionException("resolveImage(ID) not support.")
    }

    override val isActive: Boolean
        get() = source.isActive

    /**
     * Deprecated: QQ频道BOT没有'群'概念
     */
    @Deprecated(
        "Group related APIs are not supported",
        ReplaceWith("emptyItems()", "love.forte.simbot.utils.item.Items.Companion.emptyItems")
    )
    override val groups: Items<Group>
        get() = emptyItems()

    /**
     * Deprecated: QQ频道BOT没有'群'概念
     */
    @Deprecated("Group related APIs are not supported", ReplaceWith("false"))
    override val isGroupsSupported: Boolean
        get() = false

    /**
     * Deprecated: QQ频道BOT没有'群'概念
     */
    @Deprecated("Group related APIs are not supported", ReplaceWith("0"))
    @JvmSynthetic
    override suspend fun groupCount(): Int = 0

    /**
     * 是否支持QQ频道相关API，如 [guilds]。始终得到 `true`。
     */
    override val isGuildsSupported: Boolean
        get() = true

    /**
     * 获取当前bot所在的频道服务器列表。
     */
    override val guilds: Items<QGGuild>

    /**
     * QQ频道数量。QQ频道不支持获取频道服务器数量，始终得到 `-1`。
     */
    @Deprecated("The guild count API is not supported.", ReplaceWith("-1"))
    @JvmSynthetic
    override suspend fun guildCount(): Int = -1

    /**
     * 根据ID尝试获取一个指定的guild。
     */
    @JST(blockingBaseName = "getGuild", blockingSuffix = "", asyncBaseName = "getGuild")
    override suspend fun guild(id: ID): QGGuild?

    /**
     * Deprecated: QQ频道BOT不存在'联系人'列表，始终得到 [emptyItems]。
     */
    @Deprecated(
        "Contact related APIs are not supported",
        ReplaceWith("emptyItems()", "love.forte.simbot.utils.item.Items.Companion.emptyItems")
    )
    override val contacts: Items<Contact>
        get() = emptyItems()


    /**
     * Deprecated: QQ频道BOT不存在'联系人'列表，始终得到 `false`。
     */
    @Deprecated("Contact related APIs are not supported", ReplaceWith("null"))
    override val isContactsSupported: Boolean
        get() = false

    /**
     * Deprecated: QQ频道BOT不存在'联系人'列表，始终得到 `0`。
     */
    @Deprecated("Contact related APIs are not supported", ReplaceWith("0"))
    override suspend fun contactCount(): Int = 0

    /**
     * Deprecated: QQ频道BOT不存在'联系人'列表，始终得到 `null`。
     */
    @Deprecated("Contact related APIs are not supported", ReplaceWith("null"))
    @JvmSynthetic
    override suspend fun contact(id: ID): Contact? = null

    /**
     * QQ频道BOT不存在'联系人'列表
     */
    @Deprecated("Group related APIs are not supported", ReplaceWith("null"))
    @JvmSynthetic
    override suspend fun group(id: ID): Group? = null

    /**
     * 通过API实时查询当前bot对应的用户信息。
     *
     * @return API得到的用户信息结果
     */
    @JSTP
    public suspend fun me(): QGSourceUser
}


/**
 * QQ频道组件中 [QGSourceBot] 对 [GuildBot] 的实现。
 *
 */
public interface QGGuildBot : QGBot, GuildBot {
    override suspend fun asMember(): QGMember

    /**
     * 获取频道服务器序列
     */
    override val guilds: Items<QGGuild>

    /**
     * 根据ID尝试获取一个指定的guild。
     */
    @JST(blockingBaseName = "getGuild", blockingSuffix = "", asyncBaseName = "getGuild")
    override suspend fun guild(id: ID): QGGuild?
}
