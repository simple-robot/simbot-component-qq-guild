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
import love.forte.plugin.suspendtrans.annotation.JvmAsync
import love.forte.plugin.suspendtrans.annotation.JvmBlocking
import love.forte.simbot.ID
import love.forte.simbot.Timestamp
import love.forte.simbot.definition.Guild
import love.forte.simbot.definition.Organization
import love.forte.simbot.qguild.api.apipermission.ApiPermissions
import love.forte.simbot.qguild.api.member.GetGuildMemberListApi
import love.forte.simbot.utils.item.Items
import kotlin.time.Duration

/**
 * 一个QQ频道服务器.
 *
 * @author ForteScarlet
 */
public interface TencentGuild : Guild, CoroutineScope {
    override val bot: TencentGuildComponentGuildBot
    override val createTime: Timestamp
    override val currentMember: Int
    override val description: String
    override val icon: String
    override val id: ID
    override val maximumMember: Int
    override val name: String
    override val ownerId: ID

    public val source: love.forte.simbot.qguild.model.Guild

    /**
     * 当前bot在频道服务器中拥有的API权限集, 在 [TencentGUild] 被构建时初始化，
     * 并可能会周期性更新。
     */
    public val permissions: ApiPermissions

    /**
     * 得到此频道服务器下的所有子频道。
     */
    override val channels: Items<TencentChannel>
    
    
    /**
     * 得到此频道服务器下的所有子频道的列表副本。
     */
    public val channelList: List<TencentChannel>
    
    /**
     * 获取指定ID的子频道。
     */
    @JvmBlocking(baseName = "getChannel", suffix = "")
    @JvmAsync(baseName = "getChannel")
    override suspend fun channel(id: ID): TencentChannel?
    
    
    /**
     * 得到此频道服务器下的所有子频道。同 [channels]。
     *
     * @see channels
     */
    override val children: Items<TencentChannel> get() = channels
    
    
    /**
     * 获取指定ID的子频道。
     *
     * @see channel
     */
    @JvmBlocking(baseName = "getChild", suffix = "")
    @JvmAsync(baseName = "getChild")
    override suspend fun child(id: ID): TencentChannel? = channel(id)

    /**
     * 当前频道服务器的所属人。
     *
     * 也可理解为频道主、创建者等。
     */
    @JvmBlocking(asProperty = true, suffix = "")
    @JvmAsync(asProperty = true)
    override suspend fun owner(): TencentMember
    
    
    /**
     * 获取指定成员的信息。
     */
    @JvmBlocking(baseName = "getMember", suffix = "")
    @JvmAsync(baseName = "getMember")
    override suspend fun member(id: ID): TencentMember?
    
    /**
     * 频道所有成员列表。
     *
     * 只有当bot拥有使用 [获取成员列表][GetGuildMemberListApi] 的权限时
     * [members] 才会得到有效结果，否则将会始终返回 [空的Items][Items.emptyItems].
     *
     * 可以通过 [permissions] 手动检查是否存在 [GetGuildMemberListApi] 的权限。
     * 通常情况下此权限仅限私域类型的BOT。
     *
     */
    override val members: Items<TencentMember>


    /**
     * 当前频道中的角色。
     */
    override val roles: Items<TencentRole>
    
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
