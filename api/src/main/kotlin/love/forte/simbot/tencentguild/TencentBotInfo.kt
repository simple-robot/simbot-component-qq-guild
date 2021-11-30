package love.forte.simbot.tencentguild

import kotlinx.serialization.KSerializer
import love.forte.simbot.BotInfo
import love.forte.simbot.ID


/**
 * Bot信息
 *
 * @author ForteScarlet
 */
public interface TencentBotInfo : TencentUserInfo, BotInfo {
    override val id: ID
    override val username: String
    override val avatar: String
    override val isBot: Boolean
    override val unionOpenid: String?
    override val unionUserAccount: String?
    public companion object {
        internal val serializer: KSerializer<out TencentBotInfo> = TencentBotInfoImpl.serializer()
    }
}