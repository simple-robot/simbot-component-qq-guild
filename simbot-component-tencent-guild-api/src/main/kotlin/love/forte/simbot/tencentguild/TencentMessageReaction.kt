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

package love.forte.simbot.tencentguild

import kotlinx.serialization.*
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.*
import love.forte.simbot.*
import love.forte.simbot.tencentguild.internal.*


/**
 * [表情表态对象](https://bot.q.qq.com/wiki/develop/api/openapi/reaction/model.html)
 */
public interface TencentMessageReaction {

    /**
     * 用户ID
     */
    public val userId: ID

    /**
     * 频道ID
     */
    public val guildId: ID

    /**
     * 子频道ID
     */
    public val channelId: ID

    /**
     * 表态对象
     */
    public val target: Target

    /**
     * 表态所用表情
     * @see [TencentEmoji]
     */
    public val emoji: TencentEmoji

    public companion object {
        public val serializer: KSerializer<out TencentMessageReaction> = TencentMessageReactionImpl.serializer()
    }

    /**
     * [表态对象](https://bot.q.qq.com/wiki/develop/api/openapi/reaction/model.html#reactiontarget)
     */
    public interface Target {

        /**
         * 表态对象ID
         */
        public val id: ID

        /**
         * 表态对象类型
         */
        public val type: TargetType

        public companion object {
            public val serializer: KSerializer<out Target> = TencentMessageReactionTargetImpl.serializer()
        }
    }

    /**
     * [表态对象类型](https://bot.q.qq.com/wiki/develop/api/openapi/reaction/model.html#reactiontargettype)
     */
    @Serializable(TargetType.Serializer::class)
    public enum class TargetType(public val type: Int) {
        /** 消息 */
        MESSAGE(0),

        /** 帖子 */
        POST(1),

        /** 评论 */
        COMMENT(2),

        /** 回复 */
        REPLY(3);

        public object Serializer : KSerializer<TargetType> {
            override fun deserialize(decoder: Decoder): TargetType = messageReactionTargetType(decoder.decodeInt())
            override val descriptor: SerialDescriptor =
                PrimitiveSerialDescriptor("messageReaction.targetType", PrimitiveKind.INT)

            override fun serialize(encoder: Encoder, value: TargetType) {
                encoder.encodeInt(value.type)
            }
        }
    }
}


public fun messageReactionTargetType(type: Int): TencentMessageReaction.TargetType =
    TencentMessageReaction.TargetType.values().find { it.type == type }
        ?: throw IllegalArgumentException("Unknown reaction target type type: $type")
