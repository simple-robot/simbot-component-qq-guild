/*
 * Copyright (c) 2023. ForteScarlet.
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

package love.forte.simbot.component.qguild.role

import love.forte.simbot.ExperimentalSimbotApi
import love.forte.simbot.ID
import love.forte.simbot.action.DeleteSupport
import love.forte.simbot.component.qguild.JST
import love.forte.simbot.component.qguild.QGMember
import love.forte.simbot.component.qguild.QGRole
import love.forte.simbot.definition.GuildMember
import love.forte.simbot.qguild.QQGuildApiException
import love.forte.simbot.utils.item.Items

/**
 * 一个[角色][QGRole]在某个频道服务器中的表现类型。
 *
 * @see QGRole
 */
@ExperimentalSimbotApi
public interface QGGuildRole : QGRole, DeleteSupport {


    /**
     * 将当前角色赋予指定用户ID的用户。
     *
     * @throws QQGuildApiException API异常，例如没有权限或用户不存在等
     */
    @JST
    public suspend fun grantTo(memberId: ID): QGMemberRole

    /**
     * 将当前角色赋予指定用户。[member] 的实际类型必须为 [QGMember]
     *
     * @throws ClassCastException 如果 [member] 的类型不是 [QGMember]
     * @throws QQGuildApiException API异常，例如没有权限或用户不存在等
     */
    @JST
    public suspend fun grantTo(member: GuildMember): QGMemberRole =
        grantTo(member as? QGMember ?: throw ClassCastException("QGGuildRole.grantTo only support member of type QGMember, but ${member::class}"))

    /**
     * 将当前角色赋予指定用户。
     *
     * @throws QQGuildApiException API异常，例如没有权限或用户不存在等
     */
    @JST
    public suspend fun grantTo(member: QGMember): QGMemberRole


    /**
     * 将当前角色赋予指定用户ID的用户。
     *
     * @param channelId 如果身份组 `ID` 是 `5-子频道管理员`，需要增加 `channel.id` 来指定具体是哪个子频道
     *
     * @throws QQGuildApiException API异常，例如没有权限或用户不存在等
     */
    @JST
    public suspend fun grantTo(memberId: ID, channelId: ID): QGMemberRole


    /**
     * 将当前角色赋予指定用户。[member] 的实际类型必须为 [QGMember]
     *
     * @param channelId 如果身份组 `ID` 是 `5-子频道管理员`，需要增加 `channel.id` 来指定具体是哪个子频道
     *
     * @throws ClassCastException 如果 [member] 的类型不是 [QGMember]
     * @throws QQGuildApiException API异常，例如没有权限或用户不存在等
     */
    @JST
    public suspend fun grantTo(member: GuildMember, channelId: ID): QGMemberRole =
        grantTo(member as? QGMember ?: throw ClassCastException("QGGuildRole.grantTo only support member of type QGMember, but ${member::class}"), channelId)

    /**
     * 将当前角色赋予指定用户。
     *
     * @param channelId 如果身份组 `ID` 是 `5-子频道管理员`，需要增加 `channel.id` 来指定具体是哪个子频道
     *
     * @throws QQGuildApiException API异常，例如没有权限或用户不存在等
     */
    @JST
    public suspend fun grantTo(member: QGMember, channelId: ID): QGMemberRole

    /**
     * 删除频道下对应的角色（身份组）。
     *
     * @return 如果没出现异常则始终为 `true`
     *
     * @throws QQGuildApiException API异常，例如没有权限或对象不存在
     */
    @JST
    override suspend fun delete(): Boolean

    /**
     * 得到当前频道身份组下所有的频道成员。
     */
    public val members: Items<QGMember>

}
