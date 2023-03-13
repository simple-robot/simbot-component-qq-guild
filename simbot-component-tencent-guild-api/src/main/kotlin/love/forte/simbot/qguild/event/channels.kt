/*
 * Copyright (c) 2023. ForteScarlet.
 *
 * This file is part of simbot-component-tencent-guild.
 *
 * simbot-component-tencent-guild is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * simbot-component-tencent-guild is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with simbot-component-tencent-guild. If not, see <https://www.gnu.org/licenses/>.
 */

package love.forte.simbot.qguild.event

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.qguild.ApiModel
import love.forte.simbot.qguild.model.Channel
import love.forte.simbot.qguild.model.ChannelSubType
import love.forte.simbot.qguild.model.ChannelType

/**
 * 子频道事件 [CHANNEL_CREATE](https://bot.q.qq.com/wiki/develop/api/gateway/channel.html#channel-create)
 *
 * ## 发送时机
 * - 子频道被创建
 */
@Serializable
@SerialName(EventIntents.Guilds.CHANNEL_CREATE_TYPE)
public data class ChannelCreate(override val s: Long, @SerialName("d") override val data: EventChannel) : Signal.Dispatch()

/**
 * 子频道事件 [CHANNEL_UPDATE](https://bot.q.qq.com/wiki/develop/api/gateway/channel.html#channel-update)
 *
 * ## 发送时机
 * - 子频道信息变更
 */
@Serializable
@SerialName(EventIntents.Guilds.CHANNEL_UPDATE_TYPE)
public data class ChannelUpdate(override val s: Long, @SerialName("d") override val data: EventChannel) : Signal.Dispatch()

/**
 * 子频道事件 [CHANNEL_DELETE](https://bot.q.qq.com/wiki/develop/api/gateway/channel.html#channel-delete)
 *
 * ## 发送时机
 * - 子频道被删除
 */
@Serializable
@SerialName(EventIntents.Guilds.CHANNEL_DELETE_TYPE)
public data class ChannelDelete(override val s: Long, @SerialName("d") override val data: EventChannel) : Signal.Dispatch()


/**
 * [子频道事件](https://bot.q.qq.com/wiki/develop/api/gateway/channel.html)
 * 中接收到的 [Channel] 信息。
 *
 * ## 内容
 * 在 [Channel] 的部分字段基础上，增加 `op_user_id` 代表操作人。
 *
 * _Note: [EventChannel] 暂不实现 [Channel]，因为这个 "部分" 还不好界定。_
 *
 */
@ApiModel
@Serializable
public data class EventChannel(
    /** 子频道 id */
    val id: String,
    /** 频道 id */
    @SerialName("guild_id") val guildId: String,
    /** 子频道名 */
    val name: String,
    /** 子频道类型 [ChannelType] */
    val type: ChannelType,
    /** 子频道子类型 [ChannelSubType] */
    @SerialName("sub_type") val subType: ChannelSubType,
    /** 创建人 id */
    @SerialName("owner_id") val ownerId: String,
    /** 操作人 */
    @SerialName("op_user_id") val opUserId: String,
)


