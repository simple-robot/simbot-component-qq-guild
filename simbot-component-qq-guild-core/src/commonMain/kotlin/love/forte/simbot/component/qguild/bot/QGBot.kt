/*
 * Copyright (c) 2021-2024. ForteScarlet.
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

package love.forte.simbot.component.qguild.bot

import io.ktor.client.*
import io.ktor.client.request.*
import love.forte.simbot.bot.Bot
import love.forte.simbot.bot.ContactRelation
import love.forte.simbot.bot.GroupRelation
import love.forte.simbot.common.id.ID
import love.forte.simbot.common.id.StringID.Companion.ID
import love.forte.simbot.component.qguild.QQGuildComponent
import love.forte.simbot.component.qguild.channel.QGTextChannel
import love.forte.simbot.component.qguild.guild.QGGuildRelation
import love.forte.simbot.component.qguild.message.QGMessageReceipt
import love.forte.simbot.message.Message
import love.forte.simbot.message.MessageContent
import love.forte.simbot.qguild.QQGuildApiException
import love.forte.simbot.qguild.api.MessageAuditedException
import love.forte.simbot.suspendrunner.ST
import love.forte.simbot.suspendrunner.STP
import kotlin.jvm.JvmSynthetic
import love.forte.simbot.qguild.model.User as QGSourceUser
import love.forte.simbot.qguild.stdlib.Bot as QGSourceBot

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
     * 在QQ频道标准库中的原始Bot类型。
     */
    public val source: QGSourceBot

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
     * 否则会导致 [IllegalStateException] 异常。
     *
     * @throws IllegalStateException bot没有通过 [start] 初始化用户信息.
     */
    public val userId: ID

    /**
     * bot的用户名
     *
     * 需要至少执行一次 [start] 来初始化，
     * 否则会导致 [IllegalStateException] 异常。
     *
     * @throws IllegalStateException bot没有通过 [start] 初始化用户信息.
     */
    override val name: String

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
     * 启动时会通过 [me] 查询当前bot的信息，并为 [isMe] 提供额外id的匹配支持。
     *
     */
    @JvmSynthetic
    override suspend fun start()

    /**
     * bot的头像
     */
    public val avatar: String

    //// Impl

    /**
     * QQ频道BOT没有'群'概念，始终得到 `null`。
     */
    override val groupRelation: GroupRelation?
        get() = null

    /**
     * QQ频道BOT不存在'联系人'相关操作，始终得到 `null`。
     */
    override val contactRelation: ContactRelation?
        get() = null

    /**
     * 对频道服务器、以及频道、分组等相关内容的操作。
     *
     */
    override val guildRelation: QGGuildRelation

    /**
     * 直接向目标子频道发送消息。
     *
     * 此频道需要为文字子频道，否则会产生异常，但是此异常不会由程序检测，
     * 而是通过API的错误响应 [QQGuildApiException] 体现。
     *
     * [sendTo] 相对于 [QGTextChannel.send] 而言更加“不可靠”
     * —— 因为它跳过了对频道服务器和对子频道类型的校验，失去了在消息中自动填充 `msgId` 等透明行为，并且直接使用ID也会存在一些细微的隐患。
     * 但是这可以有效规避当没有获取频道服务器或子频道信息权限时候可能导致的问题。
     *
     * @throws Exception see [HttpClient.request], 可能会抛出任何ktor请求过程中的异常。
     * @throws QQGuildApiException 请求异常，例如无权限
     * @throws MessageAuditedException 当响应状态为表示消息审核的 `304023`、`304024` 时
     *
     * @return 消息发送回执
     */
    @ST
    public suspend fun sendTo(channelId: ID, text: String): QGMessageReceipt

    /**
     * 直接向目标子频道发送消息。
     *
     * 此频道需要为文字子频道，否则会产生异常，但是此异常不会由程序检测，
     * 而是通过API的错误响应 [QQGuildApiException] 体现。
     *
     * [sendTo] 相对于 [QGTextChannel.send] 而言更加“不可靠”
     * —— 因为它跳过了对频道服务器和对子频道类型的校验，失去了在消息中自动填充 `msgId` 等透明行为，并且直接使用ID也会存在一些细微的隐患。
     * 但是这可以有效规避当没有获取频道服务器或子频道信息权限时候可能导致的问题。
     *
     * @throws Exception see [HttpClient.request], 可能会抛出任何ktor请求过程中的异常。
     * @throws QQGuildApiException 请求异常，例如无权限
     * @throws MessageAuditedException 当响应状态为表示消息审核的 `304023`、`304024` 时
     *
     * @return 消息发送回执
     */
    @ST
    public suspend fun sendTo(channelId: ID, message: Message): QGMessageReceipt

    /**
     * 直接向目标子频道发送消息。
     *
     * 此频道需要为文字子频道，否则会产生异常，但是此异常不会由程序检测，
     * 而是通过API的错误响应 [QQGuildApiException] 体现。
     *
     * [sendTo] 相对于 [QGTextChannel.send] 而言更加“不可靠”
     * —— 因为它跳过了对频道服务器和对子频道类型的校验，失去了在消息中自动填充 `msgId` 等透明行为，
     * 并且直接使用ID也会存在一些细微的隐患。
     * 但是这可以有效规避当没有获取频道服务器或子频道信息权限时候可能导致的问题。
     *
     * @throws Exception see [HttpClient.request], 可能会抛出任何ktor请求过程中的异常。
     * @throws QQGuildApiException 请求异常，例如无权限
     * @throws MessageAuditedException 当响应状态为表示消息审核的 `304023`、`304024` 时
     *
     * @return 消息发送回执
     */
    @ST
    public suspend fun sendTo(channelId: ID, message: MessageContent): QGMessageReceipt


    /**
     * 获取当前bot对应的用户信息。
     *
     * 如果 [withCache] 为 `true` 且当前bot内存在上一次查询的结果缓存，
     * 则直接得到缓存，否则通过API查询。
     *
     * 通过API查询的结果会刷新至当前的内部缓存。
     *
     * @return API得到的用户信息结果
     */
    @ST(blockingBaseName = "getMe", blockingSuffix = "", asyncBaseName = "getMe", reserveBaseName = "getMe")
    public suspend fun me(withCache: Boolean): QGSourceUser

    /**
     * 通过API实时查询当前bot对应的用户信息。
     *
     * 通过API查询的结果会刷新至当前的内部缓存。
     *
     * @return API得到的用户信息结果
     */
    @STP
    public suspend fun me(): QGSourceUser = me(false)
}
