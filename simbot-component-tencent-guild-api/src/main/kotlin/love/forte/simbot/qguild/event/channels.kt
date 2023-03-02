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
public class ChannelCreate(override val s: Long, @SerialName("d") override val data: EventChannel) : Signal.Dispatch()

/**
 * 子频道事件 [CHANNEL_UPDATE](https://bot.q.qq.com/wiki/develop/api/gateway/channel.html#channel-update)
 *
 * ## 发送时机
 * - 子频道信息变更
 */
@Serializable
@SerialName(EventIntents.Guilds.CHANNEL_UPDATE_TYPE)
public class ChannelUpdate(override val s: Long, @SerialName("d") override val data: EventChannel) : Signal.Dispatch()

/**
 * 子频道事件 [CHANNEL_DELETE](https://bot.q.qq.com/wiki/develop/api/gateway/channel.html#channel-delete)
 *
 * ## 发送时机
 * - 子频道被删除
 */
@Serializable
@SerialName(EventIntents.Guilds.CHANNEL_DELETE_TYPE)
public class ChannelDelete(override val s: Long, @SerialName("d") override val data: EventChannel) : Signal.Dispatch()


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
//    /**
//     * 排序值，具体请参考 有[关 position 的说明](https://bot.q.qq.com/wiki/develop/api/openapi/channel/model.html#%E6%9C%89%E5%85%B3-position-%E7%9A%84%E8%AF%B4%E6%98%8E)
//     *
//     * - position 从 1 开始
//     * - 当子频道类型为 子频道分组（ChannelType=4）时，由于 position 1 被未分组占用，所以 position 只能从 2 开始
//     * - 如果不传默认追加到分组下最后一个
//     * - 如果填写一个已经存在的值，那么会插入在原来的元素之前
//     * - 如果填写一个较大值，与不填是相同的表现，同时存储的值会根据真实的 position 进行重新计算，并不会直接使用传入的值
//     *
//     */
//    val position: Int,
//    /** 所属分组 id，仅对子频道有效，对 子频道分组（ChannelType=4） 无效 */
//    @SerialName("parent_id") val parentId: String,
//    /** 子频道私密类型 [PrivateType] */
//    @SerialName("private_type") val privateType: PrivateType,
//    /** 子频道发言权限 [SpeakPermission] */
//    @SerialName("speak_permission") val speakPermission: SpeakPermission,
//    /** 用于标识应用子频道应用类型，仅应用子频道时会使用该字段，具体定义请参考 [应用子频道的应用类型](https://bot.q.qq.com/wiki/develop/api/openapi/channel/model.html#%E5%BA%94%E7%94%A8%E5%AD%90%E9%A2%91%E9%81%93%E7%9A%84%E5%BA%94%E7%94%A8%E7%B1%BB%E5%9E%8B) */
//    @SerialName("application_id") val applicationId: String,
//    /** 用户拥有的子频道权限 [Permissions] */
//    @get:JvmSynthetic
//    val permissions: Permissions,

)
