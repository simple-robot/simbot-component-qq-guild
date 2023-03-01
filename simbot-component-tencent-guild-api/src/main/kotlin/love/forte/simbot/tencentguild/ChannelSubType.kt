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


