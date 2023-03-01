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

package love.forte.simbot.tencentguild

import kotlinx.serialization.Serializable
import love.forte.simbot.Timestamp


/**
 *
 * [日程对象](https://bot.q.qq.com/wiki/develop/api/openapi/schedule/model.html#%E6%97%A5%E7%A8%8B%E5%AF%B9%E8%B1%A1-schedule)
 *
 * TODO impl
 *
 * @author ForteScarlet
 */
public interface TencentSchedule {

    /** 日程 id */
    public val id: String

    /** 日程名称 */
    public val name: String

    /** 日程描述 */
    public val description: String

    /** 日程开始时间戳(ms) */
    public val startTimestamp: Timestamp

    /** 日程结束时间戳(ms) */
    public val endTimestamp: Timestamp

    /** 创建者 */
    public val creator: TencentMemberInfo

    /** 日程开始时跳转到的子频道 id */
    public val jumpChannelId: String

    /** 日程提醒类型，取值参考RemindType */
    public val remindType: RemindType


    @Serializable
    public enum class RemindType(public val type: String) {
        /** 不提醒 */
        NO_REMIND("0"),

        /** 开始时提醒 */
        START("1"),

        /** 开始前5分钟提醒 */
        MINUTES_5_BEFORE_START("2"),

        /** 开始前15分钟提醒 */
        MINUTES_15_BEFORE_START("3"),

        /** 开始前30分钟提醒 */
        MINUTES_30_BEFORE_START("4"),

        /** 开始前60分钟提醒 */
        MINUTES_60_BEFORE_START("5"),
        ;
    }
}

/**
 * 通过 [type] 字符串解析得到一个 [TencentSchedule.RemindType]. 如果 [type] 没有对应的结果，则会抛出 [IllegalArgumentException] 异常。
 *
 */
public fun scheduleRemindType(type: String): TencentSchedule.RemindType {
    // 目前所有的type的length都是1
    if (type.length > 1) throw IllegalArgumentException("Unknown schedule type value $type")

    return TencentSchedule.RemindType.values().find { it.type == type }
        ?: throw IllegalArgumentException("Unknown schedule type value $type")
}
