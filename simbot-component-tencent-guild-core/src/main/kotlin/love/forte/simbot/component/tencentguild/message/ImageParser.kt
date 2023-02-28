package love.forte.simbot.component.tencentguild.message

import io.ktor.client.request.forms.*
import io.ktor.utils.io.streams.*
import love.forte.simbot.component.tencentguild.internal.SendingMessageParser
import love.forte.simbot.message.Image
import love.forte.simbot.message.Message
import love.forte.simbot.message.Messages
import love.forte.simbot.resources.ByteArrayResource
import love.forte.simbot.resources.FileResource
import love.forte.simbot.resources.PathResource
import love.forte.simbot.resources.URLResource
import love.forte.simbot.tencentguild.api.message.MessageSendApi


/**
 *
 * @author ForteScarlet
 */
public object ImageParser : SendingMessageParser {
    override suspend fun invoke(
        index: Int,
        element: Message.Element<*>,
        messages: Messages?,
        builder: MessageSendApi.Body.Builder,
    ) {
        when (element) {
            is Image -> {
                when (val resource = element.resource()) {
                    is FileResource -> {
                        builder.setFileImage(resource.file)
                    }
                    is PathResource -> {
                        builder.setFileImage(resource.path)
                    }
                    is ByteArrayResource -> {
                        builder.setFileImage(resource.bytes)
                    }
                    is URLResource -> {
                        builder.image = resource.url.toString()
                    }
                    else -> {
                        builder.setFileImage(InputProvider { resource.openStream().asInput() })
                    }
                }
            }

            // TODO more image type support for file_image
        }
    }
}
