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

package love.forte.simbot.tencentguild.api.message

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.util.cio.*
import io.ktor.utils.io.nio.*
import io.ktor.utils.io.streams.*
import kotlinx.serialization.*
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.CompositeEncoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import love.forte.simbot.CharSequenceID
import love.forte.simbot.ID
import love.forte.simbot.Timestamp
import love.forte.simbot.literal
import love.forte.simbot.resources.*
import love.forte.simbot.tencentguild.*
import love.forte.simbot.tencentguild.api.RouteInfoBuilder
import love.forte.simbot.tencentguild.api.TencentApi
import love.forte.simbot.tencentguild.internal.TencentUserInfoImpl
import java.nio.channels.FileChannel
import java.nio.file.StandardOpenOption


/**
 *
 * [发送消息](https://bot.q.qq.com/wiki/develop/api/openapi/message/post_messages.html)
 *
 * - 要求操作人在该子频道具有发送消息的权限。
 * - 发送成功之后，会触发一个创建消息的事件。
 * - 被动回复消息有效期为 5 分钟
 * - 主动推送消息每日每个子频道限 2 条
 * - 发送消息接口要求机器人接口需要链接到websocket gateway 上保持在线状态
 *
 * <hr>
 *
 * [发送模板消息](https://bot.q.qq.com/wiki/develop/api/openapi/message/post_ark_messages.html)
 *
 * - 要求操作人在该子频道具有发送消息和 模板消息 的权限。
 * - 调用前需要先申请消息模板，这一步会得到一个模板id，在请求时填在ark.template_id上
 * - 发送成功之后，会触发一个创建消息的事件。
 * - 可用模板参考 [可用模板](https://bot.q.qq.com/wiki/develop/api/openapi/message/message_template.html)
 *
 * > content, embed, ark, image/MessageSendApi.fileImage, markdown
 *
 * @author ForteScarlet
 */
public class MessageSendApi private constructor(
    channelId: ID,
    override val body: Any, // TencentMessageForSending || MultiPartFormDataContent
) : TencentApi<TencentMessage>() {
    public companion object {
        internal val defaultJson: Json get() = DefaultJson
    }
    
    
    @JvmOverloads
    public constructor(channelId: ID, content: String, msgId: ID? = null) : this(
        channelId,
        TencentMessageForSending(content = content, msgId = msgId),
    )
    
    @JvmOverloads
    public constructor(channelId: ID, embed: TencentMessage.Embed, msgId: ID? = null) : this(
        channelId,
        TencentMessageForSending(embed = embed, msgId = msgId),
    )
    
    @JvmOverloads
    public constructor(channelId: ID, ark: TencentMessage.Ark, msgId: ID? = null) : this(
        channelId,
        TencentMessageForSending(ark = ark, msgId = msgId),
    )
    
    
    // with 'fileImage'
    
    @JvmOverloads
    public constructor(channelId: ID, sendingBody: TencentMessageForSending, fileImage: Resource? = null) : this(
        channelId = channelId,
        body = if (fileImage != null) sendingBody.toMultiPartFormDataContent(defaultJson, fileImage) else sendingBody
    )
    
    
    public constructor(channelId: ID, fileImage: Resource) : this(
        channelId = channelId,
        body = null.toMultiPartFormDataContent(defaultJson, fileImage)
    )
    
    
    // POST /channels/{channel_id}/messages
    private val path: List<String> = listOf("channels", channelId.literal, "messages")
    
    override val resultDeserializer: DeserializationStrategy<out TencentMessage>
        get() = SendMessageResult.serializer()
    
    override val method: HttpMethod
        get() = HttpMethod.Post
    
    override fun route(builder: RouteInfoBuilder) {
        builder.apiPath = path
        if (body is MultiPartFormDataContent) {
            builder.contentType = ContentType.MultiPart.FormData
        }
    }
    
    
}

private fun FormBuilder.appendTencentMessageForSending(json: Json, value: TencentMessageForSending) {
    TencentMessageForSending.serializer().serialize(
        TencentMessageForSendingFormDataDecoder(json.serializersModule, json, this),
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
private class TencentMessageForSendingFormDataDecoder(
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
    
    @ExperimentalSerializationApi
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
    
    @ExperimentalSerializationApi
    override fun encodeInline(inlineDescriptor: SerialDescriptor): Encoder {
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


@Serializable
internal data class SendMessageResult(
    override val id: CharSequenceID,
    @SerialName("channel_id")
    override val channelId: CharSequenceID,
    @SerialName("guild_id")
    override val guildId: CharSequenceID,
    override val content: String,
    @Serializable(TimestampISO8601Serializer::class)
    override val timestamp: Timestamp,
    @SerialName("edited_timestamp")
    override val editedTimestamp: Timestamp = Timestamp.NotSupport,
    @SerialName("mention_everyone")
    override val mentionEveryone: Boolean = false,
    override val author: TencentUserInfoImpl,
    override val attachments: List<TencentMessage.Attachment> = emptyList(),
    override val embeds: List<TencentMessage.Embed> = emptyList(),
    override val mentions: List<TencentUserInfoImpl> = emptyList(),
    override val ark: TencentMessage.Ark? = null,
    override val seqInChannel: String? = null,
) : TencentMessage {
    @Transient
    override val member: AuthorAsMember = AuthorAsMember(guildId, author)
}

internal data class AuthorAsMember(
    override val guildId: ID?,
    private val author: TencentUserInfoImpl,
) : TencentMemberInfo {
    override val user: TencentUserInfo get() = author
    override val nick: String get() = ""
    override val roleIds: List<ID> = listOf(TencentRoleInfo.DefaultRole.ALL_MEMBER.code.ID)
    override val joinedAt: Timestamp
        get() = Timestamp.NotSupport
}