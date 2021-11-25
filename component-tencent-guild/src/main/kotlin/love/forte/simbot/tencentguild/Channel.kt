package love.forte.simbot.tencentguild

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.LongID


@Serializable
public data class TencentChannel(
    public val id: LongID,
    @SerialName("guild_id")
    public val guildId: LongID,

    public val name: String,
    @SerialName("type")
    public val channelTypeValue: Int,
    @SerialName("sub_type")
    public val channelSubTypeValue: Int,

    public val position: Int,
    @SerialName("parent_id")
    public val parentId: String,

    @SerialName("owner_id")
    public val ownerId: String,
)

// https://bot.q.qq.com/wiki/develop/api/openapi/channel/model.html#channeltype
public enum class ChannelType {

}

// https://bot.q.qq.com/wiki/develop/api/openapi/channel/model.html#channelsubtype
public enum class ChannelSubType {

}