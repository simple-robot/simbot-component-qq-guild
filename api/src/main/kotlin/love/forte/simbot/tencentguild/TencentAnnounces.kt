package love.forte.simbot.tencentguild

import kotlinx.serialization.KSerializer
import love.forte.simbot.ID
import love.forte.simbot.tencentguild.internal.TencentAnnouncesImpl


/**
 *
 * [公告对象(Announces)](https://bot.q.qq.com/wiki/develop/api/openapi/announces/model.html#announces)
 *
 * @author ForteScarlet
 */
public interface TencentAnnounces {
    /**
     * 	频道 id
     */
    public val guildId: ID


    /**
     * 	子频道 id
     */
    public val channelId: ID

    /**
     * 	消息 id
     */
    public val messageId: ID

    public companion object {
        internal val serializer: KSerializer<out TencentAnnouncesImpl> = TencentAnnouncesImpl.serializer()
    }
}