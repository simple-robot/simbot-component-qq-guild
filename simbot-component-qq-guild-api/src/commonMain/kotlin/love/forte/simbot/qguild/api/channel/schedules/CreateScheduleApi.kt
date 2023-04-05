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

package love.forte.simbot.qguild.api.channel.schedules

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.qguild.api.PostQQGuildApi
import love.forte.simbot.qguild.api.RouteInfoBuilder
import love.forte.simbot.qguild.api.SimplePostApiDescription
import love.forte.simbot.qguild.api.channel.schedules.CreateScheduleApi.Body.Companion.toCreateBody
import love.forte.simbot.qguild.model.Schedule
import love.forte.simbot.qguild.model.Schedule.RemindTypes
import kotlin.js.JsName
import kotlin.jvm.JvmName
import kotlin.jvm.JvmStatic


/**
 * [创建日程](https://bot.q.qq.com/wiki/develop/api/openapi/schedule/post_schedule.html)
 *
 * 用于在 `channel_id` 指定的 `日程子频道` 下创建一个日程。
 *
 * - 要求操作人具有 `管理频道` 的权限，如果是机器人，则需要将机器人设置为管理员。
 * - 创建成功后，返回创建成功的日程对象。
 * - 创建操作频次限制
 *     - 单个管理员每天限 `10` 次。
 *     - 单个频道每天 `100` 次。
 *
 * @author ForteScarlet
 */
public class CreateScheduleApi private constructor(
    channelId: String, body: Body
) : PostQQGuildApi<Schedule>() {
    public companion object Factory : SimplePostApiDescription("/channels/{channel_id}/schedules") {

        /**
         * 构造 [CreateScheduleApi]
         *
         * @param channelId 日程子频道ID
         * @param body 创建目标
         *
         */
        @JvmStatic
        public fun create(channelId: String, body: Body): CreateScheduleApi = CreateScheduleApi(channelId, body)

        /**
         * 构造 [CreateScheduleApi]
         *
         * @param channelId 日程子频道ID
         * @param schedule 创建目标，会通过 [toCreateBody] 转化
         *
         */
        @JvmStatic
        public fun create(channelId: String, schedule: Schedule): CreateScheduleApi =
            create(channelId, schedule.toCreateBody())

    }

    override val resultDeserializer: DeserializationStrategy<Schedule> get() = Schedule.serializer()

    private val path = arrayOf("channels", channelId, "schedules")

    override fun route(builder: RouteInfoBuilder) {
        builder.apiPath = path
    }

    override val body: Any = body

    /**
     * 用于 [CreateScheduleApi] 的请求body，结构类似于 [Schedule]，但是没有 `id` 和 `creator`。
     *
     */
    @Serializable
    public data class Body(
        /**
         * 日程名称
         */
        val name: String,
        /**
         * 日程描述
         */
        val description: String,
        /**
         * 日程开始时间戳(ms)
         */
        @SerialName("start_timestamp") val startTimestamp: Long,
        /**
         * 日程结束时间戳(ms)
         */
        @SerialName("end_timestamp") val endTimestamp: Long,
        /**
         * 日程开始时跳转到的子频道 id
         */
        @SerialName("jump_channel_id") val jumpChannelId: String,
        /**
         * 日程提醒类型，取值参考 [RemindTypes]。
         */
        @SerialName("remind_type") val remindType: String,
    ) {

        public companion object {
            /**
             * 将一个 [Schedule] 转化为请求用的 [Body]。
             */
            @JvmStatic
            @JvmName("of")
            @JsName("ofSchedule")
            public fun Schedule.toCreateBody(): Body = Body(
                name = name,
                description = description,
                startTimestamp = startTimestamp,
                endTimestamp = endTimestamp,
                jumpChannelId = jumpChannelId,
                remindType = remindType,
            )
        }
    }
}
