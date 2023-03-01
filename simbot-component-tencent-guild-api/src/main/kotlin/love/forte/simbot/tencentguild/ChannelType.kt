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

@file:JvmName("ChannelTypes")

package love.forte.simbot.tencentguild

// region 常量

/*
 * 编码常量信息来自于：
 *  https://bot.q.qq.com/wiki/develop/api/openapi/channel/model.html#channeltype
 *
 * 有更新？通过 issue 告诉我们或者 PR 帮助我们更新！
 * Issues: https://github.com/simple-robot/simbot-component-tencent-guild/issues
 * PR:     https://github.com/simple-robot/simbot-component-tencent-guild/pulls
 *
 */

/**
 * 文字子频道
 */
public const val CHANNEL_TYPE_TEXT: Int = 0

/**
 * > 保留，不可用
 */
@Deprecated("reserved, unavailable")
public const val CHANNEL_TYPE_UNKNOWN_1: Int = 1

/**
 * 语音子频道
 */
public const val CHANNEL_TYPE_VOICE: Int = 2

/**
 * > 保留，不可用
 */
@Deprecated("reserved, unavailable")
public const val CHANNEL_TYPE_UNKNOWN_3: Int = 3

/**
 * 子频道分组
 */
public const val CHANNEL_TYPE_GROUPING: Int = 4

/**
 * 直播子频道
 */
public const val CHANNEL_TYPE_LIVE: Int = 10005

/**
 * 应用子频道
 */
public const val CHANNEL_TYPE_APP: Int = 10006

/**
 * 论坛子频道
 */
public const val CHANNEL_TYPE_FORUM: Int = 10007
// endregion


/**
 * 频道类型。
 *
 * Java使用者可以通过静态工具类 `ChannelTypes` 中的各方法或常量。
 *
 * e.g.
 * ```java
 * int appValue = ChannelTypes.CHANNEL_TYPE_APP;
 * boolean isText = ChannelTypes.isText(0);
 * ```
 *
 * see [channel type](https://bot.q.qq.com/wiki/develop/api/openapi/channel/model.html#channeltype)
 *
 */
@kotlinx.serialization.Serializable
@JvmInline
public value class ChannelType(public val code: Int) {
    public companion object
}

// region 工厂(?)函数
@get:JvmSynthetic
public inline val ChannelType.Companion.Text: ChannelType get() = ChannelType(CHANNEL_TYPE_TEXT)

@get:JvmSynthetic
public inline val ChannelType.Companion.Voice: ChannelType get() = ChannelType(CHANNEL_TYPE_VOICE)

@get:JvmSynthetic
public inline val ChannelType.Companion.Grouping: ChannelType get() = ChannelType(CHANNEL_TYPE_GROUPING)

@get:JvmSynthetic
public inline val ChannelType.Companion.Live: ChannelType get() = ChannelType(CHANNEL_TYPE_LIVE)

@get:JvmSynthetic
public inline val ChannelType.Companion.App: ChannelType get() = ChannelType(CHANNEL_TYPE_APP)

@get:JvmSynthetic
public inline val ChannelType.Companion.Forum: ChannelType get() = ChannelType(CHANNEL_TYPE_FORUM)
// endregion

// region 判断
@get:JvmName("isText")
public val ChannelType.isText: Boolean get() = code == CHANNEL_TYPE_TEXT

@get:JvmName("isVoice")
public val ChannelType.isVoice: Boolean get() = code == CHANNEL_TYPE_VOICE

@get:JvmName("isGrouping")
public val ChannelType.isGrouping: Boolean get() = code == CHANNEL_TYPE_GROUPING

@get:JvmName("isLive")
public val ChannelType.isLive: Boolean get() = code == CHANNEL_TYPE_LIVE

@get:JvmName("isApp")
public val ChannelType.isApp: Boolean get() = code == CHANNEL_TYPE_APP

@get:JvmName("isForum")
public val ChannelType.isForum: Boolean get() = code == CHANNEL_TYPE_FORUM

// endregion


