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

package love.forte.simbot.component.qguild

import kotlinx.coroutines.CoroutineScope
import love.forte.simbot.ID
import love.forte.simbot.Timestamp
import love.forte.simbot.definition.*
import love.forte.simbot.message.Message
import love.forte.simbot.message.MessageReceipt
import love.forte.simbot.qguild.model.Channel
import love.forte.simbot.utils.item.Items
import kotlin.coroutines.CoroutineContext
import kotlin.time.Duration


/**
 * 一个QQ频道中的子频道 `Channel` 类型。
 *
 * [QGChannel] 提供部分来自 [source channel][Channel] 的属性获取。

 *
 * @author ForteScarlet
 */
@Suppress("DeprecatedCallableAddReplaceWith")
public interface QGChannel : BotContainer, CoroutineScope, QGObjectiveContainer<Channel>, love.forte.simbot.definition.Channel {
    /**
     * 原始的子频道信息
     */
    override val source: Channel

    /**
     * 所属bot
     */
    override val bot: QGGuildBot

    /**
     * 子频道ID
     */
    override val id: ID

    /**
     * 所属频道ID
     */
    override val guildId: ID

    /**
     * 子频道名称
     */
    override val name: String get() = source.name

    /**
     * 创建人ID。
     */
    override val ownerId: ID

    /**
     * 无效的属性，始终得到 [Timestamp.notSupport()]。
     */
    @Deprecated("Invalid property")
    override val createTime: Timestamp get() = Timestamp.notSupport()

    /**
     * 无效的属性，始终得到 `-1`。
     */
    @Deprecated("Invalid property")
    override val currentMember: Int get() = -1

    /**
     * 无效的属性，始终得到 `""`。
     */
    @Deprecated("Invalid property")
    override val description: String get() = ""

    /**
     * 无效的属性，始终得到 `""`。
     */
    @Deprecated("Invalid property")
    override val icon: String get() = ""

    /**
     * 无效的属性，始终得到 `""`。
     */
    @Deprecated("Invalid property")
    override val maximumMember: Int get() = -1

    /**
     * 子频道分组**的ID**。
     *
     * 子频道分组ID实例 [QGChannelCategoryId] 是一个仅包含 `id` 信息的未初始化实例，
     * 其 [id][QGChannelCategoryId.id] 和 [name][QGChannelCategoryId.name]
     * 的值都是 [source.parentId][love.forte.simbot.qguild.model.Channel.parentId] 的值，
     * 即分组ID的字符串值。
     *
     * 如果希望获取完整信息，使用 [QGChannelCategoryId.resolve].
     *
     * @see QGChannelCategoryId
     */
    override val category: QGChannelCategoryId

    // TODO?

    override suspend fun send(message: Message): MessageReceipt {
        TODO("Not yet implemented")
    }

    override val members: Items<GuildMember>
        get() = TODO("Not yet implemented")

    override suspend fun guild(): Guild {
        TODO("Not yet implemented")
    }

    override suspend fun member(id: ID): GuildMember? {
        TODO("Not yet implemented")
    }

    override suspend fun owner(): GuildMember {
        TODO("Not yet implemented")
    }

    override val roles: Items<Role>
        get() = TODO("Not yet implemented")

    override suspend fun mute(duration: Duration): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun previous(): Organization? {
        TODO("Not yet implemented")
    }

    override suspend fun unmute(): Boolean {
        TODO("Not yet implemented")
    }

    override val coroutineContext: CoroutineContext
        get() = TODO("Not yet implemented")
}
