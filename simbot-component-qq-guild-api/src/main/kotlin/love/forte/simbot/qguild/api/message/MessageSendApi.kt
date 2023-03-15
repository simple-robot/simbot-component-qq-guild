/*
 * Copyright (c) 2022-2023. ForteScarlet.
 *
 * This file is part of simbot-component-qq-guild.
 *
 * simbot-component-qq-guild is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * simbot-component-qq-guild is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with simbot-component-qq-guild. If not, see <https://www.gnu.org/licenses/>.
 */

package love.forte.simbot.qguild.api.message

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
import love.forte.simbot.qguild.*
import love.forte.simbot.qguild.api.QQGuildApi
import love.forte.simbot.qguild.api.RouteInfoBuilder
import love.forte.simbot.qguild.api.SimplePostApiDescription
import love.forte.simbot.qguild.api.message.MessageSendApi.Body.Builder
import love.forte.simbot.qguild.model.Message
import love.forte.simbot.resources.*
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
 * ## 主动消息与被动消息
 * - 主动消息：发送消息时，未填充 `msg_id/event_id` 字段的消息。
 * - 被动消息：发送消息时，填充了 `msg_id/event_id` 字段的消息。`msg_id` 和 `event_id` 两个字段任意填一个即为被动消息。
 * 接口使用此 `msg_id/event_id` 拉取用户的消息或事件，同时判断用户消息或事件的发送时间，如果超过被动消息回复时效，将会不允许发送该消息。
 *
 * 更多参考 [文档](https://bot.q.qq.com/wiki/develop/api/openapi/message/post_messages.html#%E4%B8%BB%E5%8A%A8%E6%B6%88%E6%81%AF%E4%B8%8E%E8%A2%AB%E5%8A%A8%E6%B6%88%E6%81%AF)
 *
 * ## 发送 ARK 模板消息
 * 通过指定 `ark` 字段发送模板消息。
 *
 * - 要求操作人在该子频道具有发送消息和 对应 `ARK 模板` 的权限。
 * - 调用前需要先申请消息模板，这一步会得到一个模板 `id`，在请求时填在 `ark.template_id` 上。
 * - 发送成功之后，会触发一个创建消息的事件。
 * - 可用模板参考[可用模板](https://bot.q.qq.com/wiki/develop/api/openapi/message/message_template.html)。
 *
 * 更多参考 [文档](https://bot.q.qq.com/wiki/develop/api/openapi/message/post_ark_messages.html)
 *
 * ## 发送引用消息
 *
 * - 只支持引用机器人自己发送到的消息以及用户@机器人产生的消息。
 * - 发送成功之后，会触发一个创建消息的事件。
 *
 * 不能单独发送引用消息，引用消息需要和其他消息类型组合发送，参数请见[发送消息](https://bot.q.qq.com/wiki/develop/api/openapi/message/post_messages.html)。
 *
 * 更多参考 [文档](https://bot.q.qq.com/wiki/develop/api/openapi/message/post_messages_reference.html)
 *
 * ## 发送含有消息按钮组件的消息
 *
 * 通过指定 `keyboard` 字段发送带按钮的消息，支持 `keyboard 模版` 和 `自定义 keyboard` 两种请求格式。
 *
 * - 要求操作人在该子频道具有 `发送消息` 和 `对应消息按钮组件` 的权限。
 * - 请求参数 `keyboard 模版` 和 `自定义 keyboard` 只能单一传值。
 * - `keyboard 模版`
 *     - 调用前需要先申请消息按钮组件模板，这一步会得到一个模板 id，在请求时填在 `keyboard` 字段上。
 *     - 申请消息按钮组件模板需要提供响应的 json，具体格式参考 [InlineKeyboard](https://bot.q.qq.com/wiki/develop/api/openapi/message/message_keyboard.html#inlinekeyboard)。
 * - 仅 `markdown` 消息支持消息按钮。
 *
 * 更多参考 [文档](https://bot.q.qq.com/wiki/develop/api/openapi/message/post_keyboard_messages.html)
 *
 * ## 内嵌格式
 * 利用 `content` 字段发送内嵌格式的消息。
 *
 * - 内嵌格式仅在 `content` 中会生效，在 `Ark` 和 `Embed` 中不生效。
 * - 为了区分是文本还是内嵌格式，消息抄送和发送会对消息内容进行相关的转义。
 *
 * ### 转义内容
 *
 * | **源字符** | **转义后** |
 * |----------|----------|
 * | `&` | `&amp;` |
 * | `<` | `&lt;` |
 * | `>` | `&gt;` |
 *
 * 更多参考 [文档](https://bot.q.qq.com/wiki/develop/api/openapi/message/message_format.html)
 *
 * @author ForteScarlet
 */
public class MessageSendApi private constructor(
    channelId: String,
    body: Body, // TencentMessageForSending || MultiPartFormDataContent
) : QQGuildApi<Message>() {
    public companion object Factory : SimplePostApiDescription(
        "/channels/{channel_id}/messages"
    ) {
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
    }

    override val body: Any = body.toRealBody(defaultJson)

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
        /**
         * 选填，消息内容，文本内容，支持[内嵌格式](https://bot.q.qq.com/wiki/develop/api/openapi/message/message_format.html)
         */
        public val content: String?,

        /**
         * 选填，embed 消息，一种特殊的 ark，详情参考[Embed消息](https://bot.q.qq.com/wiki/develop/api/openapi/message/embed_message.html)
         */
        public val embed: Message.Embed?,
        /**
         * 选填，ark 消息
         */
        public val ark: Message.Ark?,
        /**
         * 选填，引用消息
         */
        public val messageReference: Message.Reference?,
        /**
         * 选填，图片url地址，平台会转存该图片，用于下发图片消息
         */
        public val image: String?,
        /**
         * 选填，要回复的消息id, 在 `AT_CREATE_MESSAGE` 事件中获取。
         */
        public val msgId: String?,
        /**
         * 选填，要回复的事件id, 在各事件对象中获取。
         */
        public val eventId: String?,
        /**
         * 选填，markdown 消息
         */
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
         * 各属性参考 [Body] 内同名属性。
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

            public fun appendContent(append: String) {
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
 * @return [MessageSendApi.Body] or [MultiPartFormDataContent]
 */
internal fun MessageSendApi.Body.toRealBody(json: Json): Any {
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

