package love.forte.simbot.component.tencentguild.message

import love.forte.simbot.component.tencentguild.internal.SendingMessageParser
import love.forte.simbot.component.tencentguild.internal.TencentMessageForSendingForParse
import love.forte.simbot.literal
import love.forte.simbot.message.Image
import love.forte.simbot.message.Message
import love.forte.simbot.message.Messages
import love.forte.simbot.message.ResourceImage


/**
 *
 * @author ForteScarlet
 */
public object ImageParser : SendingMessageParser {
    override suspend fun invoke(
        index: Int,
        element: Message.Element<*>,
        messages: Messages?,
        builder: TencentMessageForSendingForParse,
    ) {
        when (element) {
            is ResourceImage -> {
                builder.fileImage = element.resource()
            }
            is Image -> {
                builder.forSending.image = element.id.literal
            }
        }
    }
}