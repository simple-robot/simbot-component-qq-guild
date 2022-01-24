/*
 *  Copyright (c) 2022-2022 ForteScarlet <ForteScarlet@163.com>
 *
 *  根据 GNU LESSER GENERAL PUBLIC LICENSE 3 获得许可；
 *  除非遵守许可，否则您不得使用此文件。
 *  您可以在以下网址获取许可证副本：
 *
 *       https://www.gnu.org/licenses/lgpl-3.0-standalone.html
 *
 *   有关许可证下的权限和限制的具体语言，请参见许可证。
 */

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