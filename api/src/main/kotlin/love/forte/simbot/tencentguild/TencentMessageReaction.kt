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

package love.forte.simbot.tencentguild

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import love.forte.simbot.ID
import love.forte.simbot.tencentguild.internal.TencentMessageReactionImpl
import love.forte.simbot.tencentguild.internal.TencentMessageReactionTargetImpl


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
