package love.forte.simbot.component.tencentguild.internal

import love.forte.simbot.Api4J
import love.forte.simbot.ID
import love.forte.simbot.Timestamp
import love.forte.simbot.component.tencentguild.TencentGuildComponentBot
import love.forte.simbot.component.tencentguild.TencentGuildComponentGuildMemberBot
import love.forte.simbot.component.tencentguild.TencentMember
import love.forte.simbot.definition.UserStatus
import love.forte.simbot.message.Message
import love.forte.simbot.message.MessageReceipt


/**
 *
 * @author ForteScarlet
 */
internal class TencentGuildComponentGuildMemberBotImpl(
    override val bot: TencentGuildComponentBot,
    private val member: TencentMember,
) : TencentGuildComponentGuildMemberBot, TencentGuildComponentBot by bot, TencentMember by member {
    
    override val id: ID
        get() = member.id
    override val avatar: String
        get() = member.avatar
    override val username: String
        get() = member.username
    override val status: UserStatus
        get() = member.status
    override val joinTime: Timestamp
        get() = member.joinTime
    
    @Api4J
    override fun sendIfSupportBlocking(message: Message): MessageReceipt {
        return member.sendBlocking(message)
    }
    
    
}

internal fun TencentGuildComponentBot.asMember(member: TencentMember): TencentGuildComponentGuildMemberBotImpl {
    return TencentGuildComponentGuildMemberBotImpl(this, member)
}