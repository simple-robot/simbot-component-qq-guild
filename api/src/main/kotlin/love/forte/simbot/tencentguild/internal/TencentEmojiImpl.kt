/*
 *  Copyright (c) 2022-2022 ForteScarlet <ForteScarlet@163.com>
 *
 *  根据 GNU LESSER GENERAL PUBLIC LICENSE 3 获得许可；
 *  除非遵守许可，否则您不得使用此文件。
 *  您可以在以下网址获取许可证副本：
 *
 *       https://www.gnu.org/licenses/lgpl-3.0-standalone.html
 *
 *   有关许可证下的权限和限制的具体语言，请参见许可证。
 */

package love.forte.simbot.tencentguild.internal

import kotlinx.serialization.Serializable
import love.forte.simbot.CharSequenceID
import love.forte.simbot.ID
import love.forte.simbot.tencentguild.TencentEmoji
import love.forte.simbot.tencentguild.emojiType
import love.forte.simbot.toCharSequenceID


/**
 *
 * @author ForteScarlet
 */
@Serializable
internal data class TencentEmojiImpl(override val id: CharSequenceID, override val type: TencentEmoji.Type) : TencentEmoji {
    constructor(id: ID, type: Int): this(id.toCharSequenceID(), emojiType(type))
}
