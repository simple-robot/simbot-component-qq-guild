package love.forte.simbot.tencentguild.internal

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.CharSequenceID
import love.forte.simbot.tencentguild.TencentChannelInfo


@Serializable
internal data class TencentChannelInfoImpl(
    override val id: CharSequenceID,
    @SerialName("guild_id")
    override val guildId: CharSequenceID,

    override val name: String,
    @SerialName("type")
    override val channelTypeValue: Int,
    @SerialName("sub_type")
    override val channelSubTypeValue: Int,

    override val position: Int,
    @SerialName("parent_id")
    override val parentId: String,

    @SerialName("owner_id")
    override val ownerId: CharSequenceID,
) : TencentChannelInfo