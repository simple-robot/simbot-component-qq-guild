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

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import love.forte.simbot.CharSequenceID
import love.forte.simbot.ID
import love.forte.simbot.tencentguild.internal.TencentEmojiImpl
import love.forte.simbot.toCharSequenceID


/**
 * [表情对象](https://bot.q.qq.com/wiki/develop/api/openapi/emoji/model.html)
 *
 *
 * ### Emoji 列表
 * 列表仅包含部分表情，随着时间推移，可能会与线上情况相比有所增减。建议开发者如果要基于表情表态来做逻辑，优先针对已知的QQ系统表情类型（EmojiType=1）来处理。
 * see: [Emoji 列表](https://bot.q.qq.com/wiki/develop/api/openapi/emoji/model.html#emoji-%E5%88%97%E8%A1%A8)
 *
 * @see TencentEmoji function
 */
public interface TencentEmoji {

    /**
     * 表情ID，系统表情使用数字为ID，emoji使用emoji本身为id，参考 Emoji 列表
     */
    public val id: CharSequenceID


    /**
     * 表情类型 EmojiType
     */
    public val type: Type

    public companion object {
        public val serializer: KSerializer<out TencentEmoji> = TencentEmojiImpl.serializer()

        /**
         * for java static method
         */
        @JvmStatic
        @JvmOverloads
        public fun newInstance(id: ID, type: Type = Type.SYSTEM): TencentEmoji = TencentEmoji(id, type)

        /**
         * for java static method
         */
        @JvmStatic
        public fun newInstance(id: ID, type: Int): TencentEmoji = TencentEmoji(id, type)

    }

    /**
     * 表情类型 EmojiType
     *
     */
    @Serializable(Type.Serializer::class)
    public enum class Type(public val type: Int) {
        /** 系统表情 */
        SYSTEM(1),

        /** emoji表情 */
        EMOJI(2);

        public object Serializer : KSerializer<Type> {
            override fun deserialize(decoder: Decoder): Type {
                return emojiType(decoder.decodeInt())
            }

            override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("emoji.type", PrimitiveKind.INT)

            override fun serialize(encoder: Encoder, value: Type) {
                encoder.encodeInt(value.type)
            }
        }
    }
}

public fun TencentEmoji(id: ID, type: TencentEmoji.Type = TencentEmoji.Type.SYSTEM): TencentEmoji =
    TencentEmojiImpl(id.toCharSequenceID(), type)

public fun TencentEmoji(id: ID, type: Int): TencentEmoji = TencentEmojiImpl(id.toCharSequenceID(), type)

public fun emojiType(type: Int): TencentEmoji.Type = when (type) {
    1 -> TencentEmoji.Type.SYSTEM
    2 -> TencentEmoji.Type.EMOJI
    else -> throw IllegalArgumentException("Unknown emoji type value: $type")
}