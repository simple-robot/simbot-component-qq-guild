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

package love.forte.simbot.qguild

import kotlinx.serialization.*
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.*
import love.forte.simbot.*
import love.forte.simbot.qguild.internal.*


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
