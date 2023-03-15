/*
 * Copyright (c) 2022-2023. ForteScarlet.
 *
 * This file is part of simbot-component-qq-guild.
 *
 * simbot-component-qq-guild is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * simbot-component-qq-guild is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with simbot-component-qq-guild. If not, see <https://www.gnu.org/licenses/>.
 */

package love.forte.simbot.component.qguild

import love.forte.plugin.suspendtrans.annotation.JvmAsync
import love.forte.plugin.suspendtrans.annotation.JvmBlocking
import love.forte.simbot.ID
import love.forte.simbot.definition.BotContainer
import love.forte.simbot.definition.Category
import love.forte.simbot.definition.GuildInfoContainer
import love.forte.simbot.literal
import love.forte.simbot.qguild.QQGuildApiException
import love.forte.simbot.qguild.model.ChannelType
import love.forte.simbot.qguild.model.Channel as QGSourceChannel


/**
 * 当一个频道的 [QGSourceChannel.type] 的值等于 [ChannelType.CATEGORY] 时，
 * 此频道代表为一个分组。
 *
 * [QGChannelCategoryId] 是一个**仅存在ID**的 QQ频道子频道分组实现。QQ频道对于子频道分组类型的变更不会推送事件，
 * 因此无法内建缓存，而如果每次事件都要实时查询channel的分组则可能有些多余 —— 毕竟分组可能并不是一个高频使用的对象。
 *
 * 因此在 [QGChannel.category] 中我们仅提供 [QGChannelCategoryId] 类型来直接提供分组ID，
 * 并在有需要的时候通过 [resolve] 查询并获取真正的对象实例。
 *
 * [QGChannelCategoryId] 中 [id] 为子频道分组的ID，[name] 由于需要通过API查询，
 * 此处将直接返回 [id] 的字符串值。
 *
 * 通过 [QGGuild.categories] 或 [QGGuild.category] 获取的结果均为实时查询结果，
 * 它们是具体的 [QGChannelCategory] 类型，其中已经包含了具体信息。
 *
 * @see QGChannelCategory
 */
public interface QGChannelCategoryId : Category, BotContainer, GuildInfoContainer {
    /**
     * 所属BOT
     */
    override val bot: QGGuildBot

    /**
     * 当前子频道分组ID
     */
    override val id: ID

    /**
     * 当前子频道分组ID。分组信息未初始化时，值同 [id]。
     * 如果需要获取真正的名称，判断当前类型是否为 [QGChannelCategory]
     * 或直接通过 [resolve] 实时查询新的结果。
     */
    override val name: String get() = id.literal

    /**
     * 获取此分类所属的频道服务器。
     */
    @JvmBlocking(asProperty = true, suffix = "")
    @JvmAsync(asProperty = true)
    override suspend fun guild(): QGGuild

    /**
     * 根据当前ID查询一个具体的分组对象实例 [QGChannelCategory].
     *
     * @throws NoSuchElementException 当未查询到结果（例如查询时分组已被删除）
     * @throws QQGuildApiException api查询期间得到的任何异常
     */
    @JvmAsync
    @JvmBlocking
    public suspend fun resolve(): QGChannelCategory
}




/**
 * 当一个频道的 [QGSourceChannel.type] 的值等于 [ChannelType.CATEGORY] 时，
 * 此频道代表为一个分组。
 *
 * 可以通过 [QGGuild.categories] 或 [QGGuild.category] 查询获取。
 *
 * @author ForteScarlet
 */
public interface QGChannelCategory : Category, BotContainer, GuildInfoContainer, QGChannelCategoryId, QGObjectiveContainer<QGSourceChannel> {
    /**
     * 所属BOT
     */
    override val bot: QGGuildBot

    /**
     * 当前子频道分组ID
     */
    override val id: ID

    /**
     * 分组名称
     */
    override val name: String get() = source.name

    //// just like model Channel

    /**
     * 所属频道ID
     */
    public val guildId: ID

    /**
     * 创建人ID
     */
    public val ownerId: ID

    /**
     * 排序值。无法获取时得到 -1
     */
    public val position: Int get() = source.position


    /**
     * 获取此分类所属的频道服务器。
     */
    @JvmBlocking(asProperty = true, suffix = "")
    @JvmAsync(asProperty = true)
    override suspend fun guild(): QGGuild

    /**
     * 根据当前ID查询一个具体的分组对象实例。
     * 效果同 [QGChannelCategoryId.resolve], 会查询并得到一个新的实例，
     * 当前实例内信息不变。
     *
     * @throws NoSuchElementException 当未查询到结果（例如查询时分组已被删除）
     * @throws QQGuildApiException api查询期间得到的任何异常
     */
    @JvmSynthetic
    override suspend fun resolve(): QGChannelCategory
}
