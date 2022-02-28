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
