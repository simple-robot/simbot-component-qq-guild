package love.forte.simbot.tencentguild

import love.forte.simbot.ID

/**
 *
 * tencent channel info
 * @author ForteScarlet
 */
public interface TencentChannelInfo {
    /**
     * 子频道id
     */
    public val id: ID

    /**
     * 频道id
     */
    public val guildId: ID

    /**
     * 子频道名
     */
    public val name: String

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
    public val ownerId: String
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
        public fun byCode(code: Int): ChannelType = when(code) {
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