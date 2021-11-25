package love.forte.simbot.tencentguild

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.LongID
import love.forte.simbot.tencentguild.api.TencentApiResult


/**
 *
 *  [频道对象](https://bot.q.qq.com/wiki/develop/api/openapi/guild/model.html)
 *
 * @author ForteScarlet
 */
@Serializable
public data class TencentGuild(
    /**
     * 频道ID
     */
    public val id: LongID,
    /**
     * 频道名称
     */
    public val name: String,
    /**
     * 频道头像地址
     */
    public val icon: String,
    /**
     * 创建人用户ID
     */
    @SerialName("owner_id")
    public val ownerId: String,
    /**
     * 当前人是否是创建人
     */
    @SerialName("owner")
    public val isBotOwner: Boolean,
    /**
     * 成员数
     */
    @SerialName("member_count")
    public val memberCount: Int,

    /**
     * 最大成员数
     */
    @SerialName("max_members")
    public val maxMembers: Int,

    /**
     * 描述
     */
    public val description: String,

    /**
     * 加入时间
     */
    @SerialName("joined_at")
    public val joinedAt: String,

    /**
     * 游戏绑定公会区服ID，需要特殊申请并配置后才会返回
     */
    @SerialName("union_world_id")
    public val unionWorldId: String? = null,

    /**
     * 游戏绑定公会/战队ID，需要特殊申请并配置后才会返回
     */
    @SerialName("union_org_id")
    public val unionOrgId: String? = null,
) : TencentApiResult {


}