/*
 * Copyright (c) 2024. ForteScarlet.
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

package love.forte.simbot.component.qguild.guild

import love.forte.simbot.bot.GuildRelation
import love.forte.simbot.common.collectable.Collectable
import love.forte.simbot.common.id.ID
import love.forte.simbot.component.qguild.channel.*
import love.forte.simbot.qguild.QQGuildApiException
import love.forte.simbot.qguild.model.ChannelType
import love.forte.simbot.suspendrunner.ST
import kotlin.jvm.JvmSynthetic


/**
 * QQ频道组件中一个 [QGBot] 下针对频道服务器、子频道、子频道分组等相关内容的关系操作。
 *
 * @author ForteScarlet
 */
public interface QGGuildRelation : GuildRelation {

    override val guilds: Collectable<QGGuild>

    @ST(blockingBaseName = "getGuild", blockingSuffix = "", asyncBaseName = "getGuild")
    override suspend fun guild(id: ID): QGGuild?

    /**
     * 获取所有频道服务器的数量。
     * 注意：QQ频道没有直接获取数量的 API，
     * 因此会查询所有的频道列表并计数。
     */
    @JvmSynthetic
    override suspend fun guildCount(): Int

    /**
     * 直接获取指定ID的子频道。
     *
     * @see category
     *
     * @throws QQGuildApiException 请求失败，例如没有权限
     */
    @ST(blockingBaseName = "getChannel", blockingSuffix = "", asyncBaseName = "getChannel")
    public suspend fun channel(channelId: ID): QGChannel?

    /**
     * 直接获取指定ID的文字类型子频道。
     *
     * @see category
     *
     * @throws QQGuildApiException 请求失败，例如没有权限
     * @throws IllegalStateException 当目标子频道的类型不属于 [文字类型][ChannelType.TEXT] 时
     */
    @ST(blockingBaseName = "getChatChannel", blockingSuffix = "", asyncBaseName = "getChatChannel")
    public suspend fun chatChannel(channelId: ID): QGTextChannel?


    /**
     * 直接获取指定ID的子频道分类。
     *
     * @throws QQGuildApiException 请求失败，例如没有权限
     * @throws IllegalStateException 当目标子频道的类型不属于 [分组类型][ChannelType.CATEGORY] 时
     *
     */
    @ST(blockingBaseName = "getCategory", blockingSuffix = "", asyncBaseName = "getCategory")
    public suspend fun category(channelId: ID): QGCategoryChannel?

    /**
     * 根据ID寻找匹配的 **帖子类型** [ChannelType.FORUM] 的子频道
     *
     * @throws QQGuildApiException API请求过程中出现的异常
     * @throws IllegalStateException 当目标子频道类型不是 [ChannelType.FORUM] 时
     */
    @ST(blockingBaseName = "getForumChannel", blockingSuffix = "", asyncBaseName = "getForumChannel")
    public suspend fun forumChannel(id: ID): QGForumChannel?
}

