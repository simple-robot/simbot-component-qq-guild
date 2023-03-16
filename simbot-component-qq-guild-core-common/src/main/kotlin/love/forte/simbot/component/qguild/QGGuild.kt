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
import love.forte.simbot.ID
import love.forte.simbot.Timestamp
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
     * 得到此频道服务器下的所有子频道。
     *
     * _可以通过 [permissions] 手动检查是否存在 [GetGuildChannelListApi] 的权限。_
     *
     * [channels] 的结果中不会出现 [子频道分类][QGChannelCategory]，毕竟它们是不同的类型。
     * 获取子频道分类相关内容，参考 [categories] 或 [category].
     *
     * @see channel
     * @see categories
     * @see category
     *
     * @throws QQGuildApiException 请求失败，例如没有权限
     */
    override val channels: Items<QGChannel>

    /**
     * 获取指定ID的子频道。
     *
     * @throws QQGuildApiException 请求失败，例如没有权限
     */
    @JST(blockingBaseName = "getChannel", blockingSuffix = "", asyncBaseName = "getChannel")
    override suspend fun channel(id: ID): QGChannel?

    /**
     * 得到此频道服务器下的所有子频道。同 [channels]。
     *
     * @see channels
     */
    override val children: Items<QGChannel> get() = channels

    /**
     * 获取指定ID的子频道，同 [channel]
     *
     * @see channel
     */
    @JST(blockingBaseName = "getChild", blockingSuffix = "", asyncBaseName = "getChild")
    override suspend fun child(id: ID): QGChannel? = channel(id)

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
    override val roles: Items<QGRole>

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
