package love.forte.simbot.tencentguild.internal

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.CharSequenceID
import love.forte.simbot.tencentguild.TencentAnnounces


/**
 *
 * @author ForteScarlet
 */
@Serializable
internal class TencentAnnouncesImpl(
    @SerialName("guild_id")
    override val guildId: CharSequenceID,
    @SerialName("channel_id")
    override val channelId: CharSequenceID,
    @SerialName("message_id")
    override val messageId: CharSequenceID
) : TencentAnnounces