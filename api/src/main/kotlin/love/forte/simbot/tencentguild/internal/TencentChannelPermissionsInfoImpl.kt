package love.forte.simbot.tencentguild.internal

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import love.forte.simbot.ID
import love.forte.simbot.definition.PermissionStatus
import love.forte.simbot.tencentguild.Permissions
import love.forte.simbot.tencentguild.TencentChannelPermissionsInfo

/**
 *
 * @author ForteScarlet
 */
@Serializable
internal data class TencentChannelPermissionsInfoImpl(
    override val id: ID,
    @SerialName("channel_id")
    override val channelId: ID,
    @SerialName("user_id")
    override val userId: ID,
    override val permissions: Permissions
) : TencentChannelPermissionsInfo {
    @Transient
    override var status: PermissionStatus = permissions.status
        internal set

}