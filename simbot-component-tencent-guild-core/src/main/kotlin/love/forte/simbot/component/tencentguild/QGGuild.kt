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

package love.forte.simbot.component.tencentguild

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.sync.Mutex
import love.forte.plugin.suspendtrans.annotation.JvmAsync
import love.forte.plugin.suspendtrans.annotation.JvmBlocking
import love.forte.simbot.ID
import love.forte.simbot.Timestamp
import love.forte.simbot.definition.Guild
import love.forte.simbot.definition.Organization
import love.forte.simbot.qguild.TencentApiException
import love.forte.simbot.qguild.api.apipermission.ApiPermissions
import love.forte.simbot.qguild.api.channel.GetGuildChannelListApi
import love.forte.simbot.qguild.api.member.GetGuildMemberListApi
import love.forte.simbot.qguild.event.EventIntents
import love.forte.simbot.qguild.model.ChannelType
import love.forte.simbot.utils.item.Items
import kotlin.time.Duration
import love.forte.simbot.qguild.model.Guild as QGSourceGuild

/**
 * 一个QQ频道服务器.
 *
 * ### 内建缓存
 *
 * [QGGuild] 内部的部分信息支持在满足一定条件的情况下**内建缓存**。
 * 通过内建缓存可以有效降低API的调用频率，增加相关属性的获取速度，
 * 进而提高程序的整体效率。
 *
 * #### 成员缓存
 *
 * #### 子频道缓存
 *
 *
 *
 * @author ForteScarlet
 */
public interface QGGuild : Guild, CoroutineScope {
    override val bot: QGGuildBot
    override val createTime: Timestamp
    override val currentMember: Int
    override val description: String
    override val icon: String
    override val id: ID
    override val maximumMember: Int
    override val name: String
    override val ownerId: ID

    public val source: QGSourceGuild

    /**
     * 当前bot在频道服务器中拥有的API权限集, 在 [QGGuild] 被构建时初始化，
     * 并可能会周期性更新。
     *
     * 可以通过 [refreshPermissions] 手动刷新 [permissions] 信息。
     *
     * @see refreshPermissions
     *
     */
    public val permissions: ApiPermissions

    /**
     * 主动刷新 [permissions] 信息。
     *
     * 通过API查询当前bot在当前guild中的 [ApiPermissions]，并将信息更新到 [permissions]。
     *
     * 同一时刻只会有一个 [refreshPermissions] 工作，会通过 [Mutex] 控制并发。
     *
     * 如果 [evaluationInternalCache] 为 `true`，则在刷新 [permissions] 之后，
     * 重新评估各属性内建缓存的可能性。默认为 `false`。
     *
     * 如果内建缓存已经被支持或存在，则 [evaluationInternalCache] 不生效。
     *
     * @param evaluationInternalCache 是否重新评估各属性内建缓存的可能性。默认为 `false`
     *
     * @return 刷新结果
     */
    @JvmBlocking
    @JvmAsync
    public suspend fun refreshPermissions(evaluationInternalCache: Boolean): ApiPermissions

    /**
     * 主动刷新 [permissions] 信息。
     *
     * 通过API查询当前bot在当前guild中的 [ApiPermissions]，并将信息更新到 [permissions]。
     *
     * 同一时刻只会有一个 [refreshPermissions] 工作，会通过 [Mutex] 控制并发。
     *
     * @return 刷新结果
     */
    @JvmBlocking
    @JvmAsync
    public suspend fun refreshPermissions(): ApiPermissions = refreshPermissions(false)


    /**
     * 得到此频道服务器下的所有子频道。
     *
     * 如果当前bot在当前guild中没有 [获取频道列表][GetGuildChannelListApi] 的API权限，
     * 则 [channels] 将会始终得到[空的Items][Items.emptyItems].
     *
     * 如果拥有权限，但是当前bot未监听 [Guild相关事件][EventIntents.Guilds]
     * 则 [channels] 不会内建缓存，每次获取都会实时使用相关API。
     *
     * _可以通过 [permissions] 手动检查是否存在 [GetGuildChannelListApi] 的权限。_
     *
     * [channels] 的结果中不会出现 [子频道分类][QGChannelCategory]，毕竟它们是不同的类型。
     * 获取子频道分类相关内容，参考 [categories] 或 [category].
     *
     * @see channel
     * @see categories
     * @see category
     */
    override val channels: Items<QGChannel>

    /**
     * 获取指定ID的子频道。
     *
     * 如果当前bot没有 [获取频道列表][GetGuildChannelListApi] 的API权限，
     * 或者没有监听 [Guild相关事件][EventIntents.Guilds] (参考 [channels] 说明)，
     * 则 [channel] 会尝试直接通过API实时查询对应信息。
     *
     * @throws TencentApiException API请求失败，例如没有权限等
     */
    @JvmBlocking(baseName = "getChannel", suffix = "")
    @JvmAsync(baseName = "getChannel")
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
    @JvmBlocking(baseName = "getChild", suffix = "")
    @JvmAsync(baseName = "getChild")
    override suspend fun child(id: ID): QGChannel? = channel(id)

    /**
     * 得到当前频道服务器下的所有频道分类。
     *
     * 如果当前bot在当前guild中没有 [获取频道列表][GetGuildChannelListApi] 的API权限，
     * 则 [categories] 将会始终得到[空的Items][Items.emptyItems].
     *
     * 如果拥有权限，但是当前bot未监听 [Guild相关事件][EventIntents.Guilds]
     * 则 [categories] 不会内建缓存，每次获取都会实时使用相关API。
     *
     * _可以通过 [permissions] 手动检查是否存在 [GetGuildChannelListApi] 的权限。_
     *
     * @see channels
     */
    public val categories: Items<QGChannelCategory>

    /**
     * 获取指定ID的子频道分类。
     *
     * 如果当前bot在当前guild中没有 [获取频道列表][GetGuildChannelListApi] 的API权限，
     * 或者未监听 [Guild相关事件][EventIntents.Guilds]，则 [category] 会直接通过API实时查询。
     *
     * 更多参考 [categories] 有关内建缓存的说明。
     *
     * @throws TencentApiException API请求过程中产生的异常
     * @throws IllegalStateException 当目标子频道的类型不属于 [分组类型][ChannelType.CATEGORY] 时
     *
     */
    @JvmBlocking(baseName = "getCategory", suffix = "")
    @JvmAsync(baseName = "getCategory")
    public suspend fun category(id: ID): QGChannelCategory?

    /**
     * 当前频道服务器的所属人。
     *
     * 也可理解为频道主、创建者等。
     */
    @JvmBlocking(asProperty = true, suffix = "")
    @JvmAsync(asProperty = true)
    override suspend fun owner(): QGMember
    
    
    /**
     * 获取指定成员的信息。
     */
    @JvmBlocking(baseName = "getMember", suffix = "")
    @JvmAsync(baseName = "getMember")
    override suspend fun member(id: ID): QGMember?
    
    /**
     * 频道所有成员列表。
     *
     * 只有当bot拥有使用 [获取成员列表][GetGuildMemberListApi] 的权限时
     * [members] 才会得到有效结果，否则将会始终返回 [空的Items][Items.emptyItems].
     *
     * 如果拥有相关权限，但是当前bot未监听 [成员相关事件][EventIntents.GuildMembers]，
     * 则不会内建缓存，而是每次都实时调用相关API。
     *
     * _可以通过 [permissions] 手动检查是否存在 [GetGuildMemberListApi] 的权限。
     * 通常情况下此权限仅限私域类型的BOT。_
     *
     */
    override val members: Items<QGMember>

    /**
     * 当前频道中的角色。
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
