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

package love.forte.simbot.tencentguild.api.message

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.util.cio.*
import io.ktor.utils.io.core.*
import io.ktor.utils.io.nio.*
import io.ktor.utils.io.streams.*
import kotlinx.serialization.*
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.CompositeEncoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import love.forte.simbot.resources.*
import love.forte.simbot.tencentguild.*
import love.forte.simbot.tencentguild.api.RouteInfoBuilder
import love.forte.simbot.tencentguild.api.TencentApi
import love.forte.simbot.tencentguild.api.message.MessageSendApi.Body.Builder
import love.forte.simbot.tencentguild.model.Message
import java.io.File
import java.net.URI
import java.net.URL
import java.nio.channels.FileChannel
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import kotlin.io.path.name


/**
 *
 * [发送消息](https://bot.q.qq.com/wiki/develop/api/openapi/message/post_messages.html)
 *
 * 用于向 `channel_id` 指定的子频道发送消息。
 *
 * - 要求操作人在该子频道具有 `发送消息` 的权限。
 * - 主动消息在频道主或管理设置了情况下，按设置的数量进行限频。在未设置的情况遵循如下限制:
 *     - 主动推送消息，默认每天往每个子频道可推送的消息数是 `20` 条，超过会被限制。
 *     - 主动推送消息在每个频道中，每天可以往 `2` 个子频道推送消息。超过后会被限制。
 * - 不论主动消息还是被动消息，在一个子频道中，每 `1s` 只能发送 `5` 条消息。
 * - 被动回复消息有效期为 `5` 分钟。超时会报错。
 * - 发送消息接口要求机器人接口需要连接到 websocket 上保持在线状态
 * - 有关主动消息审核，可以通过 [Intents](https://bot.q.qq.com/wiki/develop/api/gateway/intents.html)
 * 中审核事件 `MESSAGE_AUDIT` 返回 [MessageAudited](https://bot.q.qq.com/wiki/develop/api/openapi/message/model.html#messageaudited) 对象获取结果。
 *
 * <hr />
 *
 * 主动消息与被动消息
 * - 主动消息：发送消息时，未填充 `msg_id/event_id` 字段的消息。
 * - 被动消息：发送消息时，填充了 `msg_id/event_id` 字段的消息。`msg_id` 和 `event_id` 两个字段任意填一个即为被动消息。
 * 接口使用此 `msg_id/event_id` 拉取用户的消息或事件，同时判断用户消息或事件的发送时间，如果超过被动消息回复时效，将会不允许发送该消息。
 *
 * 更多参考 [文档](https://bot.q.qq.com/wiki/develop/api/openapi/message/post_messages.html#%E4%B8%BB%E5%8A%A8%E6%B6%88%E6%81%AF%E4%B8%8E%E8%A2%AB%E5%8A%A8%E6%B6%88%E6%81%AF)
 *
 * @author ForteScarlet
 */
public class MessageSendApi private constructor(
    channelId: String,
    private val _body: Body, // TencentMessageForSending || MultiPartFormDataContent
) : TencentApi<Message>() {
    public companion object Factory {
        /** 类似于 [io.ktor.serialization.kotlinx.json.DefaultJson] */
        internal val defaultJson: Json
            get() = Json {
                encodeDefaults = true
                isLenient = true
                allowSpecialFloatingPointValues = true
                allowStructuredMapKeys = true
                prettyPrint = false
                useArrayPolymorphism = false
            }

        /**
         * 构造 [MessageSendApi]
         */
        @JvmStatic
        public fun create(channelId: String, body: Body): MessageSendApi = MessageSendApi(channelId, body)
//
//        /**
//         * 构造 [MessageSendApi]
//         */
//        @JvmStatic
//        @JvmOverloads
//        public fun create(channelId: String, content: String, msgId: String? = null): MessageSendApi =
//            MessageSendApi(channelId, TencentMessageForSending(content = content, msgId = msgId))
//
//        /**
//         * 构造 [MessageSendApi]
//         */
//        @JvmStatic
//        @JvmOverloads
//        public fun create(channelId: String, embed: Message.Embed, msgId: String? = null): MessageSendApi =
//            MessageSendApi(
//                channelId,
//                TencentMessageForSending(embed = embed, msgId = msgId),
//            )
//
//        /**
//         * 构造 [MessageSendApi]
//         */
//        @JvmStatic
//        @JvmOverloads
//        public fun create(channelId: String, ark: Message.Ark, msgId: String? = null): MessageSendApi = MessageSendApi(
//            channelId,
//            TencentMessageForSending(ark = ark, msgId = msgId),
//        )
//
//        // with 'fileImage'
//
//        /**
//         * 构造 [MessageSendApi]
//         */
//        @JvmStatic
//        @JvmOverloads
//        public fun create(
//            channelId: String,
//            sendingBody: TencentMessageForSending,
//            fileImage: Resource? = null
//        ): MessageSendApi = MessageSendApi(
//            channelId = channelId,
//            body = if (fileImage != null) sendingBody.toMultiPartFormDataContent(
//                defaultJson,
//                fileImage
//            ) else sendingBody
//        )
//
//        /**
//         * 构造 [MessageSendApi]
//         */
//        @JvmStatic
//        public fun create(channelId: String, fileImage: Resource): MessageSendApi = MessageSendApi(
//            channelId = channelId,
//            body = null.toMultiPartFormDataContent(defaultJson, fileImage)
//        )
    }

    override val body: Any = _body.toRealBody(defaultJson)

    // POST /channels/{channel_id}/messages
    private val path = arrayOf("channels", channelId, "messages")

    override val resultDeserializer: DeserializationStrategy<Message>
        get() = Message.serializer()

    override val method: HttpMethod
        get() = HttpMethod.Post

    override fun route(builder: RouteInfoBuilder) {
        builder.apiPath = path
        if (body is MultiPartFormDataContent) {
            builder.contentType = ContentType.MultiPart.FormData
        }
    }

    /**
     * [MessageSendApi] 所需参数。详情参考 [文档](https://bot.q.qq.com/wiki/develop/api/openapi/message/post_messages.html#%E9%80%9A%E7%94%A8%E5%8F%82%E6%95%B0)
     *
     * [fileImage] 存在时将会使用 `multipart/form-data` 的形式发送，否则使用 `application/json`。
     *
     * 使用 [Builder] 构建。
     *
     */
    @Serializable
    public class Body private constructor(
        public val content: String?,
        public val embed: Message.Embed?,
        public val ark: Message.Ark?,
        public val messageReference: Message.Reference?,
        public val image: String?,
        public val msgId: String?,
        public val eventId: String?,
        public val markdown: Message.Markdown?,
    ) {

        /**
         * 可使用的类型：
         * - [ByteArray]
         * - [InputProvider]
         * - [ByteReadPacket]
         * - [ChannelProvider]
         * - [File]
         * - [Path]
         * - [URL]
         * - [URI]
         *
         */
        @Transient
        public var fileImage: Any? = null
            private set

        /**
         *
         * [Body] 的构建器。
         *
         * Kotlin 可以直接使用 [Body.invoke].
         *
         * Java:
         * ```java
         * var builder = new Builder();
         * builder.setXxx(...);
         * builder.setXxx(...);
         * var body = builder.build();
         * ```
         *
         */
        public class Builder {
            public var content: String? = null
            public var embed: Message.Embed? = null
            public var ark: Message.Ark? = null
            public var messageReference: Message.Reference? = null
            public var image: String? = null
            public var msgId: String? = null
            public var eventId: String? = null
            public var markdown: Message.Markdown? = null

            public fun contentAppend(append: String) {
                if (content == null) {
                    content = append
                } else {
                    content += append
                }
            }

            private var fileImage: Any? = null

            public fun setFileImage(byteArray: ByteArray) {
                fileImage = byteArray
            }

            public fun setFileImage(inputProvider: InputProvider) {
                fileImage = inputProvider
            }

            public fun setFileImage(byteReadPacket: ByteReadPacket) {
                fileImage = byteReadPacket
            }

            public fun setFileImage(channelProvider: ChannelProvider) {
                fileImage = channelProvider
            }

            public fun setFileImage(file: File) {
                fileImage = file
            }

            public fun setFileImage(path: Path) {
                fileImage = path
            }

            public fun setFileImage(url: URL) {
                fileImage = url
            }

            public fun setFileImage(uri: URI) {
                fileImage = uri
            }

            public fun build(): Body =
                Body(content, embed, ark, messageReference, image, msgId, eventId, markdown).apply {
                    this.fileImage = this@Builder.fileImage
                }
        }

        public companion object {

            /**
             * ```kotlin
             * val body = Body {
             *   // ...
             * }
             *```
             *
             */
            @JvmSynthetic
            public inline operator fun invoke(block: Builder.() -> Unit): Body = Builder().also(block).build()
        }


    }

}

// // TencentMessageForSending || MultiPartFormDataContent
/**
 *
 * @return MessageSendApi.Body || MultiPartFormDataContent
 */
private fun MessageSendApi.Body.toRealBody(json: Json): Any {
    if (fileImage == null) {
        return this
    }

    val formParts = formData {
        MessageSendApi.Body.serializer().serialize(
            FormDataDecoder(json.serializersModule, json, this), this@toRealBody
        )

        appendFileImage(fileImage)
    }


    return MultiPartFormDataContent(formParts)
}


/**
 * support:
 * - [ByteArray]
 * - [InputProvider]
 * - [io.ktor.utils.io.core.ByteReadPacket]
 * - [ChannelProvider]
 * - [File]
 * - [Path]
 * - [URL]
 * - [URI]
 */

private fun FormBuilder.appendFileImage(fileImage: Any?) {
    when (fileImage) {
        is ByteArray -> {
            val imgHeaders = Headers.build {
                append(HttpHeaders.ContentDisposition, "filename=\"image\"")
            }
            append(key = "file_image", fileImage, imgHeaders)
        }

        is InputProvider -> {
            val imgHeaders = Headers.build {
                append(HttpHeaders.ContentDisposition, "filename=\"image\"")
            }
            append(key = "file_image", fileImage, imgHeaders)
        }

        is ByteReadPacket -> {
            val imgHeaders = Headers.build {
                append(HttpHeaders.ContentDisposition, "filename=\"image\"")
            }
            append(key = "file_image", fileImage, imgHeaders)
        }

        is ChannelProvider -> {
            val imgHeaders = Headers.build {
                append(HttpHeaders.ContentDisposition, "filename=\"image\"")
            }
            append(key = "file_image", fileImage, imgHeaders)
        }

        is File -> {
            val imgHeaders = Headers.build {
                append(HttpHeaders.ContentDisposition, "filename=\"${fileImage.name}\"")
            }
            append(key = "file_image", ChannelProvider { fileImage.readChannel() }, imgHeaders)
        }

        is Path -> {
            val imgHeaders = Headers.build {
                append(HttpHeaders.ContentDisposition, "filename=\"${fileImage.name}\"")
            }
            append(
                key = "file_image",
                InputProvider { FileChannel.open(fileImage, StandardOpenOption.READ).asInput() },
                imgHeaders
            )
        }

        is URL -> {
            val imgHeaders = Headers.build {
                append(HttpHeaders.ContentDisposition, "filename=\"file\"")
            }
            append(key = "file_image", InputProvider { fileImage.openStream().asInput() }, imgHeaders)
        }

        is URI -> {
            val imgHeaders = Headers.build {
                append(HttpHeaders.ContentDisposition, "filename=\"file\"")
            }
            append(key = "file_image", InputProvider { fileImage.toURL().openStream().asInput() }, imgHeaders)
        }

        else -> {
            // do nothing
        }
    }

}


private fun FormBuilder.appendTencentMessageForSending(json: Json, value: TencentMessageForSending) {
    TencentMessageForSending.serializer().serialize(
        FormDataDecoder(json.serializersModule, json, this),
        value
    )
}

internal fun TencentMessageForSending?.toMultiPartFormDataContent(
    json: Json,
    fileImage: Resource,
): MultiPartFormDataContent {
    val formParts = formData {
        if (this@toMultiPartFormDataContent != null) {
            appendTencentMessageForSending(json, this@toMultiPartFormDataContent)
        }

        val imgHeaders = Headers.build {
            append(HttpHeaders.ContentDisposition, "filename=\"${fileImage.name}\"")
        }

        when (fileImage) {
            is FileResource -> {
                val file = fileImage.file
                append(key = "file_image", ChannelProvider { file.readChannel() }, imgHeaders)
            }

            is PathResource -> {
                val path = fileImage.path
                append(
                    key = "file_image",
                    InputProvider { FileChannel.open(path, StandardOpenOption.READ).asInput() },
                    imgHeaders
                )
            }

            is URLResource -> {
                val url = fileImage.url
                append(key = "file_image", InputProvider { url.openStream().asInput() }, imgHeaders)
            }

            is ByteArrayResource -> {
                append(key = "file_image", fileImage.bytes, imgHeaders)
            }

            else -> {
                append(key = "file_image", InputProvider { fileImage.openStream().asInput() }, imgHeaders)
            }
        }
    }

    return MultiPartFormDataContent(formParts)
}


@OptIn(ExperimentalSerializationApi::class)
private class FormDataDecoder(
    override val serializersModule: SerializersModule,
    private val json: Json,
    private val formBuilder: FormBuilder,
) : Encoder, CompositeEncoder {
    override fun encodeBooleanElement(descriptor: SerialDescriptor, index: Int, value: Boolean) {
        val name = descriptor.getElementName(index)
        formBuilder.append(name, value.toString())
    }

    override fun encodeByteElement(descriptor: SerialDescriptor, index: Int, value: Byte) {
        val name = descriptor.getElementName(index)
        formBuilder.append(name, value)
    }

    override fun encodeCharElement(descriptor: SerialDescriptor, index: Int, value: Char) {
        val name = descriptor.getElementName(index)
        formBuilder.append(name, value.toString())
    }

    override fun encodeDoubleElement(descriptor: SerialDescriptor, index: Int, value: Double) {
        val name = descriptor.getElementName(index)
        formBuilder.append(name, value)
    }

    override fun encodeFloatElement(descriptor: SerialDescriptor, index: Int, value: Float) {
        val name = descriptor.getElementName(index)
        formBuilder.append(name, value)
    }

    override fun encodeInlineElement(descriptor: SerialDescriptor, index: Int): Encoder {
        return this
    }

    override fun encodeIntElement(descriptor: SerialDescriptor, index: Int, value: Int) {
        val name = descriptor.getElementName(index)
        formBuilder.append(name, value)
    }

    override fun encodeLongElement(descriptor: SerialDescriptor, index: Int, value: Long) {
        val name = descriptor.getElementName(index)
        formBuilder.append(name, value)
    }

    @ExperimentalSerializationApi
    override fun <T : Any> encodeNullableSerializableElement(
        descriptor: SerialDescriptor,
        index: Int,
        serializer: SerializationStrategy<T>,
        value: T?,
    ) {
        if (value != null) {
            val name = descriptor.getElementName(index)
            formBuilder.append(name, json.encodeToString(serializer, value))
        }
    }

    override fun <T> encodeSerializableElement(
        descriptor: SerialDescriptor,
        index: Int,
        serializer: SerializationStrategy<T>,
        value: T,
    ) {
        encodeNullableSerializableElement(descriptor, index, serializer, value)
    }

    override fun encodeShortElement(descriptor: SerialDescriptor, index: Int, value: Short) {
        val name = descriptor.getElementName(index)
        formBuilder.append(name, value)
    }

    override fun encodeStringElement(descriptor: SerialDescriptor, index: Int, value: String) {
        val name = descriptor.getElementName(index)
        formBuilder.append(name, value)
    }

    override fun endStructure(descriptor: SerialDescriptor) {
    }

    override fun beginStructure(descriptor: SerialDescriptor): CompositeEncoder {
        return this
    }


    override fun encodeBoolean(value: Boolean) {
    }

    override fun encodeByte(value: Byte) {
    }

    override fun encodeChar(value: Char) {
    }

    override fun encodeDouble(value: Double) {
    }

    override fun encodeEnum(enumDescriptor: SerialDescriptor, index: Int) {
    }

    override fun encodeFloat(value: Float) {
    }

    override fun encodeInline(descriptor: SerialDescriptor): Encoder {
        return this
    }

    override fun encodeInt(value: Int) {
    }

    override fun encodeLong(value: Long) {
    }

    @ExperimentalSerializationApi
    override fun encodeNull() {
    }

    override fun encodeShort(value: Short) {
    }

    override fun encodeString(value: String) {
    }
}


//@Serializable
//internal data class SendMessageResult(
//    override val id: CharSequenceID,
//    @SerialName("channel_id")
//    override val channelId: CharSequenceID,
//    @SerialName("guild_id")
//    override val guildId: CharSequenceID,
//    override val content: String,
//    @Serializable(TimestampISO8601Serializer::class)
//    override val timestamp: Timestamp,
//    @SerialName("edited_timestamp")
//    override val editedTimestamp: Timestamp = Timestamp.NotSupport,
//    @SerialName("mention_everyone")
//    override val mentionEveryone: Boolean = false,
//    override val author: TencentUserInfoImpl,
//    override val attachments: List<Message.Attachment> = emptyList(),
//    override val embeds: List<Message.Embed> = emptyList(),
//    override val mentions: List<TencentUserInfoImpl> = emptyList(),
//    override val ark: Message.Ark? = null,
//    override val seqInChannel: String? = null,
//) : Message {
//    @Transient
//    override val member: AuthorAsMember = AuthorAsMember(guildId, author)
//}
//
//internal data class AuthorAsMember(
//    override val guildId: String?,
//    private val author: TencentUserInfoImpl,
//) : TencentMemberInfo {
//    override val user: TencentUserInfo get() = author
//    override val nick: String get() = ""
//    override val roleIds: List<ID> = listOf(TencentRoleInfo.DefaultRole.ALL_MEMBER.code.ID)
//    override val joinedAt: Timestamp
//        get() = Timestamp.NotSupport
//}
