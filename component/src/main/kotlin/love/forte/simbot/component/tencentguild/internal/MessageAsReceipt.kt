package love.forte.simbot.component.tencentguild.internal

import love.forte.simbot.ID
import love.forte.simbot.message.MessageReceipt
import love.forte.simbot.tencentguild.TencentMessage

/**
 *
 * @author ForteScarlet
 */
@Suppress("MemberVisibilityCanBePrivate")
internal class MessageAsReceipt(val messageResult: TencentMessage) : MessageReceipt {
    override val id: ID
        get() = messageResult.id

    override val isSuccess: Boolean
        get() = true
}

internal fun TencentMessage.asReceipt(): MessageReceipt = MessageAsReceipt(this)