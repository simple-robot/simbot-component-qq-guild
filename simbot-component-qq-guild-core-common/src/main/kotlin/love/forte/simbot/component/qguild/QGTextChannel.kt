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

import love.forte.simbot.ID
import love.forte.simbot.Timestamp
import love.forte.simbot.component.qguild.message.QGMessageReceipt
import love.forte.simbot.definition.Channel
import love.forte.simbot.definition.ChannelInfo
import love.forte.simbot.definition.Role
import love.forte.simbot.message.Message
import love.forte.simbot.message.MessageContent
import love.forte.simbot.message.Text
import love.forte.simbot.qguild.QQGuildApiException
import love.forte.simbot.qguild.model.ChannelType
import love.forte.simbot.utils.item.Items
import love.forte.simbot.utils.item.Items.Companion.emptyItems
import love.forte.simbot.utils.item.flowItems
import kotlin.time.Duration

/**
 * QQ频道中的 **文字子频道**。
 *
 * [QGTextChannel] 是当 [source.type][love.forte.simbot.qguild.model.Channel.type] 为 [ChannelType.TEXT] 类型时的表现。
 * 因为只有 [ChannelType.TEXT] 类型的子频道允许消息发送等行为，因此 [QGTextChannel] 作为 [simbot Channel][Channel] 必须要求子频道的类型。
 *
 * 其他子频道的应用类型可参考 [QGChannel] 的其他实现类型，
 * 如果希望获取所有子频道，参考 [QGGuild.sourceChannels]
 *
 *
 * @see QGChannel
 * @see QGGuild
 * @author ForteScarlet
 */
@Suppress("DeprecatedCallableAddReplaceWith")
public interface QGTextChannel : QGChannel, ChannelInfo, Channel {

    /**
     * 原始的子频道信息
     */
    override val source: love.forte.simbot.qguild.model.Channel

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
     * 无效的属性，始终得到 [Timestamp.notSupport()]。考虑使用 [guild] 获取相关信息。
     */
    @Deprecated("Invalid property")
    override val createTime: Timestamp get() = Timestamp.notSupport()

    /**
     * 无效的属性，始终得到 `-1`。考虑使用 [guild] 获取相关信息。
     */
    @Deprecated("Invalid property")
    override val currentMember: Int get() = -1

    /**
     * 无效的属性，始终得到 `""`。考虑使用 [guild] 获取相关信息。
     */
    @Deprecated("Invalid property")
    override val description: String get() = ""

    /**
     * 无效的属性，始终得到 `""`。考虑使用 [guild] 获取相关信息。
     */
    @Deprecated("Invalid property")
    override val icon: String get() = ""

    /**
     * 无效的属性，始终得到 `""`。考虑使用 [guild] 获取相关信息。
     */
    @Deprecated("Invalid property")
    override val maximumMember: Int get() = -1

    /**
     * 子频道分组**的ID**。
     *
     * 子频道分组ID实例 [QGChannelCategoryId] 是一个仅包含 `id`
     * 信息的未初始化实例，其 [id][QGChannelCategoryId.id] 和 [name][QGChannelCategoryId.name]
     * 的值都是 [source.parentId][love.forte.simbot.qguild.model.Channel.parentId] 的值，即分组ID的字符串值。
     *
     * 如果希望获取完整信息，使用 [QGChannelCategoryId.resolve].
     *
     * @see QGChannelCategoryId
     */
    override val category: QGChannelCategoryId

    /**
     * 得到当前子频道所属频道服务器
     */
    @JSTP
    override suspend fun guild(): QGGuild

    /**
     * 得到当前子频道所属用户
     */
    @JSTP
    override suspend fun owner(): QGMember

    /**
     * 尚不支持子频道角色（权限）获取。
     *
     * 如果希望获取频道角色，使用 [guild] 后获取。
     */
    @Deprecated("NotSupported yet")
    override val roles: Items<Role> get() = emptyItems()

    /**
     * 同 [guild]
     */
    @JSTP
    override suspend fun previous(): QGGuild = guild()

    /**
     * 向子频道发送消息。此频道需要为文字频道。
     *
     * @throws QQGuildApiException 请求异常，例如无权限
     *
     */
    @JST
    override suspend fun send(message: Message): QGMessageReceipt


    /**
     * 向子频道发送消息。此频道需要为文字频道。
     *
     * @throws QQGuildApiException 请求异常，例如无权限
     *
     */
    @JST
    override suspend fun send(text: String): QGMessageReceipt {
        return send(Text.of(text))
    }


    /**
     * 向子频道发送消息。此频道需要为文字频道。
     *
     * @throws QQGuildApiException 请求异常，例如无权限
     *
     */
    @JST
    override suspend fun send(message: MessageContent): QGMessageReceipt {
        return send(message.messages)
    }

    /**
     * 子频道不能获取成员列表，考虑使用 [guild] 获取。
     */
    @Deprecated(
        "Channels cannot get a list of members, use `guild` to get it.",
        ReplaceWith("guild().members")
    )
    override val members: Items<QGMember>
        get() = flowItems { prop ->
            guild().members
                .batch(prop.batch)
                .offset(prop.offset)
                .limit(prop.limit)
                .collect {
                    emit(it)
                }
        }

    /**
     * 子频道不能获取成员，考虑使用 [guild] 获取。
     */
    @Deprecated(
        "Channels cannot get a member, use `guild` to get it.",
        ReplaceWith("guild().member(id)")
    )
    @JST(blockingBaseName = "getMember", blockingSuffix = "", asyncBaseName = "getMember")
    override suspend fun member(id: ID): QGMember? = guild().member(id)

    // TODO
    @Deprecated("Mute channel is not supported", ReplaceWith("false"))
    @JvmSynthetic
    override suspend fun mute(duration: Duration): Boolean = false

    // TODO
    @Deprecated("Mute channel is not supported", ReplaceWith("false"))
    @JvmSynthetic
    override suspend fun unmute(): Boolean = false
}
