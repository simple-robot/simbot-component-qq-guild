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

@file:JvmName("ChannelSubTypes")

package love.forte.simbot.tencentguild

// region 常量

/*
 * 编码常量信息来自于：
 *  https://bot.q.qq.com/wiki/develop/api/openapi/channel/model.html#channelsubtype
 *
 * 有更新？通过 issue 告诉我们或者 PR 帮助我们更新！
 * Issues: https://github.com/simple-robot/simbot-component-tencent-guild/issues
 * PR:     https://github.com/simple-robot/simbot-component-tencent-guild/pulls
 *
 */

/** 闲聊 */
public const val CHANNEL_SUB_TYPES_SMALL_TALK: Int = 0

/** 公告 */
public const val CHANNEL_SUB_TYPES_ANNOUNCEMENT: Int = 1

/** 攻略 */
public const val CHANNEL_SUB_TYPES_RAIDERS: Int = 2

/** 开黑 */
public const val CHANNEL_SUB_TYPES_PLAY_TOGETHER: Int = 3
// endregion

/**
 * 频道子类型。
 *
 * Java中可以使用 `ChannelSubTypes` 中的各静态方法或常量。
 *
 * e.g.
 *
 * ```java
 * int smallTalkValue = ChannelSubTypes.CHANNEL_SUB_TYPES_SMALL_TALK;
 * boolean isSmallTalk = ChannelSubTypes.isSmallTalk(0);
 * ```
 *
 * see [channel sub type](https://bot.q.qq.com/wiki/develop/api/openapi/channel/model.html#channelsubtype)
 */
@JvmInline
@kotlinx.serialization.Serializable
public value class ChannelSubType(public val code: Int) {
    public companion object
}


// region 工厂
@get:JvmSynthetic
public inline val ChannelSubType.Companion.SmallTalk: ChannelSubType get() = ChannelSubType(CHANNEL_SUB_TYPES_SMALL_TALK)

@get:JvmSynthetic
public inline val ChannelSubType.Companion.Announcement: ChannelSubType get() = ChannelSubType(CHANNEL_SUB_TYPES_ANNOUNCEMENT)

@get:JvmSynthetic
public inline val ChannelSubType.Companion.Raiders: ChannelSubType get() = ChannelSubType(CHANNEL_SUB_TYPES_RAIDERS)

@get:JvmSynthetic
public inline val ChannelSubType.Companion.PlayTogether: ChannelSubType get() = ChannelSubType(CHANNEL_SUB_TYPES_PLAY_TOGETHER)
// endregion


// region 判断
@get:JvmName("isSmallTalk")
public val ChannelSubType.isSmallTalk: Boolean get() = code == CHANNEL_SUB_TYPES_SMALL_TALK

@get:JvmName("isAnnouncement")
public val ChannelSubType.isAnnouncement: Boolean get() = code == CHANNEL_SUB_TYPES_ANNOUNCEMENT

@get:JvmName("isRaiders")
public val ChannelSubType.isRaiders: Boolean get() = code == CHANNEL_SUB_TYPES_RAIDERS

@get:JvmName("isPlayTogether")
public val ChannelSubType.isPlayTogether: Boolean get() = code == CHANNEL_SUB_TYPES_PLAY_TOGETHER
// endregion


