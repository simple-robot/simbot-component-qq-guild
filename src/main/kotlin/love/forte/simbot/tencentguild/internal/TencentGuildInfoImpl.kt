package love.forte.simbot.tencentguild.internal

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.LongID
import love.forte.simbot.tencentguild.TencentGuildInfo


/**
 *
 *  [频道对象](https://bot.q.qq.com/wiki/develop/api/openapi/guild/model.html)
 *
 * @author ForteScarlet
 */
@Serializable
internal data class TencentGuildInfoImpl(
    override val id: LongID,
    override val name: String,
    override val icon: String,
    @SerialName("owner_id")
    override val ownerId: String,
    @SerialName("owner")
    override val isBotOwner: Boolean,
    @SerialName("member_count")
    override val memberCount: Int,
    @SerialName("max_members")
    override val maxMembers: Int,
    override val description: String,
    @SerialName("joined_at")
    override val joinedAt: String,
    @SerialName("union_world_id")
    override val unionWorldId: String? = null,
    @SerialName("union_org_id")
    override val unionOrgId: String? = null,
) : TencentGuildInfo {


}