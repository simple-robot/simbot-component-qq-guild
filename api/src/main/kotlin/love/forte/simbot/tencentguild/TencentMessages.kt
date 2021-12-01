package love.forte.simbot.tencentguild

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encoding.Decoder
import love.forte.simbot.ID
import love.forte.simbot.Timestamp
import love.forte.simbot.tencentguild.internal.TencentMessageImpl


/**
 * [消息对象(Message)](https://bot.q.qq.com/wiki/develop/api/openapi/message/model.html)
 *
 * 消息对象的完整定义, 是用于接收的消息。
 *
 */
public interface TencentMessage {
    /**
     * 消息 id
     */
    public val id: ID

    /**
     * 子频道 id
     */
    public val channelId: ID

    /**
     * 频道 id
     */
    public val guildId: ID

    /**
     * 消息内容
     */
    public val content: String

    /**
     * ISO8601 timestamp	消息创建时间
     */
    public val timestamp: Timestamp

    /**
     * ISO8601 timestamp	消息编辑时间
     */
    public val editedTimestamp: Timestamp

    /**
     * 是否是@全员消息
     */
    public val mentionEveryone: Boolean

    /**
     * 消息创建者
     */
    public val author: TencentUserInfo

    /**
     * MessageAttachment 对象数组	附件
     */
    public val attachments: List<Attachment>

    /**
     * embeds	MessageEmbed 对象数组	embed
     */
    public val embeds: List<Embed>

    /**
     * User 对象数组	消息中@的人
     */
    public val mentions: List<TencentUserInfo>

    /**
     * Member 对象	消息创建者的member信息
     */
    public val member: TencentMemberInfo

    /**
     * ark消息对象	ark消息
     */
    public val ark: Ark?


    /**
     * [https://bot.q.qq.com/wiki/develop/api/openapi/message/model.html#messageembed]
     */
    @Serializable
    public data class Embed(
        /**
         * 标题
         */
        public val title: String,

        /**
         * 描述
         */
        public val description: String,

        /**
         * 消息弹窗内容
         */
        public val prompt: String,

        /**
         * iISO8601 消息创建时间
         */
        public val timestamp: Timestamp,

        /**
         * MessageEmbedField 对象数组	字段信息
         */
        public val fields: List<Field>
    ) {

        /**
         * [https://bot.q.qq.com/wiki/develop/api/openapi/message/model.html#messageembedfield]
         */
        @Serializable
        public data class Field(
            /**
             * 字段名
             */
            public val name: String,

            /**
             * 字段值
             */
            public val value: String,
        )
    }


    /**
     * [https://bot.q.qq.com/wiki/develop/api/openapi/message/model.html#messageattachment]
     */
    @Serializable
    public data class Attachment(
        /**
         * 下载地址
         */
        public val url: String
    )


    /**
     * [https://bot.q.qq.com/wiki/develop/api/openapi/message/model.html#messageark]
     */
    @Serializable
    public data class Ark(
        /**
         * ark模板id（需要先申请）
         */
        @SerialName("template_id")
        @Serializable(ID.AsCharSequenceIDSerializer::class)
        public val templateId: ID,

        /**
         * kv值列表
         */
        public val kv: List<Kv> = emptyList()
    ) {


        /**
         * [https://bot.q.qq.com/wiki/develop/api/openapi/message/model.html#messagearkkv]
         */
        @Serializable
        public data class Kv(
            public val key: String,
            public val value: String? = null,
            public val obj: List<Obj> = emptyList()
        )


        /**
         * [https://bot.q.qq.com/wiki/develop/api/openapi/message/model.html#messagearkobj]
         */
        @Serializable
        public data class Obj(
            /** ark obj kv列表 */
            @SerialName("obj_kv")
            public val objKv: List<Kv> = emptyList()
        ) {

            /**
             * https://bot.q.qq.com/wiki/develop/api/openapi/message/model.html#messagearkobjkv
             */
            @Serializable
            public data class Kv(public val key: String, public val value: String)
        }

    }



    public companion object {
        internal val serializer: KSerializer<out TencentMessage> = TencentMessageImplSerializer(TencentMessageImpl.serializer())
        //internal val sendSerializer: KSerializer<out TencentMessage> = TencentMessageImplSerializer(TencentMessageImpl.serializer())
    }
}

/**
 * 反序列化后进行后处理。
 */
private class TencentMessageImplSerializer(private val delegate: KSerializer<TencentMessageImpl>) : KSerializer<TencentMessageImpl> by delegate {
    override fun deserialize(decoder: Decoder): TencentMessageImpl {
        val instance = delegate.deserialize(decoder)
        instance.member.user = instance.author
        return instance
    }
}


