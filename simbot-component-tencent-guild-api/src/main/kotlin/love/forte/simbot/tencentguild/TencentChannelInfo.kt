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
import love.forte.simbot.definition.*
import love.forte.simbot.tencentguild.internal.*

/**
 *
 * tencent channel info
 * @author ForteScarlet
 */
public interface TencentChannelInfo : ChannelInfo {
    /**
     * 子频道id
     */
    override val id: ID

    /**
     * 频道id
     */
    override val guildId: ID

    /**
     * 子频道名
     */
    override val name: String

    /**
     * 子频道类型
     * @see ChannelType
     */
    public val channelTypeValue: Int

    /**
     * 子频道子类型
     * @see ChannelSubType
     */
    public val channelSubTypeValue: Int

    /**
     * 排序，必填，而且不能够和其他子频道的值重复
     */
    public val position: Int

    /**
     * 分组 id
     */
    public val parentId: String

    /**
     * 创建人 id
     */
    override val ownerId: ID


    @Deprecated("子频道没有创建时间信息", ReplaceWith("Timestamp.NotSupport", "love.forte.simbot.Timestamp"))
    override val createTime: Timestamp
        get() = Timestamp.NotSupport

    @Deprecated("子频道没有人数信息", ReplaceWith("-1"))
    override val currentMember: Int
        get() = -1

    @Deprecated("子频道没有描述信息", ReplaceWith("\"\""))
    override val description: String
        get() = ""

    @Deprecated("子频道没有图标", ReplaceWith("\"\""))
    override val icon: String
        get() = ""

    @Deprecated("子频道没有人数信息", ReplaceWith("-1"))
    override val maximumMember: Int
        get() = -1

    public companion object {
        internal val serializer: KSerializer<out TencentChannelInfo> = TencentChannelInfoImpl.serializer()
    }
}


/**
 * https://bot.q.qq.com/wiki/develop/api/openapi/channel/model.html#channeltype
 */
public enum class ChannelType(public val code: Int) {
    TEXT(0),
    UNKNOWN_1(1),
    VOICE(2),
    UNKNOWN_3(3),
    GROUPING(4),
    LIVE(10005),
    ;

    public companion object {
        @JvmStatic
        public fun byCode(code: Int): ChannelType = when (code) {
            0 -> TEXT
            1 -> UNKNOWN_1
            2 -> VOICE
            3 -> UNKNOWN_3
            4 -> GROUPING
            10005 -> LIVE
            else -> throw NoSuchElementException("code: $code")
        }
    }
}

/**
 * https://bot.q.qq.com/wiki/develop/api/openapi/channel/model.html#channelsubtype
 */
public enum class ChannelSubType(public val code: Int) {
    /** 闲聊 */
    SMALL_TALK(0),

    /** 公告 */
    ANNOUNCEMENT(1),

    /** 攻略 */
    RAIDERS(2),

    /** 开黑 */
    PLAY_TOGETHER(3),
    ;

    public companion object {
        @JvmStatic
        public fun byCode(code: Int): ChannelSubType = when (code) {
            0 -> SMALL_TALK
            1 -> ANNOUNCEMENT
            2 -> RAIDERS
            3 -> PLAY_TOGETHER
            else -> throw NoSuchElementException("code: $code")
        }
    }
}