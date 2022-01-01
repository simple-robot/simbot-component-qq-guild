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
