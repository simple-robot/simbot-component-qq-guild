package love.forte.simbot.tencentguild.internal

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.LongID
import love.forte.simbot.tencentguild.TencentChannelInfo


@Serializable
internal data class TencentChannelInfoImpl(
    override val id: LongID,
    @SerialName("guild_id")
    override val guildId: LongID,

    override val name: String,
    @SerialName("type")
    override val channelTypeValue: Int,
    @SerialName("sub_type")
    override val channelSubTypeValue: Int,

    override val position: Int,
    @SerialName("parent_id")
    override val parentId: String,

    @SerialName("owner_id")
    override val ownerId: LongID,
) : TencentChannelInfo