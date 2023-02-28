package love.forte.simbot.tencentguild.model

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import love.forte.simbot.tencentguild.ApiModel

/**
 * [子频道对象(Channel)](https://bot.q.qq.com/wiki/develop/api/openapi/channel/model.html)
 *
 * 子频道对象中所涉及的 ID 类数据，都仅在机器人场景流通，与真实的 ID 无关。请不要理解为真实的 ID
 *
 */
@ApiModel
@Serializable
public data class Channel(
    /** 子频道 id */
    val id: String,
    /** 频道 id */
    @SerialName("guild_id") val guildId: String,
    /** 子频道名 */
    val name: String,
    /** 子频道类型 [ChannelType] */
    val type: ChannelType,
    /** 子频道子类型 [ChannelSubType] */
    @SerialName("sub_type") val subType: ChannelSubType,
    /**
     * 排序值，具体请参考 有[关 position 的说明](https://bot.q.qq.com/wiki/develop/api/openapi/channel/model.html#%E6%9C%89%E5%85%B3-position-%E7%9A%84%E8%AF%B4%E6%98%8E)
     *
     * - postiton 从 1 开始
     * - 当子频道类型为 子频道分组（ChannelType=4）时，由于 position 1 被未分组占用，所以 position 只能从 2 开始
     * - 如果不传默认追加到分组下最后一个
     * - 如果填写一个已经存在的值，那么会插入在原来的元素之前
     * - 如果填写一个较大值，与不填是相同的表现，同时存储的值会根据真实的 position 进行重新计算，并不会直接使用传入的值
     *
     */
    val position: Int,
    /** 所属分组 id，仅对子频道有效，对 子频道分组（ChannelType=4） 无效 */
    @SerialName("parent_id") val parentId: String,
    /** 创建人 id */
    @SerialName("owner_id") val ownerId: String,
    /** 子频道私密类型 [PrivateType] */
    @SerialName("private_type") val privateType: PrivateType,
    /** 子频道发言权限 [SpeakPermission] */
    @SerialName("speak_permission") val speakPermission: SpeakPermission,
    /** 用于标识应用子频道应用类型，仅应用子频道时会使用该字段，具体定义请参考 [应用子频道的应用类型](https://bot.q.qq.com/wiki/develop/api/openapi/channel/model.html#%E5%BA%94%E7%94%A8%E5%AD%90%E9%A2%91%E9%81%93%E7%9A%84%E5%BA%94%E7%94%A8%E7%B1%BB%E5%9E%8B) */
    @SerialName("application_id") val applicationId: String,
    /** 用户拥有的子频道权限 [Permissions] */
    val permissions: String // Permissions
) : Comparable<Channel> {
    override fun compareTo(other: Channel): Int = position.compareTo(other.position)
}

/**
 * [ChannelType](https://bot.q.qq.com/wiki/develop/api/openapi/channel/model.html#channeltype)
 *
 * 注：由于QQ频道还在持续的迭代中，经常会有新的子频道类型增加，文档更新不一定及时，开发者识别 ChannelType 时，请注意相关的未知 ID 的处理。
 *
 * 使用 [ChannelType.valueOf] 构建实例。
 */
@Serializable(ChannelTypeSerializer::class)
public class ChannelType private constructor(public val value: Int) {
    public companion object {
        /** 文字子频道 */
        @JvmField
        public val TEXT: ChannelType = ChannelType(0)

        /** 语音子频道 */
        @JvmField
        public val VOICE: ChannelType = ChannelType(2)

        /** 子频道分组 */
        @JvmField
        public val CATEGORY: ChannelType = ChannelType(4)

        /** 直播子频道 */
        @JvmField
        public val LIVE: ChannelType = ChannelType(10005)

        /** 应用子频道 */
        @JvmField
        public val APP: ChannelType = ChannelType(10006)

        /** 论坛子频道 */
        @JvmField
        public val FORUM: ChannelType = ChannelType(10007)

        private val known = arrayOf(TEXT, VOICE, CATEGORY, LIVE, APP, FORUM)

        /**
         * 判断当前 [ChannelType] 是否为一个目前（程序中）已知的类型。
         * 即是否为下述元素之一：
         * - [TEXT]
         * - [VOICE]
         * - [CATEGORY]
         * - [LIVE]
         * - [APP]
         * - [FORUM]
         *
         */
        @JvmStatic
        public val ChannelType.isKnown: Boolean get() = known.any { it.value == this.value }

        /**
         * 通过提供的 [value] 寻找或构建一个 [ChannelType].
         *
         */
        @JvmStatic
        public fun valueOf(type: Int): ChannelType = known.find { it.value == type } ?: ChannelType(type)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ChannelType) return false

        if (value != other.value) return false

        return true
    }

    override fun hashCode(): Int {
        return value
    }

    override fun toString(): String {
        return "ChannelType(value=$value)"
    }


}

/** Serializer for [ChannelType] */
private object ChannelTypeSerializer : KSerializer<ChannelType> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("ChannelType", PrimitiveKind.INT)

    override fun deserialize(decoder: Decoder): ChannelType = ChannelType.valueOf(decoder.decodeInt())

    override fun serialize(encoder: Encoder, value: ChannelType) {
        encoder.encodeInt(value.value)
    }
}


/**
 * [ChannelSubType](https://bot.q.qq.com/wiki/develop/api/openapi/channel/model.html#channelsubtype)
 *
 * 目前只有文字子频道具有 [ChannelSubType] 二级分类，同时二级分类也可能会不断增加，开发者也需要注意对未知 ID 的处理。
 *
 * 使用 [ChannelSubType.valueOf] 构建实例。
 *
 */
@Serializable(ChannelSubTypeSerializer::class)
public class ChannelSubType private constructor(public val value: Int) {
    public companion object {
        /** 闲聊 (value = 0) */
        @JvmField
        public val SMALL_TALK: ChannelSubType = ChannelSubType(0)

        /** 公告 (value = 1) */
        @JvmField
        public val ANNOUNCEMENT: ChannelSubType = ChannelSubType(1)

        /** 攻略 (value = 2) */
        @JvmField
        public val STRATEGY: ChannelSubType = ChannelSubType(2)

        /** 开黑 (value = 3) */
        @JvmField
        public val PLAY_TOGETHER: ChannelSubType = ChannelSubType(3)

        private val known = arrayOf(SMALL_TALK, ANNOUNCEMENT, STRATEGY, PLAY_TOGETHER)

        /**
         * 判断当前 [ChannelSubType] 是否为以下的已知类型：
         * - [闲聊][SMALL_TALK]
         * - [公告][ANNOUNCEMENT]
         * - [攻略][STRATEGY]
         * - [开黑][PLAY_TOGETHER]
         */
        @JvmStatic
        public val ChannelSubType.isKnown: Boolean get() = known.any { it.value == this.value }

        /**
         * 通过 [type] 寻找或构建一个 [ChannelSubType] 实例。
         */
        @JvmStatic
        public fun valueOf(type: Int): ChannelSubType = known.find { it.value == type } ?: ChannelSubType(type)
    }
}

/** Serializer for [ChannelSubType] */
private object ChannelSubTypeSerializer : KSerializer<ChannelSubType> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("ChannelSubType", PrimitiveKind.INT)
    override fun deserialize(decoder: Decoder): ChannelSubType = ChannelSubType.valueOf(decoder.decodeInt())
    override fun serialize(encoder: Encoder, value: ChannelSubType) {
        encoder.encodeInt(value.value)
    }
}

/**
 * [PrivateType](https://bot.q.qq.com/wiki/develop/api/openapi/channel/model.html#privatetype)
 *
 * 子频道私密类型
 */
@Serializable(PrivateTypeSerializer::class)
public enum class PrivateType(public val value: Int) {
    /** 公开频道 */
    PUBLIC(0),

    /** 群主管理员可见 */
    ADMIN_ONLY(1),

    /** 群主管理员+指定成员，可使用 [修改子频道权限接口](https://bot.q.qq.com/wiki/develop/api/openapi/channel_permissions/put_channel_permissions.html) 指定成员 */
    ADMIN_AND_DESIGNATED_MEMBER(2)
}

/** Serializer for [PrivateType] */
private object PrivateTypeSerializer : KSerializer<PrivateType> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("PrivateType", PrimitiveKind.INT)

    override fun deserialize(decoder: Decoder): PrivateType = when (val value = decoder.decodeInt()) {
        PrivateType.PUBLIC.value -> PrivateType.PUBLIC
        PrivateType.ADMIN_ONLY.value -> PrivateType.ADMIN_ONLY
        PrivateType.ADMIN_AND_DESIGNATED_MEMBER.value -> PrivateType.ADMIN_AND_DESIGNATED_MEMBER
        else -> throw NoSuchElementException("PrivateType value of $value")
    }

    override fun serialize(encoder: Encoder, value: PrivateType) {
        encoder.encodeInt(value.value)
    }
}


/**
 * [SpeakPermission](https://bot.q.qq.com/wiki/develop/api/openapi/channel/model.html#speakpermission)
 *
 * 子频道发言权限
 *
 */
@Serializable(SpeakPermissionSerializer::class)
public enum class SpeakPermission(public val value: Int) {
    /** 无效类型 */
    INVALID(0),

    /** 所有人 */
    ALL(1),

    /** 群主管理员+指定成员，可使用 [修改子频道权限接口](https://bot.q.qq.com/wiki/develop/api/openapi/channel_permissions/put_channel_permissions.html) 指定成员 */
    ADMIN_AND_DESIGNATED_MEMBER(2)
}

/** Serializer for [SpeakPermission] */
private object SpeakPermissionSerializer : KSerializer<SpeakPermission> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("SpeakPermission", PrimitiveKind.INT)

    override fun deserialize(decoder: Decoder): SpeakPermission = when (val value = decoder.decodeInt()) {
        SpeakPermission.INVALID.value -> SpeakPermission.INVALID
        SpeakPermission.ALL.value -> SpeakPermission.ALL
        SpeakPermission.ADMIN_AND_DESIGNATED_MEMBER.value -> SpeakPermission.ADMIN_AND_DESIGNATED_MEMBER
        else -> throw NoSuchElementException("SpeakPermission value of $value")
    }

    override fun serialize(encoder: Encoder, value: SpeakPermission) {
        encoder.encodeInt(value.value)
    }
}




