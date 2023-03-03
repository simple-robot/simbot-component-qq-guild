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

@file:UseSerializers(InstantISO8601Serializer::class)

package love.forte.simbot.qguild.model

import kotlinx.serialization.*
import love.forte.simbot.*
import love.forte.simbot.qguild.ApiModel
import love.forte.simbot.qguild.utils.InstantISO8601Serializer
import java.time.Instant

// TODO Fields [edited_timestamp, mention_everyone, attachments, embeds, ark, src_guild_id]

/**
 * [消息对象(Message)](https://bot.q.qq.com/wiki/develop/api/openapi/message/model.html)
 *
 * 消息对象的完整定义。
 *
 */
@ApiModel
@Serializable
public data class Message(
    /**
     * 消息 id
     */
    public val id: String,

    /**
     * 子频道 id
     */
    @SerialName("channel_id") public val channelId: String,

    /**
     * 频道 id
     */
    @SerialName("guild_id") public val guildId: String,

    /**
     * 消息内容
     */
    public val content: String,

    /**
     * ISO8601 timestamp	消息创建时间
     */
    public val timestamp: Instant,

    /**
     * ISO8601 timestamp	消息编辑时间
     */
    @SerialName("edited_timestamp") public val editedTimestamp: Instant? = null,

    /**
     * 是否是@全员消息
     */
    @SerialName("mention_everyone") public val mentionEveryone: Boolean = false,

    /**
     * 消息创建者
     */
    public val author: User,

    /**
     * MessageAttachment 对象数组	附件
     */
    public val attachments: List<Attachment> = emptyList(),

    /**
     * embeds	MessageEmbed 对象数组	embed
     */
    public val embeds: List<Embed> = emptyList(),

    /**
     * User 对象数组	消息中@的人
     */
    public val mentions: List<User> = emptyList(),

    /**
     * Member 对象	消息创建者的member信息
     */
    public val member: SimpleMember,

    /**
     * ark消息对象	ark消息
     */
    public val ark: Ark? = null,

    /**
     * `seq_in_channel` string
     *
     * 子频道消息 seq，用于消息间的排序，seq 在同一子频道中按从先到后的顺序递增，不同的子频道之间消息无法排序
     */
    @SerialName("seq_in_channel") public val seqInChannel: Long,

    // TODO message_reference

    /**
     * 用于私信场景下识别真实的来源频道id
     */
    @SerialName("src_guild_id") val srcGuildId: String? = null

) {


    /**
     * [MessageEmbed](https://bot.q.qq.com/wiki/develop/api/openapi/message/model.html#messageembed)
     */
    @Serializable
    public data class Embed(
        /**
         * 标题
         */
        val title: String,

        /**
         * 消息弹窗内容
         */
        val prompt: String,

        /**
         * 缩略图
         */
        val thumbnail: Thumbnail,

        /**
         * 字段信息
         */
        val fields: List<Field> = emptyList()
    ) {

        /**
         * [MessageEmbedThumbnail](https://bot.q.qq.com/wiki/develop/api/openapi/message/model.html#messageembedthumbnail)
         *
         */
        @Serializable
        public data class Thumbnail(
            /**
             * 图片地址
             */
            val url: String
        )

        /**
         * [MessageEmbedField](https://bot.q.qq.com/wiki/develop/api/openapi/message/model.html#messageembedfield)
         */
        @Serializable
        public data class Field(
            /**
             * 字段名
             */
            public val name: String,

            /**
             * 字段值
             * // TODO 似乎已经不存在了
             */
            public val value: String,
        )
    }


    /**
     * [MessageAttachment](https://bot.q.qq.com/wiki/develop/api/openapi/message/model.html#messageattachment)
     */
    @Serializable
    public data class Attachment(
        /**
         * 下载地址
         */
        public val url: String
    )


    /**
     * [MessageArk](https://bot.q.qq.com/wiki/develop/api/openapi/message/model.html#messageark)
     */
    @Serializable
    public data class Ark(
        /**
         * ark模板id（需要先申请）
         */
        @SerialName("template_id") public val templateId: String,

        /**
         * kv值列表
         */
        public val kv: List<Kv> = emptyList()
    ) {

        /**
         * [MessageArkKv](https://bot.q.qq.com/wiki/develop/api/openapi/message/model.html#messagearkkv)
         */
        @Serializable
        public data class Kv(
            public val key: String, public val value: String? = null, public val obj: List<Obj> = emptyList()
        )

        /**
         * [MessageArkObj](https://bot.q.qq.com/wiki/develop/api/openapi/message/model.html#messagearkobj)
         */
        @Serializable
        public data class Obj(
            /** ark obj kv列表 */
            @SerialName("obj_kv") public val objKv: List<Kv> = emptyList()
        ) {

            /**
             * [MessageArkObjKv](https://bot.q.qq.com/wiki/develop/api/openapi/message/model.html#messagearkobjkv)
             */
            @Serializable
            public data class Kv(public val key: String, public val value: String)
        }


    }

    /**
     * [MessageReference](https://bot.q.qq.com/wiki/develop/api/openapi/message/model.html#messagereference)
     */
    @Serializable
    public data class Reference(
        /**
         * 需要引用回复的消息 id
         */
        @SerialName("message_id") val messageId: String,

        /**
         * 是否忽略获取引用消息详情错误，默认否
         */
        @SerialName("ignore_get_message_error") val ignoreGetMessageError: Boolean = false
    )

    /**
     * [MessageMarkdown](https://bot.q.qq.com/wiki/develop/api/openapi/message/model.html#messagemarkdown)
     */
    @Serializable
    public data class Markdown(
        /**
         * markdown 模板 id
         */
        @SerialName("template_id") val templateId: Int?,

        /**
         * markdown 自定义模板 id
         */
        @SerialName("custom_template_id") val customTemplateId: String?,

        /**
         * markdown 模板模板参数
         */
        val params: Params?,

        /**
         * 原生 markdown 内容,与上面三个参数互斥,参数都传值将报错。
         */
        val content: String? = null,
    ) {

        /**
         * [MessageMarkdownParams](https://bot.q.qq.com/wiki/develop/api/openapi/message/model.html#MessageMarkdownParams)
         */
        @Serializable
        public data class Params(
            /**
             * markdown 模版 key
             */
            val key: String,
            /**
             * markdown 模版 [key] 对应的 `values` .
             *
             * > 列表长度大小为 1，传入多个会报错
             *
             * _但代码层面暂时不做验证限制。_
             *
             */
            val values: List<String>
        )
    }


}



