package love.forte.simbot.component.tencentguild

import love.forte.simbot.Bot
import love.forte.simbot.ID
import love.forte.simbot.Timestamp
import love.forte.simbot.definition.Guild
import love.forte.simbot.definition.Member

/**
 *
 * @author ForteScarlet
 */
public interface TencentGuild : Guild {
    override val bot: Bot
    override val createTime: Timestamp
    override val currentMember: Int
    override val description: String
    override val icon: String
    override val id: ID
    override val maximumMember: Int
    override val name: String
    override val ownerId: ID
    override suspend fun owner(): Member
}