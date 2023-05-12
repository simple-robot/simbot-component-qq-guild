/*
 * Copyright (c) 2022-2023. ForteScarlet.
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

import kotlinx.coroutines.CoroutineScope
import love.forte.simbot.ExperimentalSimbotApi
import love.forte.simbot.ID
import love.forte.simbot.Timestamp
import love.forte.simbot.component.qguild.role.QGGuildRole
import love.forte.simbot.component.qguild.role.QGRoleCreator
import love.forte.simbot.definition.Guild
import love.forte.simbot.definition.Organization
import love.forte.simbot.qguild.QQGuildApiException
import love.forte.simbot.qguild.api.apipermission.ApiPermissions
import love.forte.simbot.qguild.api.channel.GetGuildChannelListApi
import love.forte.simbot.qguild.api.member.GetGuildMemberListApi
import love.forte.simbot.qguild.model.ChannelType
import love.forte.simbot.utils.item.Items
import kotlin.time.Duration
import love.forte.simbot.qguild.model.Guild as QGSourceGuild

/**
 * 一个QQ频道服务器.
 *
 * @author ForteScarlet
 */
public interface QGGuild : Guild, CoroutineScope, QGObjectiveContainer<QGSourceGuild> {
    /**
     * 当前bot在此频道服务器中的频道bot实例。
     */
    override val bot: QGGuildBot

    /**
     * 频道ID
     */
    override val id: ID

    /**
     * 频道名称
     */
    override val name: String

    /**
     * 频道主ID
     */
    override val ownerId: ID

    /**
     * 无法获取频道创建时间，始终得到 [Timestamp.notSupport]
     *
     * 如果希望获取bot加入时间，使用 [joinTime]
     */
    override val createTime: Timestamp get() = Timestamp.notSupport()

    /**
     * bot加入此频道的时间。
     */
    public val joinTime: Timestamp

    /**
     * 频道描述
     */
    override val description: String

    /**
     * 频道图标。
     */
    override val icon: String

    /**
     * 最大成员数量
     */
    override val maximumMember: Int

    /**
     * 当前成员数量
     */
    override val currentMember: Int

    /**
     * 无法无副作用的得到当前频道数量，始终得到 `-1`。
     *
     * 如果希望计算频道数量，使用 [channels] 和 [categories]。
     */
    override val currentChannel: Int get() = -1

    /**
     * 无法得到子频道上限，可能也没有上限。始终得到 `-1`
     */
    override val maximumChannel: Int get() = -1

    /**
     * 原始的频道信息实例
     */
    override val source: QGSourceGuild

    /**
     * 查询并获取当前bot在频道服务器中拥有的API权限集。
     */
    @JSTP
    public suspend fun permissions(): ApiPermissions

    /**
     * 得到此频道中的所有子频道。
     *
     * _可以通过 [permissions] 手动检查是否存在 [GetGuildChannelListApi] 的权限。_
     *
     * 如果希望获取用于发送消息的文字子频道可参考 [channels]。[QGChannel] 可能的类型参考其文档说明。
     *
     * @see QGChannel
     * @see channels
     *
     */
    public val sourceChannels: Items<QGChannel>

    /**
     * 根据ID获取指定的子频道。
     *
     * @see QGChannel
     */
    @JST(blockingBaseName = "getSourceChannel", blockingSuffix = "", asyncBaseName = "getSourceChannel")
    public suspend fun sourceChannel(id: ID): QGChannel?

    // TODO QGChanel 是否实现 Channel ，然后不支持的调用send的时候报错？

    /**
     * 得到此频道服务器下的所有**文字子频道**。
     *
     * _可以通过 [permissions] 手动检查是否存在 [GetGuildChannelListApi] 的权限。_
     *
     * [channels] 的结果仅代表**文字子频道**类型。如果希望获取全部的子频道信息，请使用 [sourceChannels];
     * 如果希望获取 _子频道分类_ ，参考 [categories]。
     *
     * @see QGTextChannel
     * @see channel
     * @see categories
     * @see category
     *
     * @throws QQGuildApiException 请求失败，例如没有权限
     */
    override val channels: Items<QGTextChannel>

    /**
     * 获取指定ID的**文字子频道**。
     *
     * @throws QQGuildApiException 请求失败，例如没有权限
     */
    @JST(blockingBaseName = "getChannel", blockingSuffix = "", asyncBaseName = "getChannel")
    override suspend fun channel(id: ID): QGTextChannel?

    /**
     * 得到此频道服务器下的所有**文字子频道**。同 [channels]。
     *
     * @see channels
     */
    override val children: Items<QGTextChannel> get() = channels

    /**
     * 获取指定ID的**文字子频道**，同 [channel]
     *
     * @see channel
     */
    @JST(blockingBaseName = "getChild", blockingSuffix = "", asyncBaseName = "getChild")
    override suspend fun child(id: ID): QGTextChannel? = channel(id)

    /**
     * 得到当前频道服务器下的所有频道分类。
     *
     * @throws QQGuildApiException 请求失败，例如没有权限
     */
    public val categories: Items<QGChannelCategory>

    /**
     * 获取指定ID的子频道分类。
     *
     * @throws QQGuildApiException 请求失败，例如没有权限
     * @throws IllegalStateException 当目标子频道的类型不属于 [分组类型][ChannelType.CATEGORY] 时
     *
     */
    @JST(blockingBaseName = "getCategory", blockingSuffix = "", asyncBaseName = "getCategory")
    public suspend fun category(id: ID): QGChannelCategory?

    /**
     * 当前频道服务器的所属人。
     *
     * 也可理解为频道主、创建者等。
     *
     * @throws QQGuildApiException 当没有权限获取时，通常 [QQGuildApiException.value] == `401`
     * @throws NoSuchElementException 当没有找到对应成员时
     *
     */
    @JSTP
    override suspend fun owner(): QGMember


    /**
     * 获取指定成员的信息。
     *
     * @throws QQGuildApiException 请求失败，例如没有权限
     */
    @JST(blockingBaseName = "getMember", blockingSuffix = "", asyncBaseName = "getMember")
    override suspend fun member(id: ID): QGMember?

    /**
     * 频道所有成员列表。
     *
     * _可以通过 [permissions] 手动检查是否存在 [GetGuildMemberListApi] 的权限。
     * 通常情况下此权限仅限私域类型的BOT。_
     *
     * @throws QQGuildApiException 请求失败，例如没有权限
     */
    override val members: Items<QGMember>

    /**
     * 当前频道中的角色。
     *
     * @throws QQGuildApiException 请求失败，例如没有权限
     */
    @ExperimentalSimbotApi
    override val roles: Items<QGGuildRole>

    /**
     * 得到一个角色构造器。
     */
    @ExperimentalSimbotApi
    public fun roleCreator(): QGRoleCreator

    //// Impls

    /**
     * 频道服务器没有全频道禁言/取消禁言
     */
    @JvmSynthetic
    override suspend fun unmute(): Boolean = false

    /**
     * 频道服务器没有全频道禁言/取消禁言
     */
    @JvmSynthetic
    override suspend fun mute(duration: Duration): Boolean = false

    /**
     * 频道服务器没有上级概念。
     */
    @JvmSynthetic
    override suspend fun previous(): Organization? = null
}

/**
 * 构造一个 [QGRole]。
 *
 * @see QGRoleCreator
 */
@ExperimentalSimbotApi
public suspend inline fun QGGuild.createRole(block: QGRoleCreator.() -> Unit): QGGuildRole =
    roleCreator().also(block).create()
