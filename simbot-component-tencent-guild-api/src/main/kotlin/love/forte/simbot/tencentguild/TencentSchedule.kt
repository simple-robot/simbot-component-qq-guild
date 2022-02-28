/*
 *  Copyright (c) 2022-2022 ForteScarlet <ForteScarlet@163.com>
 *
 *  本文件是 simbot-component-tencent-guild 的一部分。
 *
 *  simbot-component-tencent-guild 是自由软件：你可以再分发之和/或依照由自由软件基金会发布的 GNU 通用公共许可证修改之，无论是版本 3 许可证，还是（按你的决定）任何以后版都可以。
 *
 *  发布 simbot-component-tencent-guild 是希望它能有用，但是并无保障;甚至连可销售和符合某个特定的目的都不保证。请参看 GNU 通用公共许可证，了解详情。
 *
 *  你应该随程序获得一份 GNU 通用公共许可证的复本。如果没有，请看:
 *  https://www.gnu.org/licenses
 *  https://www.gnu.org/licenses/gpl-3.0-standalone.html
 *  https://www.gnu.org/licenses/lgpl-3.0-standalone.html
 *
 *
 */

package love.forte.simbot.tencentguild

import kotlinx.serialization.*
import love.forte.simbot.*


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
