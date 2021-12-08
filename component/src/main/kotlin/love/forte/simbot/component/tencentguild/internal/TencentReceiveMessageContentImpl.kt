package love.forte.simbot.component.tencentguild.internal

import love.forte.simbot.component.tencentguild.message.TencentReceiveMessageContent
import love.forte.simbot.message.Messages
import love.forte.simbot.tencentguild.TencentMessage

/**
 *
 * @author ForteScarlet
 */

/**
 *
 * @author ForteScarlet
 */
internal class TencentReceiveMessageContentImpl(sourceMessage: TencentMessage) : TencentReceiveMessageContent() {

    override val metadata: Metadata = Metadata(
        sourceMessage.id,
        sourceMessage.channelId,
        sourceMessage.guildId,
        sourceMessage.timestamp
    )

    override val messages: Messages by lazy(LazyThreadSafetyMode.NONE) { MessageParsers.parse(sourceMessage) }


    override val plainText: String by lazy(LazyThreadSafetyMode.NONE) {
        var content = sourceMessage.content

        for (mention in sourceMessage.mentions) {
            val target = "<@!${mention.id}>"
            content = content.replaceFirst(target, "")
        }

        if (sourceMessage.mentionEveryone) {
            content = content.replaceFirst("@everyone", "")
        }

        content
    }
    // get() = super.plainText // maybe without mention string

    override fun toString(): String {
        return "TencentReceiveMessageContent(metadata=$metadata)"
    }

    override fun hashCode(): Int = metadata.hashCode()

    override fun equals(other: Any?): Boolean {
        if (other !is TencentReceiveMessageContent) return false
        return metadata == other.metadata
    }

}