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

package love.forte.simbot.qguild.model.forum

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.qguild.ApiModel

/**
 *
 * 富文本内容
 *
 * 参考[forum文档](https://bot.q.qq.com/wiki/develop/api/openapi/forum/model.html#thread)
 * @author ForteScarlet
 */
@ApiModel
@Serializable
public data class RichObject(
    /**
     * 富文本类型
     *
     * @see RichTypes
     */
    val type: Int,
    /**
     * 文本
     * @see TextInfo
     */
    @SerialName("text_info") val textInfo: TextInfo,
    /**
     * `@` 内容
     * @see AtInfo
     */
    @SerialName("at_info") val atInfo: String,
    /**
     * 链接
     * @see URLInfo
     */
    @SerialName("url_info") val urlInfo: String,
    /**
     * 表情
     * @see EmojiInfo
     */
    @SerialName("emoji_info") val emojiInfo: String,
    /**
     * 提到的子频道
     * @see ChannelInfo
     */
    @SerialName("channel_info") val channelInfo: String,
)

/**
 * 富文本类型的部分常量
 *
 * 参考[forum文档](https://bot.q.qq.com/wiki/develop/api/openapi/forum/model.html#thread)
 * @author ForteScarlet
 */
public object RichTypes {
    /**
     * 普通文本
     */
    public const val TEXT: Int = 1

    /**
     * at信息
     */
    public const val AT: Int = 2

    /**
     * url信息
     */
    public const val URL: Int = 3

    /**
     * 表情
     */
    public const val EMOJI: Int = 4

    /**
     * #子频道
     */
    public const val CHANNEL: Int = 5

    /**
     * 视频
     */
    public const val VIDEO: Int = 10

    /**
     * 图片
     */
    public const val IMAGE: Int = 11
}

/**
 * 富文本 - 普通文本
 *
 * @property text 普通文本
 *
 * 参考[forum文档](https://bot.q.qq.com/wiki/develop/api/openapi/forum/model.html#thread)
 * @author ForteScarlet
 */
@ApiModel
@Serializable
public data class TextInfo(val text: String)

/**
 * 富文本 - @内容
 *
 * 参考[forum文档](https://bot.q.qq.com/wiki/develop/api/openapi/forum/model.html#thread)
 * @author ForteScarlet
 */
@ApiModel
@Serializable
public data class AtInfo(
    /**
     * at类型
     *
     * @see AtTypes
     */
    val type: Int,
    /**
     * 用户
     *
     * @see AtUserInfo
     */
    @SerialName("user_info")
    val userInfo: String,
    /**
     * 角色组信息
     *
     * @see AtRoleInfo
     */
    @SerialName("role_info")
    val roleInfo: String,
    /**
     * 频道信息
     *
     * @see AtGuildInfo
     */
    @SerialName("guild_info")
    val guildInfo: String,
)

/**
 * `@`类型的常量
 */
public object AtTypes {
    /**
     * at特定人
     */
    public const val AT_EXPLICIT_USER: Int = 1

    /**
     * at角色组所有人
     */
    public const val AT_ROLE_GROUP: Int = 2

    /**
     * at频道所有人
     */
    public const val AT_GUILD: Int = 3
}

// TODO
