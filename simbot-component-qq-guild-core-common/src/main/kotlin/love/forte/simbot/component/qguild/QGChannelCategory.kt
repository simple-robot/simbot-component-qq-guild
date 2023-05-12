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

import kotlinx.coroutines.sync.Mutex
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
 * 因此在 [QGTextChannel.category] 中我们仅提供 [QGChannelCategoryId] 类型来直接提供分组ID，
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
    @JSTP
    override suspend fun guild(): QGGuild

    /**
     * 根据当前ID**初始化**并得到一个具体的分组对象实例 [QGChannelCategory].
     *
     * [resolve] 只会最多生效一次并记录其结果，内部通过 [锁][Mutex] 控制并发。
     *
     * 如下：
     * ```kotlin
     * category.resolve().resolve()
     * ```
     *
     * **至多** 只会请求一次API。参考 [QGChannelCategory.resolve]。
     *
     * 如果在初始化阶段出现异常（例如无权限或未找到），则下次仍然会发起请求（且仍然会得到可能的任何异常）。
     *
     * @throws NoSuchElementException 当未查询到结果（例如查询时分组已被删除）
     * @throws QQGuildApiException api查询期间得到的任何异常
     */
    @JST
    public suspend fun resolve(): QGChannelCategory

}


/**
 * 频道分组。[QGChannel] 的实现类型之一。
 *
 * 当一个频道的 [QGSourceChannel.type] 的值等于 [ChannelType.CATEGORY] 时，
 * 此频道代表为一个分组。
 *
 * 可以通过 [QGGuild.categories] 或 [QGGuild.category] 查询获取。
 *
 * @see QGChannel
 *
 * @author ForteScarlet
 */
public interface QGChannelCategory : Category, QGChannel, BotContainer, GuildInfoContainer, QGChannelCategoryId, QGObjectiveContainer<QGSourceChannel> {
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

    /**
     * 所属频道ID
     */
    override val guildId: ID

    /**
     * 排序值。无法获取时得到 -1
     */
    public val position: Int get() = source.position


    /**
     * 获取此分类所属的频道服务器。
     */
    @JSTP
    override suspend fun guild(): QGGuild

    /**
     * 直接得到自身 ( `this` )。
     */
    @JvmSynthetic
    override suspend fun resolve(): QGChannelCategory
}
