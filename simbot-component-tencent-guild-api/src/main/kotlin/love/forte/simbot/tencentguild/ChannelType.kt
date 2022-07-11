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
public const val TEXT: Int = 0

/**
 * > 保留，不可用
 */
@Deprecated("reserved, unavailable")
public const val UNKNOWN_1: Int = 1

/**
 * 语音子频道
 */
public const val VOICE: Int = 2

/**
 * > 保留，不可用
 */
@Deprecated("reserved, unavailable")
public const val UNKNOWN_3: Int = 3

/**
 * 子频道分组
 */
public const val GROUPING: Int = 4

/**
 * 直播子频道
 */
public const val LIVE: Int = 10005

/**
 * 应用子频道
 */
public const val APP: Int = 10006

/**
 * 论坛子频道
 */
public const val FORUM: Int = 10007
// endregion


/**
 * 频道类型。
 *
 * Java使用者可以通过静态工具类 `ChannelTypes` 中的各方法或常量。
 *
 * e.g.
 * ```java
 * int appValue = ChannelTypes.APP;
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
public inline val ChannelType.Companion.Text: ChannelType get() = ChannelType(TEXT)

@get:JvmSynthetic
public inline val ChannelType.Companion.Voice: ChannelType get() = ChannelType(VOICE)

@get:JvmSynthetic
public inline val ChannelType.Companion.Grouping: ChannelType get() = ChannelType(GROUPING)

@get:JvmSynthetic
public inline val ChannelType.Companion.Live: ChannelType get() = ChannelType(LIVE)

@get:JvmSynthetic
public inline val ChannelType.Companion.App: ChannelType get() = ChannelType(APP)

@get:JvmSynthetic
public inline val ChannelType.Companion.Forum: ChannelType get() = ChannelType(FORUM)
// endregion

// region 判断
@get:JvmName("isText")
public val ChannelType.isText: Boolean get() = code == TEXT

@get:JvmName("isVoice")
public val ChannelType.isVoice: Boolean get() = code == VOICE

@get:JvmName("isGrouping")
public val ChannelType.isGrouping: Boolean get() = code == GROUPING

@get:JvmName("isLive")
public val ChannelType.isLive: Boolean get() = code == LIVE

@get:JvmName("isApp")
public val ChannelType.isApp: Boolean get() = code == APP

@get:JvmName("isForum")
public val ChannelType.isForum: Boolean get() = code == FORUM

// endregion


